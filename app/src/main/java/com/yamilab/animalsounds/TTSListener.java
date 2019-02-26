package com.yamilab.animalsounds;

/**
 * Created by Misha on 19.01.2018.
 */

interface TTSListener {
    void speak(String text, int sound);
    void playSilence(int mseconds);

}
