package com.example.tick_tack_toe_infinity;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.ImageButton; // Изменено с MaterialButton на ImageButton
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import com.example.tick_tack_toe_infinity.game.GameEngine;

public class DifficultyMenuActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_difficulty_menu);

        // Находим кнопки в XML. Теперь они типа ImageButton
        ImageButton btnEasy = findViewById(R.id.btnEasy);
        ImageButton btnMedium = findViewById(R.id.btnMedium);
        ImageButton btnHard = findViewById(R.id.btnHard);

        // Установка слушателей нажатий
        btnEasy.setOnClickListener(v -> startGame(GameEngine.Difficulty.EASY));
        btnMedium.setOnClickListener(v -> startGame(GameEngine.Difficulty.MEDIUM));
        btnHard.setOnClickListener(v -> startGame(GameEngine.Difficulty.HARD));

        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_FULLSCREEN
        );
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // Прячет нижние кнопки
                | View.SYSTEM_UI_FLAG_FULLSCREEN      // Прячет строку состояния
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY // Делает режим "липким" (панели сами скрываются через пару секунд)
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);

        ImageButton btnBackToMenu = findViewById(R.id.btnBackToMenu);
        btnBackToMenu.setOnClickListener(v -> {
            // Просто закрывает текущее окно и возвращает на предыдущее (в главное меню)
            finish();
        });

        startCloudAnimations();
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

    /**
     * Запуск игры с выбранной сложностью
     */
    private void startGame(GameEngine.Difficulty difficulty) {
        // Создаем Intent для перехода на экран игры
        Intent intent = new Intent(this, GameActivity.class);

        // Передаем уровень сложности в GameActivity
        // GameEngine.Difficulty должен реализовывать Serializable или быть Enum
        intent.putExtra("DIFFICULTY_LEVEL", difficulty);

        // Запускаем игровой экран
        startActivity(intent);
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