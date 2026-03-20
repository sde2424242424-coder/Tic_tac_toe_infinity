package com.example.tick_tack_toe_infinity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.tick_tack_toe_infinity.game.GameEngine;
import com.google.android.material.button.MaterialButton;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;

public class GameActivity extends AppCompatActivity {

    private GameEngine gameEngine;
    private TextView tvStatus;
    private BoardTouchView boardTouchView;

    private boolean isVsAi = true;
    private int turnCount = 0; // Счетчик ходов для анимации горения

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        gameEngine = new GameEngine();

        Intent intent = getIntent();
        if (intent != null) {
            String mode = intent.getStringExtra("GAME_MODE");
            if ("FRIEND".equals(mode)) {
                isVsAi = false;
            }

            if (isVsAi && intent.hasExtra("DIFFICULTY_LEVEL")) {
                GameEngine.Difficulty difficulty = (GameEngine.Difficulty) intent.getSerializableExtra("DIFFICULTY_LEVEL");
                gameEngine.setDifficulty(difficulty);
            }
        }

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

        setupUI();
        resetGame();
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

    private void setupUI() {
        tvStatus = findViewById(R.id.tvStatus);
        boardTouchView = findViewById(R.id.boardTouchView);

        MaterialButton btnNewGame = findViewById(R.id.btnNewGame);
        btnNewGame.setOnClickListener(v -> resetGame());

        MaterialButton btnExit = findViewById(R.id.btnExit);
        btnExit.setOnClickListener(v -> finish());

        boardTouchView.setOnCellTouchListener((row, col) -> {
            handlePlayerMove(row, col);
        });
    }

    private void handlePlayerMove(int row, int col) {
        if (gameEngine.getGameState() != GameEngine.GameState.IN_PROGRESS) return;

        if (gameEngine.makeMove(row, col)) {
            turnCount++; // Увеличиваем счетчик после хода игрока
            updateUI();

            if (isVsAi && gameEngine.getGameState() == GameEngine.GameState.IN_PROGRESS) {
                tvStatus.setText("Computer thinking...");
                boardTouchView.postDelayed(this::computerTurn, 600);
            }
        }
    }

    private void computerTurn() {
        if (gameEngine.getGameState() != GameEngine.GameState.IN_PROGRESS) return;

        int[] move = gameEngine.getComputerMove();
        if (move != null) {
            if (gameEngine.makeMove(move[0], move[1])) {
                turnCount++; // Увеличиваем счетчик после хода компьютера
            }
        }
        updateUI();
    }

    private void updateUI() {
        // Передаем и поле, и текущий номер хода
        boardTouchView.updateBoardState(gameEngine.getBoard(), turnCount);
        updateStatus();
    }

    private void updateStatus() {
        GameEngine.GameState state = gameEngine.getGameState();
        char current = gameEngine.getCurrentPlayer();

        switch (state) {
            case IN_PROGRESS:
                if (isVsAi) {
                    tvStatus.setText(current == 'X' ? "Your turn (Yellow)" : "Computer thinking...");
                } else {
                    tvStatus.setText(current == 'X' ? "Turn: Player 1 (Yellow)" : "Turn: Player 2 (Blue)");
                }
                break;
            case X_WINS:
                tvStatus.setText(isVsAi ? "You won!" : "Player 1 won!");
                break;
            case O_WINS:
                tvStatus.setText(isVsAi ? "Computer won!" : "Player 2 won!");
                break;
            case DRAW:
                tvStatus.setText("It's a draw!");
                break;
        }
    }

    private void resetGame() {
        gameEngine.reset();
        turnCount = 0; // Сброс счетчика при новой игре
        updateUI();
    }

    private void startCloudAnimations() {
        ImageView cloud1 = findViewById(R.id.cloud1);
        ImageView cloud2 = findViewById(R.id.cloud2);
        ImageView cloud3 = findViewById(R.id.cloud3);

        // Параметры: (объект, свойство, откуда, куда)
        // Чтобы плыли справа налево: от 0 до -(ширина экрана + ширина облака)
        animateCloud(cloud1, 20000, 0);  // 25 секунд
        animateCloud(cloud2, 20000, 6000); // 35 секунд, задержка 2 сек
        animateCloud(cloud3, 20000, 12000); // 20 секунд, задержка 5 сек
    }

    private void animateCloud(ImageView cloud, int duration, int delay) {
        // Получаем ширину экрана
        float screenWidth = getResources().getDisplayMetrics().widthPixels;

        // Анимация перемещения по X
        // Начинаем чуть правее экрана (screenWidth) и двигаемся до левого края (-300)
        ObjectAnimator animator = ObjectAnimator.ofFloat(cloud, "translationX", 0f, -(screenWidth + 500f));

        animator.setDuration(duration);
        animator.setStartDelay(delay);
        animator.setInterpolator(new LinearInterpolator()); // Равномерная скорость
        animator.setRepeatCount(ValueAnimator.INFINITE);    // Бесконечно
        animator.setRepeatMode(ValueAnimator.RESTART);      // Сначала

        animator.start();
    }
}