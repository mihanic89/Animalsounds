package com.yamilab.animalsounds;

import android.content.Context;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;

/**
 * Created by Михаил on 31.03.2017.
 */

public class SoundPlay {
    private static SoundPool sp;




    public static void playSP(Context context, Integer sound) {

        if (sp!=null){
            sp.release();
            sp=null;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

            AudioAttributes audioAttrib = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_GAME)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build();
            sp = new SoundPool.Builder().setAudioAttributes(audioAttrib).setMaxStreams(1).build();
        }
        else {

            sp = new SoundPool(1, AudioManager.STREAM_MUSIC, 0);
        }

        int soundId = sp.load(context, sound, 1);
        sp.play(soundId, 1, 1, 0, 0, 1);

//
        sp.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener()
        {
            @Override
            public void onLoadComplete(SoundPool soundPool, int sampleId,int status) {
                soundPool.play(sampleId,1,1,0,0,1);
            }
        });
    }

    public void clearSP(){
        if (sp!=null){
            sp.release();
            sp=null;
        }
    }

    public static void clearSP(Context context) {
        if (sp!=null){
            sp.release();
            sp=null;
        }
    }
}
