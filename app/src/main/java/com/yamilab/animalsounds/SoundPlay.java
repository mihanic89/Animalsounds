package com.yamilab.animalsounds;

import android.content.Context;
import android.media.AudioAttributes;
import android.media.SoundPool;
import android.util.Log;
import android.util.SparseIntArray;

/**
 * Created by Михаил on 31.03.2017.
 *
 * Проигрывает короткие звуки животных через лениво создаваемый общий {@link SoundPool}.
 * Слушатель загрузки регистрируется ровно один раз — при создании пула, поэтому повторные
 * вызовы {@link #playSP(Context, Integer)} не могут оставить пул без слушателя.
 *
 * Загруженные сэмплы кешируются по resource id: без этого каждый повторный тап по тому же
 * животному заново декодировал звук и оставлял в нативной памяти ещё одну загруженную копию
 * на всё время жизни пула (утечка + лишняя задержка перед воспроизведением).
 */
public class SoundPlay {

    private static final String TAG = "SoundPlay";

    private static SoundPool sp;
    private static final SparseIntArray loadedSoundIds = new SparseIntArray();

    public static void playSP(Context context, Integer sound) {
        try {
            SoundPool pool = getsp();
            int cachedId = loadedSoundIds.get(sound, 0);
            if (cachedId != 0) {
                pool.play(cachedId, 1, 1, 0, 0, 1);
            } else {
                int soundId = pool.load(context, sound, 1);
                loadedSoundIds.put(sound, soundId);
            }
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
                loadedSoundIds.clear();
            }
        }
    }
}