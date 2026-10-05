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
                    tts = new TextToSpeech(appContext, status -> post(() -> onEngineInit(status)));
                } catch (Exception e) {
                    tts = null;
                }
            } else if (engineReady && !lang.equals(appliedLanguage)) {
                applyLanguage();
            }
        });
    }

    void speakNow(String text) {
        post(() -> {
            if (languageReady) {
                tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, "tts_speak");
            }
        });
    }

    void speak(String text) {
        post(() -> {
            if (languageReady) {
                tts.speak(text, TextToSpeech.QUEUE_ADD, null, "tts_speak");
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

    private void onEngineInit(int status) {
        if (status != TextToSpeech.SUCCESS || tts == null) {
            engineReady = false;
            languageReady = false;
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
                appliedLanguage = requestedLanguage;
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
