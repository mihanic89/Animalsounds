package com.yamilab.animalsounds;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.media.AudioAttributes;
import android.speech.tts.TextToSpeech;
import android.speech.tts.Voice;
import android.util.Log;

import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionException;

/**
 * Один {@link TextToSpeech} на весь процесс (а не на каждый экземпляр Activity).
 *
 * <ul>
 *   <li>Привязка к движку озвучки и все вызовы TTS идут в одном фоновом потоке: внутренняя
 *       блокировка TextToSpeech на главном потоке давала ANR.</li>
 *   <li>Экземпляр переживает поворот экрана, поэтому движок не перепривязывается каждый раз.</li>
 *   <li>Движок: Google TTS, если установлен (обычно звучит лучше движков производителей);
 *       если в нём нет нужного языка или он не поднялся — системный движок по умолчанию.</li>
 *   <li>Язык: язык интерфейса с регионом устройства (pt-BR, en-US, zh-TW…), при его отсутствии
 *       у движка — английский. Если нет и английского, озвучка просто отключается.</li>
 *   <li>Голос: из установленных голосов языка берётся самый качественный, а не голос по
 *       умолчанию; сетевые и нескачанные голоса не берём, чтобы не было задержки.</li>
 * </ul>
 */
final class TtsManager {

    private static final String TAG = "TtsManager";
    private static final String DEFAULT_LANGUAGE = "en";
    private static final String GOOGLE_TTS = "com.google.android.tts";
    // Отдельные слова (названия животных) для детей чуть медленнее звучат чётче.
    private static final float SPEECH_RATE = 0.9f;

    private static TtsManager instance;

    static synchronized TtsManager get(Context context) {
        if (instance == null) {
            instance = new TtsManager(context.getApplicationContext());
        }
        return instance;
    }

    private final Context appContext;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    // Все поля ниже трогаются только из executor.
    private TextToSpeech tts;
    private boolean initStarted;
    private boolean engineReady;
    private boolean languageReady;
    // Google TTS уже не подошёл (не поднялся или нет нужного языка) — до перезапуска берём
    // системный движок.
    private boolean googleRejected;
    private boolean usingGoogle;
    private String requestedLanguage = DEFAULT_LANGUAGE;
    private String appliedLanguage;
    // Последняя фраза, запрошенная до готовности движка: проигрывается, как только он готов.
    private String pendingText;
    private boolean pendingFlush;

    private TtsManager(Context appContext) {
        this.appContext = appContext;
    }

    /** Запускает инициализацию (однократно) и выставляет язык озвучки. */
    void init(String language) {
        final String lang = (language == null || language.isEmpty()) ? DEFAULT_LANGUAGE : language;
        post(() -> {
            requestedLanguage = lang;
            if (!initStarted) {
                createEngine();
            } else if (engineReady && !lang.equals(appliedLanguage)) {
                applyLanguage();
            }
        });
    }

    void speakNow(String text) {
        post(() -> speakInternal(text, true));
    }

    void speak(String text) {
        post(() -> speakInternal(text, false));
    }

    private void speakInternal(String text, boolean flush) {
        if (languageReady) {
            tts.speak(text, flush ? TextToSpeech.QUEUE_FLUSH : TextToSpeech.QUEUE_ADD, null, "tts_speak");
        } else if (initStarted) {
            pendingText = text;
            pendingFlush = flush;
        }
    }

    /** Освобождает движок (когда приложение скрыто); следующий init() привяжет его заново. */
    void release() {
        post(() -> {
            shutdownEngine();
            pendingText = null;
        });
    }

    void playSilence(int millis) {
        post(() -> {
            if (languageReady) {
                tts.playSilentUtterance(millis, TextToSpeech.QUEUE_FLUSH, "tts_silence");
            }
        });
    }

    private void createEngine() {
        initStarted = true;
        usingGoogle = !googleRejected && isGoogleTtsInstalled();
        try {
            final TextToSpeech[] holder = new TextToSpeech[1];
            TextToSpeech.OnInitListener listener = status -> post(() -> onEngineInit(holder[0], status));
            holder[0] = usingGoogle
                    ? new TextToSpeech(appContext, listener, GOOGLE_TTS)
                    : new TextToSpeech(appContext, listener);
            tts = holder[0];
        } catch (Exception e) {
            tts = null;
            initStarted = false; // повторим при следующем init()
            pendingText = null;
        }
    }

    private void shutdownEngine() {
        TextToSpeech old = tts;
        tts = null;
        initStarted = false;
        engineReady = false;
        languageReady = false;
        appliedLanguage = null;
        if (old != null) {
            old.stop();
            old.shutdown();
        }
    }

    /** Отказывается от Google TTS и сразу поднимает системный движок (фраза в ожидании сохраняется). */
    private void fallBackToDefaultEngine() {
        googleRejected = true;
        shutdownEngine();
        createEngine();
    }

