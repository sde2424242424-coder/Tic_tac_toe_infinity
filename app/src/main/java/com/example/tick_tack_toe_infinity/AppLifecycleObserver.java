package com.example.tick_tack_toe_infinity;

import android.content.Context;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.lifecycle.DefaultLifecycleObserver;
import androidx.lifecycle.LifecycleOwner;

public class AppLifecycleObserver implements DefaultLifecycleObserver {

    private final Context context;

    public AppLifecycleObserver(Context context) {
        this.context = context.getApplicationContext();
    }

    @Override
    public void onStart(@NonNull LifecycleOwner owner) {
        // Приложение вернулось на передний план — запускаем музыку
        context.startService(new Intent(context, MusicService.class));
    }

    @Override
    public void onStop(@NonNull LifecycleOwner owner) {
        // Приложение ушло в фон (нажат Home или переключено на другое приложение)
        context.stopService(new Intent(context, MusicService.class));
    }
}