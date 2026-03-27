// Файл: game/GameEngine.java
package com.example.tick_tack_toe_infinity.game;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class GameEngine {

    // --- Классы и перечисления ---

    public static class Move {
        public int row, col;
        public char player;

        public Move(int row, int col, char player) {
            this.row = row;
            this.col = col;
            this.player = player;
        }
    }

    // ДОБАВЛЕНО: Уровни сложности
    public enum Difficulty {
        EASY,
        MEDIUM,
        HARD
    }

    public enum GameState {
        IN_PROGRESS,
        X_WINS,
        O_WINS,
        DRAW
    }

    // --- Поля класса ---

    private static final int MAX_MOVES = 6;
    private final LinkedList<Move> moves = new LinkedList<>();

    private final char[][] board;
    private static final int BOARD_SIZE = 3; // Для ИИ лучше всего работает поле 3x3

    private char currentPlayer;
    private GameState gameState;
    private final Random random;

    // ДОБАВЛЕНО: Текущий уровень сложности
    private Difficulty currentDifficulty = Difficulty.EASY; // По умолчанию легкий

    // --- Конструктор и основные методы ---

    public GameEngine() {
        this.board = new char[BOARD_SIZE][BOARD_SIZE];
        this.random = new Random();
        reset();
    }

    public char[][] getBoard() {
        return board;
    }

    // ДОБАВЛЕНО: Метод для установки уровня сложности извне (из меню)
    public void setDifficulty(Difficulty difficulty) {
        this.currentDifficulty = difficulty;
    }

    public void reset() {
        moves.clear();
        for (int r = 0; r < BOARD_SIZE; r++) {
            for (int c = 0; c < BOARD_SIZE; c++) {
                board[r][c] = ' ';
            }
        }
        currentPlayer = 'X';
        gameState = GameState.IN_PROGRESS;
    }

    public boolean makeMove(int row, int col) {
        if (row < 0 || row >= BOARD_SIZE || col < 0 || col >= BOARD_SIZE || board[row][col] != ' ' || gameState != GameState.IN_PROGRESS) {
            return false;
        }

        Move newMove = new Move(row, col, currentPlayer);
        moves.add(newMove);
        board[row][col] = currentPlayer;

        if (moves.size() > MAX_MOVES) {
            Move oldestMove = moves.removeFirst();
            board[oldestMove.row][oldestMove.col] = ' ';
        }

        updateGameState(newMove);
        if (gameState == GameState.IN_PROGRESS) {
            switchPlayer();
        }
        return true;
    }

    // --- Логика Искусственного Интеллекта ---

    /**
     * Главный метод для получения хода компьютера.
     * Выбирает стратегию в зависимости от уровня сложности.
     */
    public int[] getComputerMove() {
        switch (currentDifficulty) {
            case MEDIUM:
                return getMediumMove();
            case HARD:
                // Для поля 3x3 минимакс непобедим
                return (BOARD_SIZE == 3) ? getHardMoveMinimax() : getMediumMove();
            case EASY:
            default:
                return getEasyMove();
        }
    }

    // УРОВЕНЬ: ЛЕГКИЙ (случайный ход)
    private int[] getEasyMove() {
        List<int[]> emptyCells = new ArrayList<>();
        for (int r = 0; r < BOARD_SIZE; r++) {
            for (int c = 0; c < BOARD_SIZE; c++) {
                if (board[r][c] == ' ') {
                    emptyCells.add(new int[]{r, c});
                }
            }
        }
        if (emptyCells.isEmpty()) return null;
        return emptyCells.get(random.nextInt(emptyCells.size()));
    }

    // УРОВЕНЬ: СРЕДНИЙ (пытается выиграть или блокировать)
    private int[] getMediumMove() {
        // 1. Проверить, может ли компьютер выиграть следующим ходом
        int[] winningMove = findWinningMove('O');
        if (winningMove != null) return winningMove;

        // 2. Проверить, может ли игрок выиграть следующим ходом, и заблокировать его
        int[] blockingMove = findWinningMove('X');
        if (blockingMove != null) return blockingMove;

        // 3. Если ничего из вышеперечисленного, сделать случайный ход
        return getEasyMove();
    }

    // Вспомогательный метод для СРЕДНЕГО уровня
    private int[] findWinningMove(char player) {
        for (int r = 0; r < BOARD_SIZE; r++) {
            for (int c = 0; c < BOARD_SIZE; c++) {
                if (board[r][c] == ' ') {
                    board[r][c] = player; // Попробовать сделать ход
                    boolean isWinning = checkWinner(player, r, c);
                    board[r][c] = ' '; // Отменить ход
                    if (isWinning) {
                        return new int[]{r, c};
                    }
                }
            }
        }
        return null;
    }

    // УРОВЕНЬ: СЛОЖНЫЙ (алгоритм Минимакс для поля 3x3)
    private int[] getHardMoveMinimax() {
        int[] bestMove = {-1, -1};
        int bestScore = Integer.MIN_VALUE;

        for (int r = 0; r < BOARD_SIZE; r++) {
            for (int c = 0; c < BOARD_SIZE; c++) {
                if (board[r][c] == ' ') {
                    board[r][c] = 'O'; // Сделать ход
                    int score = minimax(0, false);
                    board[r][c] = ' '; // Отменить ход
                    if (score > bestScore) {
                        bestScore = score;
                        bestMove[0] = r;
                        bestMove[1] = c;
                    }
                }
            }
        }
        return bestMove[0] == -1 ? getEasyMove() : bestMove; // Если что-то пошло не так
    }

    // Рекурсивная функция Минимакс
    private int minimax(int depth, boolean isMaximizing) {
        // Проверяем, закончилась ли игра и возвращаем счет
        if (checkWinner('O', -1, -1)) return 10 - depth; // Победа ИИ
        if (checkWinner('X', -1, -1)) return depth - 10; // Победа игрока
        if (isBoardFull()) return 0; // Ничья

        if (isMaximizing) { // Ход ИИ (максимизируем счет)
            int bestScore = Integer.MIN_VALUE;
            for (int r = 0; r < BOARD_SIZE; r++) {
                for (int c = 0; c < BOARD_SIZE; c++) {
                    if (board[r][c] == ' ') {
                        board[r][c] = 'O';
                        bestScore = Math.max(bestScore, minimax(depth + 1, false));
                        board[r][c] = ' ';
                    }
                }
            }
            return bestScore;
        } else { // Ход игрока (минимизируем счет)
            int bestScore = Integer.MAX_VALUE;
            for (int r = 0; r < BOARD_SIZE; r++) {
                for (int c = 0; c < BOARD_SIZE; c++) {
                    if (board[r][c] == ' ') {
                        board[r][c] = 'X';
                        bestScore = Math.min(bestScore, minimax(depth + 1, true));
                        board[r][c] = ' ';
                    }
                }
            }
            return bestScore;
        }
    }

    // Вспомогательный метод для Минимакса
    private boolean isBoardFull() {
        for (int r = 0; r < BOARD_SIZE; r++) {
            for (int c = 0; c < BOARD_SIZE; c++) {
                if (board[r][c] == ' ') return false;
            }
        }
        return true;
    }


    // --- Остальные методы (проверка победителя, отмена хода и т.д.) ---

    // ... (весь остальной ваш код: undoLastMove, updateGameState, checkWinner и т.д. остается без изменений) ...

    public void undoLastMove() {
        if (moves.isEmpty() || gameState != GameState.IN_PROGRESS) {
            return;
        }
        Move lastAiMove = moves.removeLast();
        board[lastAiMove.row][lastAiMove.col] = ' ';
        if (gameState == GameState.IN_PROGRESS) switchPlayer();

        if (!moves.isEmpty()) {
            Move lastPlayerMove = moves.removeLast();
            board[lastPlayerMove.row][lastPlayerMove.col] = ' ';
            if (gameState == GameState.IN_PROGRESS) switchPlayer();
        }
    }


    private void updateGameState(Move lastMove) {
        if (checkWinner(lastMove.player, lastMove.row, lastMove.col)) {
            gameState = (lastMove.player == 'X') ? GameState.X_WINS : GameState.O_WINS;
        } else if (isBoardFull() && moves.size() < MAX_MOVES) { // Ничья только если поле заполнено до механики удаления
            gameState = GameState.DRAW;
        }
    }

    private boolean checkWinner(char player, int row, int col) {
        // Проверка на выигрыш по всему полю (нужна для Минимакса)
        // Горизонтали и вертикали
        for (int i = 0; i < BOARD_SIZE; i++) {
            boolean rowWin = true, colWin = true;
            for (int j = 0; j < BOARD_SIZE; j++) {
                if (board[i][j] != player) rowWin = false;
                if (board[j][i] != player) colWin = false;
            }
            if (rowWin || colWin) return true;
        }

        // Диагонали
        boolean diag1Win = true, diag2Win = true;
        for (int i = 0; i < BOARD_SIZE; i++) {
            if (board[i][i] != player) diag1Win = false;
            if (board[i][BOARD_SIZE - 1 - i] != player) diag2Win = false;
        }
        return diag1Win || diag2Win;
    }


    public void switchPlayer() {
        currentPlayer = (currentPlayer == 'X') ? 'O' : 'X';
    }

    public char getCell(int row, int col) {
        return board[row][col];
    }

    public GameState getGameState() {
        return gameState;
    }

    public char getCurrentPlayer() {
        return currentPlayer;
    }

    public int getBoardSize() {
        return BOARD_SIZE;
    }

    public Move getOldestMove() {
        // Предупреждение показываем только тогда, когда следующий ход приведет к удалению.
        if (moves.size() == MAX_MOVES) {
            return moves.peekFirst(); // peekFirst() получает первый элемент, не удаляя его.
        }
        return null;
    }
}

