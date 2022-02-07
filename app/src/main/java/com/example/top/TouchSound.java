package com.example.top;
import android.content.DialogInterface;
import android.media.AudioAttributes;
import android.media.SoundPool;
import android.media.AudioManager;
import android.content.Context;

public class TouchSound {
    public SoundPool pool;
    public int soundOne;
    public int soundTwo;
    public int soundThree;

    public TouchSound (Context context) {
        AudioAttributes attr = new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_MEDIA)
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                .build();
        pool = new SoundPool.Builder()
                .setAudioAttributes(attr)
                .setMaxStreams(1)
                .build();

        soundOne = pool.load(context, R.raw.sound1, 1);
        soundTwo = pool.load(context, R.raw.tm2_pon001, 1);
        soundThree = pool.load(context, R.raw.error, 1);


    }

    public void playsoundOne() {
        pool.play(soundOne, 1.0f, 1.0f, 1, 0, 1.0f);
    }
    public  void playsoundTwo() {pool.play(soundTwo, 1.0f, 1.0f, 1, 0, 1.0f);}
    public  void playsoundThree() {pool.play(soundThree, 1.0f, 1.0f, 1, 0, 1.0f);}

}
