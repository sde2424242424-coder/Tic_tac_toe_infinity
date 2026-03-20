package com.example.tick_tack_toe_infinity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import androidx.appcompat.app.AppCompatActivity;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;

public class MenuActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Устанавливаем макет с общей панелью и двумя кнопками
        setContentView(R.layout.activity_menu);
        startService(new Intent(this, MusicService.class));

        // 1. Находим кнопку "Игра с компьютером" (верхняя дощечка)
        ImageButton btnVsAi = findViewById(R.id.btnVsAi);

        // 2. Находим кнопку "Игра с другом" (нижняя дощечка)
        ImageButton btnVsFriend = findViewById(R.id.btnVsFriend);

        // Обработка нажатия: Игра с роботом
        // Ведем пользователя на экран выбора сложности (DifficultyMenuActivity)
        btnVsAi.setOnClickListener(v -> {
            Intent intent = new Intent(MenuActivity.this, DifficultyMenuActivity.class);
            // Передаем информацию о режиме игры
            intent.putExtra("GAME_MODE", "AI");
            startActivity(intent);
        });

        ImageButton btnExitApp = findViewById(R.id.btnExitApp);btnExitApp.setOnClickListener(v -> {
            // Полностью закрывает приложение
            finishAffinity();
            System.exit(0);
        });

        startCloudAnimations();

        // Обработка нажатия: Игра с другом
        // Ведем пользователя сразу в игру (GameActivity), так как сложность не нужна
        btnVsFriend.setOnClickListener(v -> {
            Intent intent = new Intent(MenuActivity.this, GameActivity.class);
            // Передаем информацию о режиме игры
            intent.putExtra("GAME_MODE", "FRIEND");
            startActivity(intent);
        });

        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_FULLSCREEN
        );

        // Внутри onCreate
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // Прячет нижние кнопки
                | View.SYSTEM_UI_FLAG_FULLSCREEN      // Прячет строку состояния
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY // Делает режим "липким" (панели сами скрываются через пару секунд)
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }
    }

    private void startCloudAnimations() {
        ImageView cloud1 = findViewById(R.id.cloud1);
        ImageView cloud2 = findViewById(R.id.cloud2);
        ImageView cloud3 = findViewById(R.id.cloud3);

        // Параметры: (объект, свойство, откуда, куда)
        // Чтобы плыли справа налево: от 0 до -(ширина экрана + ширина облака)
        animateCloud(cloud1, 20000, 0);  // 25 секунд
        animateCloud(cloud2, 20000, 16000); // 35 секунд, задержка 2 сек
        animateCloud(cloud3, 20000, 8000); // 20 секунд, задержка 5 сек
    }

    private void animateCloud(ImageView cloud, int duration, int delay) {
        // Получаем ширину экрана
        float screenWidth = getResources().getDisplayMetrics().widthPixels;

        // Анимация перемещения по X
        // Начинаем чуть правее экрана (screenWidth) и двигаемся до левого края (-300)
        ObjectAnimator animator = ObjectAnimator.ofFloat(cloud, "translationX", 0f, -(screenWidth + 700f));

        animator.setDuration(duration);
        animator.setStartDelay(delay);
        animator.setInterpolator(new LinearInterpolator()); // Равномерная скорость
        animator.setRepeatCount(ValueAnimator.INFINITE);    // Бесконечно
        animator.setRepeatMode(ValueAnimator.RESTART);      // Сначала

        animator.start();
    }
}