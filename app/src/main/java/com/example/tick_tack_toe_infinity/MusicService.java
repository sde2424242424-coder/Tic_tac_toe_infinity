package com.example.tick_tack_toe_infinity;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;

public class MusicService extends Service {
    private MediaPlayer mPlayer;
    private MediaPlayer mNextPlayer;

    @Override
    public IBinder onBind(Intent intent) { return null; }

    @Override
    public void onCreate() {
        super.onCreate();
        // Создаем оба плеера сразу при старте
        mPlayer = MediaPlayer.create(this, R.raw.background_music);
        mNextPlayer = MediaPlayer.create(this, R.raw.background_music);

        setupGapless();
    }

    private void setupGapless() {
        mPlayer.setVolume(0.5f, 0.5f);
        mNextPlayer.setVolume(0.5f, 0.5f);

        // Связываем их
        mPlayer.setNextMediaPlayer(mNextPlayer);

        // Когда первый доиграл, второй уже начал играть.
        // Нам нужно подготовить первый, чтобы он стал "следующим" для второго.
        mPlayer.setOnCompletionListener(mp -> {
            // Важно: НЕ вызываем mp.release() здесь!
            // Просто говорим второму, что его следующим будет этот (уже замолчавший)
            mNextPlayer.setNextMediaPlayer(mPlayer);
        });

        mNextPlayer.setOnCompletionListener(mp -> {
            // И наоборот
            mPlayer.setNextMediaPlayer(mNextPlayer);
        });
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (!mPlayer.isPlaying()) mPlayer.start();
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        if (mPlayer != null) { mPlayer.release(); mPlayer = null; }
        if (mNextPlayer != null) { mNextPlayer.release(); mNextPlayer = null; }
        super.onDestroy();
    }
}