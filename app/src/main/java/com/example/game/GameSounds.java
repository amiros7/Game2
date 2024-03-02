package com.example.game;

import android.content.Context;
import android.media.SoundPool;

public class GameSounds {

    private SoundPool soundPool;

    private int bang;

    public GameSounds(Context context) {
        soundPool = new SoundPool.Builder()
                .setMaxStreams(3)
                .build();
        bang = soundPool.load(context, R.raw.crash, 1);

    }

    public void bang() {
        soundPool.play(bang, 1.0f, 1.0f, 1, 0, 1.0f);
    }
}
