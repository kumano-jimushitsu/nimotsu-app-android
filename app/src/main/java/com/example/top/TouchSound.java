package com.example.top;

import android.content.Context;
import android.media.AudioAttributes;
import android.media.SoundPool;

public class TouchSound {
    public SoundPool pool;
    public int soundOne;
    public int soundTwo;
    public int soundThree;
    public int sound_ooo1;
    public int sound_ooo2;
    public int sound_ooo3;
    public int sound_scan;
    public int sound_555_1;
    public int sound_555_2;
    public int sound_555_3;
    public int sound_555_pico;
    public int sound_555_complete;
    public int sound_cursor2;
    public int sound_transition;
    public int sound_search;

    public TouchSound(Context context) {
        AudioAttributes attr = new AudioAttributes.Builder().setUsage(AudioAttributes.USAGE_MEDIA).setContentType(AudioAttributes.CONTENT_TYPE_MUSIC).build();
        pool = new SoundPool.Builder().setAudioAttributes(attr).setMaxStreams(1).build();

        soundOne = pool.load(context, R.raw.cursor, 2);
        soundTwo = pool.load(context, R.raw.cursor, 2);
        soundThree = pool.load(context, R.raw.error, 2);
        sound_ooo1 = pool.load(context, R.raw.scan1, 2);
        sound_ooo2 = pool.load(context, R.raw.scan2, 2);
        sound_ooo3 = pool.load(context, R.raw.scan3, 2);
        sound_scan = pool.load(context, R.raw.done, 2);
        sound_555_1 = pool.load(context, R.raw.faiz1, 2);
        sound_555_2 = pool.load(context, R.raw.faiz2, 2);
        sound_555_3 = pool.load(context, R.raw.faiz3, 2);
        sound_555_pico = pool.load(context, R.raw.faiz_pico, 2);
        sound_555_complete = pool.load(context, R.raw.done, 2);
        sound_cursor2 = pool.load(context, R.raw.cursor2, 2);
        sound_transition = pool.load(context, R.raw.transition, 2);
        sound_search = pool.load(context, R.raw.search, 2);


    }

    public void playsoundOne() {
        pool.play(soundOne, 1.0f, 1.0f, 2, 0, 1.0f);
    }

    public void playsoundTwo() {
        pool.play(soundTwo, 1.0f, 1.0f, 2, 0, 1.0f);
    }

    public void playsounderror() {
        pool.play(soundThree, 1.0f, 1.0f, 2, 0, 1.0f);
    }

    public void registercursorblock() {
        pool.play(sound_cursor2, 1.0f, 1.0f, 2, 0, 1.0f);
    }

    public void registercursorroom() {
        pool.play(sound_cursor2, 1.0f, 1.0f, 2, 0, 1.1f);
    }

    public void registercursorryosei() {
        pool.play(sound_cursor2, 1.0f, 1.0f, 2, 0, 1.2f);
    }

    public void playsoundone() {
        pool.play(sound_scan, 1.0f, 1.0f, 2, 0, 1.0f);
    }

    public void releasecursorblock() {
        pool.play(sound_cursor2, 1.0f, 1.0f, 2, 0, 1.0f);
    }

    public void releasecursorroom() {
        pool.play(sound_cursor2, 1.0f, 1.0f, 2, 0, 1.1f);
    }

    public void releasecursorryosei() {
        pool.play(sound_cursor2, 1.0f, 1.0f, 2, 0, 1.2f);
    }

    public void playsound555pico() {
        pool.play(sound_555_pico, 1.0f, 1.0f, 2, 0, 1.0f);
    }

    public void playsound555complete() {
        pool.play(sound_555_complete, 1.0f, 1.0f, 2, 0, 1.0f);
    }

    public void playsoundtransition() {
        pool.play(sound_transition, 1.0f, 1.0f, 2, 0, 1.0f);
    }

    public void playsoundsearch() {
        pool.play(sound_search, 1.0f, 1.0f, 2, 0, 1.0f);
    }

}
