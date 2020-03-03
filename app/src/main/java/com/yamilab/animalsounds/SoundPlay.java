package com.yamilab.animalsounds;

import android.content.Context;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;
import android.widget.Toast;

/**
 * Created by Михаил on 31.03.2017.
 */

public class SoundPlay {
    private static SoundPool sp;




    public static void playSP(Context context, Integer sound) {

        /*
        try {
            if (sp != null) {
                sp.release();
                sp = null;
            }
        }
        catch (Exception e){

        }

         */

            /*
        try
        {
            sp.autoPause();
        }
        catch (
                Exception e
        ){}

             */
            /*
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
        */


        try {
            int soundId = getsp().load(context, sound, 1);
            //sp.play(soundId, 1, 1, 0, 0, 1);
        }
        catch (Exception e)
        {
            Toast toast = Toast.makeText(context,
                    "error", Toast.LENGTH_SHORT);
            toast.show();
        }
//
        sp.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener()
        {
            @Override
            public void onLoadComplete(SoundPool soundPool, int sampleId,int status) {
                soundPool.play(sampleId,1,1,0,0,1);
            }
        });
    }

    private static SoundPool getsp (){
        if (sp==null){
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

        }
        //sp.release();
        return sp;
    }

    public void clearSP(){
        if (sp!=null){
            sp.release();
            sp=null;
        }
    }

    public static void clearSP(Context context) {
        if (sp!=null){
            try {
                sp.release();
                sp = null;
            }
            catch (Exception e){

            }
        }
    }


}
