package com.yamilab.animalsounds;

import android.content.Context;
import android.media.AudioAttributes;
import android.speech.tts.TextToSpeech;

import java.util.Locale;
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
 *   <li>Язык: сначала язык интерфейса, при его отсутствии у движка — английский. Если нет и
 *       английского, озвучка просто отключается.</li>
 * </ul>
 */
final class TtsManager {

    private static final String DEFAULT_LANGUAGE = "en";

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
                initStarted = true;
                try {
                    final TextToSpeech[] holder = new TextToSpeech[1];
                    holder[0] = new TextToSpeech(appContext,
                            status -> post(() -> onEngineInit(holder[0], status)));
                    tts = holder[0];
                } catch (Exception e) {
                    tts = null;
                    initStarted = false; // повторим при следующем init()
                    pendingText = null;
                }
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
            TextToSpeech old = tts;
            tts = null;
            initStarted = false;
            engineReady = false;
            languageReady = false;
            appliedLanguage = null;
            pendingText = null;
            if (old != null) {
                old.stop();
                old.shutdown();
            }
        });
    }

    void playSilence(int millis) {
        post(() -> {
            if (languageReady) {
                tts.playSilentUtterance(millis, TextToSpeech.QUEUE_FLUSH, "tts_silence");
            }
        });
    }

    private void onEngineInit(TextToSpeech engine, int status) {
        if (engine != tts) {
            // Колбэк устаревшего экземпляра (между тем был release()): не трогаем текущее состояние.
            if (engine != null) engine.shutdown();
            return;
        }
        if (status != TextToSpeech.SUCCESS) {
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
        applyLanguage();
    }

    private void applyLanguage() {
        languageReady = false;
        String[] candidates = requestedLanguage.equals(DEFAULT_LANGUAGE)
                ? new String[]{DEFAULT_LANGUAGE}
                : new String[]{requestedLanguage, DEFAULT_LANGUAGE};
        for (String candidate : candidates) {
            int result;
            try {
                result = tts.setLanguage(new Locale(candidate));
            } catch (Exception e) {
                continue;
            }
            if (result >= TextToSpeech.LANG_AVAILABLE) {
                languageReady = true;
                // Запасной английский не запоминаем как применённый: следующий init() из
                // onStart снова попробует нужный язык (например, когда голос докачается).
                appliedLanguage = candidate.equals(requestedLanguage) ? requestedLanguage : null;
                if (pendingText != null) {
                    String text = pendingText;
                    pendingText = null;
                    tts.speak(text, pendingFlush ? TextToSpeech.QUEUE_FLUSH : TextToSpeech.QUEUE_ADD, null, "tts_speak");
                }
                return;
            }
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
