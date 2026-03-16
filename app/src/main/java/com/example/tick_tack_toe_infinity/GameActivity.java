package com.example.tick_tack_toe_infinity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.tick_tack_toe_infinity.game.GameEngine;
import com.google.android.material.button.MaterialButton;

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

        setupUI();
        resetGame();
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
                    tvStatus.setText(current == 'X' ? "Your turn (Green)" : "Computer thinking...");
                } else {
                    tvStatus.setText(current == 'X' ? "Turn: Player 1 (Green)" : "Turn: Player 2 (Blue)");
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
}