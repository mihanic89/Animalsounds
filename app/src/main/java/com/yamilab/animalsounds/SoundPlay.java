package com.yamilab.animalsounds;

import android.content.Context;
import android.media.AudioAttributes;
import android.media.SoundPool;
import android.util.Log;

/**
 * Created by Михаил on 31.03.2017.
 *
 * Проигрывает короткие звуки животных через лениво создаваемый общий {@link SoundPool}.
 * Слушатель загрузки регистрируется ровно один раз — при создании пула, поэтому повторные
 * вызовы {@link #playSP(Context, Integer)} не могут оставить пул без слушателя.
 */
public class SoundPlay {

    private static final String TAG = "SoundPlay";

    private static SoundPool sp;

    public static void playSP(Context context, Integer sound) {
        try {
            getsp().load(context, sound, 1);
        } catch (Exception e) {
            Log.w(TAG, "Failed to load sound " + sound, e);
        }
    }

    private static SoundPool getsp() {
        if (sp == null) {
            AudioAttributes audioAttrib = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_GAME)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build();
            sp = new SoundPool.Builder()
                    .setAudioAttributes(audioAttrib)
                    .setMaxStreams(1)
                    .build();
            sp.setOnLoadCompleteListener((soundPool, sampleId, status) -> {
                if (status == 0) {
                    soundPool.play(sampleId, 1, 1, 0, 0, 1);
                }
            });
        }
        return sp;
    }

    public static void clearSP(Context context) {
        if (sp != null) {
            try {
                sp.release();
            } catch (Exception e) {
                Log.w(TAG, "Error releasing SoundPool", e);
            } finally {
                sp = null;
            }
        }
    }
}