    private void onEngineInit(TextToSpeech engine, int status) {
        if (engine != tts) {
            // Колбэк устаревшего экземпляра (между тем был release()): не трогаем текущее состояние.
            if (engine != null) engine.shutdown();
            return;
        }
        if (status != TextToSpeech.SUCCESS) {
            if (usingGoogle) {
                fallBackToDefaultEngine();
                return;
            }
            engineReady = false;
            languageReady = false;
            engine.shutdown();
            tts = null;
            initStarted = false; // повторим при следующем init()
            pendingText = null;
            return;
        }
        engineReady = true;
        tts.setAudioAttributes(new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_GAME)
                .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                .build());
        tts.setSpeechRate(SPEECH_RATE);
        applyLanguage();
    }

    private void applyLanguage() {
        languageReady = false;
        Locale preferred = preferredLocale(requestedLanguage);
        Locale[] candidates = requestedLanguage.equals(DEFAULT_LANGUAGE)
                ? new Locale[]{preferred}
                : new Locale[]{preferred, preferredLocale(DEFAULT_LANGUAGE)};
        for (Locale candidate : candidates) {
            int result;
            try {
                result = tts.setLanguage(candidate);
            } catch (Exception e) {
                continue;
            }
            if (result < TextToSpeech.LANG_AVAILABLE) {
                if (candidate == preferred && usingGoogle && !isDefaultEngine(GOOGLE_TTS)) {
                    // В Google TTS нет нужного языка, а пользователь выбрал другой движок —
                    // возможно, язык есть в нём. Английский запасной не берём.
                    fallBackToDefaultEngine();
                    return;
                }
                continue;
            }
            selectBestVoice(candidate);
            languageReady = true;
            // Запасной английский не запоминаем как применённый: следующий init() из
            // onStart снова попробует нужный язык (например, когда голос докачается).
            appliedLanguage = candidate == preferred ? requestedLanguage : null;
            if (pendingText != null) {
                String text = pendingText;
                pendingText = null;
                tts.speak(text, pendingFlush ? TextToSpeech.QUEUE_FLUSH : TextToSpeech.QUEUE_ADD, null, "tts_speak");
            }
            return;
        }
    }

    /**
     * Из установленных голосов языка выбирает лучший: сначала совпадение региона, потом
     * качество, потом меньшая задержка. При равенстве остаётся голос, выбранный движком.
     */
    private void selectBestVoice(Locale target) {
        try {
            Set<Voice> voices = tts.getVoices();
            if (voices == null) return;
            Voice current = tts.getVoice();
            String lang = normalize(target.getLanguage());
            Voice best = null;
            int bestScore = Integer.MIN_VALUE;
            for (Voice v : voices) {
                if (v == null || v.getLocale() == null) continue;
                if (!lang.equals(normalize(v.getLocale().getLanguage()))) continue;
                if (v.isNetworkConnectionRequired()) continue;
                Set<String> features = v.getFeatures();
                if (features != null && features.contains(TextToSpeech.Engine.KEY_FEATURE_NOT_INSTALLED)) continue;
                int score = 0;
                if (!target.getCountry().isEmpty() && target.getCountry().equals(v.getLocale().getCountry())) {
                    score += 10000;
                } else if (current != null && current.getLocale() != null
                        && current.getLocale().getCountry().equals(v.getLocale().getCountry())) {
                    // Голоса региона устройства нет — держимся региона, который выбрал движок
                    // (es-MX → es-US, а не es-ES), но берём в нём голос получше.
                    score += 5000;
                }
                score += v.getQuality() * 2;       // QUALITY_VERY_LOW(100)…QUALITY_VERY_HIGH(500)
                score -= v.getLatency() / 100;     // LATENCY_VERY_LOW(100)…VERY_HIGH(500)
                if (v.equals(current)) score += 1; // при равенстве не меняем голос движка
                if (score > bestScore) {
                    bestScore = score;
                    best = v;
                }
            }
            if (best != null && !best.equals(current)) {
                tts.setVoice(best);
            }
            Voice applied = tts.getVoice();
            Log.i(TAG, "googleEngine=" + usingGoogle
                    + " locale=" + target + " voice=" + (applied != null ? applied.getName()
                    + " q=" + applied.getQuality() : "null"));
        } catch (Exception e) {
            // голос по умолчанию для языка уже выставлен setLanguage()
        }
    }

    /** Язык интерфейса с регионом устройства, если языки совпадают (pt → pt-BR на бразильском телефоне). */
    private static Locale preferredLocale(String language) {
        Locale device = Locale.getDefault();
        if (normalize(device.getLanguage()).equals(normalize(language))) {
            return new Locale(device.getLanguage(), device.getCountry());
        }
        return new Locale(language);
    }

    /** Java на Android отдаёт старые коды языков: in = id, iw = he, no = nb. */
    private static String normalize(String language) {
        if (language == null) return "";
        switch (language) {
            case "in": return "id";
            case "iw": return "he";
            case "no": return "nb";
            default: return language;
        }
    }

    private boolean isGoogleTtsInstalled() {
        try {
            List<ResolveInfo> engines = appContext.getPackageManager().queryIntentServices(
                    new Intent(TextToSpeech.Engine.INTENT_ACTION_TTS_SERVICE), 0);
            for (ResolveInfo info : engines) {
                if (info.serviceInfo != null && GOOGLE_TTS.equals(info.serviceInfo.packageName)) {
                    return true;
                }
            }
        } catch (Exception e) {
            // нет доступа к списку движков — используем системный
        }
        return false;
    }

    private boolean isDefaultEngine(String engine) {
        try {
            return engine.equals(tts.getDefaultEngine());
        } catch (Exception e) {
            return true;
        }
    }

    private void post(Runnable action) {
        try {
            executor.execute(() -> {
                try {
                    action.run();
                } catch (Exception e) {
                    // TTS — второстепенная функция, сбой не должен ронять приложение
                }
            });
        } catch (RejectedExecutionException e) {
            // не должно случаться: executor не останавливается
        }
    }
}
