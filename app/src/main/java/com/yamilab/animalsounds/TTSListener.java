package com.yamilab.animalsounds;

/**
 * Created by Misha on 19.01.2018.
 */

interface TTSListener {
    void speak(String text, int sound);
    /** Прерывает текущую озвучку и произносит text сразу. */
    void speakNow(String text);
    void playSilence(int mseconds);

}
