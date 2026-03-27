package com.example.tick_tack_toe_infinity;

import android.app.Application;
import androidx.lifecycle.ProcessLifecycleOwner;

public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        // Подключаем нашего наблюдателя за жизненным циклом всего приложения
        AppLifecycleObserver observer = new AppLifecycleObserver(this);
        ProcessLifecycleOwner.get().getLifecycle().addObserver(observer);
    }
}