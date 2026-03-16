package com.example.tick_tack_toe_infinity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton; // Изменено с MaterialButton на ImageButton
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
}