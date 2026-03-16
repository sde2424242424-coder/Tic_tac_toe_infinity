package com.example.tick_tack_toe_infinity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import androidx.appcompat.app.AppCompatActivity;

public class MenuActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Устанавливаем макет с общей панелью и двумя кнопками
        setContentView(R.layout.activity_menu);

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

        // Обработка нажатия: Игра с другом
        // Ведем пользователя сразу в игру (GameActivity), так как сложность не нужна
        btnVsFriend.setOnClickListener(v -> {
            Intent intent = new Intent(MenuActivity.this, GameActivity.class);
            // Передаем информацию о режиме игры
            intent.putExtra("GAME_MODE", "FRIEND");
            startActivity(intent);
        });
    }
}