package com.example.tick_tack_toe_infinity;

import android.content.Context;
import android.graphics.*;
import android.graphics.drawable.Drawable;
import android.media.AudioAttributes;
import android.media.SoundPool;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import androidx.core.content.ContextCompat;

public class BoardTouchView extends View {

    // Звук
    private SoundPool soundPool;
    private int explosionSoundId;

    public interface OnCellTouchListener {
        void onCellTouched(int row, int col);
    }

    private OnCellTouchListener listener;
    private final Region[][] cellRegions = new Region[3][3];
    private char[][] boardData = new char[3][3];

    private final int[][] placementTurn = new int[3][3];
    private int currentGlobalTurn = 0;

    // Обычные бомбы: [Ряд][Стадия 0-2][Кадр 0-1]
    private final Drawable[][][] greenBombFrames = new Drawable[3][3][2];
    private final Drawable[][][] blueBombFrames = new Drawable[3][3][2];

    // Анимация исчезновения: [Ряд][Кадр 0-1]
    private final Drawable[][] greenDisappearFrames = new Drawable[3][2];
    private final Drawable[][] blueDisappearFrames = new Drawable[3][2];

    // Состояние анимации исчезновения для каждой клетки
    private final int[][] disappearFrame = new int[3][3];
    private final char[][] disappearType = new char[3][3];

    private int animationTick = 0;
    private final Handler animationHandler = new Handler(Looper.getMainLooper());
    private final Runnable animationRunnable = new Runnable() {
        @Override
        public void run() {
            animationTick = (animationTick == 0) ? 1 : 0;
            invalidate();
            animationHandler.postDelayed(this, 300);
        }
    };

    public BoardTouchView(Context context, AttributeSet attrs) {
        super(context, attrs);

        // Настройка звука
        AudioAttributes audioAttributes = new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_GAME)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build();
        soundPool = new SoundPool.Builder()
                .setMaxStreams(5)
                .setAudioAttributes(audioAttributes)
                .build();
        explosionSoundId = soundPool.load(context, R.raw.explosion, 1);

        loadResources(context);

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                boardData[i][j] = ' ';
                placementTurn[i][j] = -1;
                disappearFrame[i][j] = -1;
            }
        }
        animationHandler.post(animationRunnable);
    }

    private void loadResources(Context context) {
        // --- ЗЕЛЕНЫЕ БОМБЫ (X) ---
        // Ряд 0 (Дальний)
        greenBombFrames[0][0][0] = ContextCompat.getDrawable(context, R.drawable.green_bomb_row03_step01_fr01);
        greenBombFrames[0][0][1] = ContextCompat.getDrawable(context, R.drawable.green_bomb_row03_step01_fr02);
        greenBombFrames[0][1][0] = ContextCompat.getDrawable(context, R.drawable.green_bomb_row03_step02_fr01);
        greenBombFrames[0][1][1] = ContextCompat.getDrawable(context, R.drawable.green_bomb_row03_step02_fr02);
        greenBombFrames[0][2][0] = ContextCompat.getDrawable(context, R.drawable.green_bomb_row03_step03_fr01);
        greenBombFrames[0][2][1] = ContextCompat.getDrawable(context, R.drawable.green_bomb_row03_step03_fr02);
        // Ряд 1 (Средний)
        greenBombFrames[1][0][0] = ContextCompat.getDrawable(context, R.drawable.green_bomb_row02_step01_fr01);
        greenBombFrames[1][0][1] = ContextCompat.getDrawable(context, R.drawable.green_bomb_row02_step01_fr02);
        greenBombFrames[1][1][0] = ContextCompat.getDrawable(context, R.drawable.green_bomb_row02_step02_fr01);
        greenBombFrames[1][1][1] = ContextCompat.getDrawable(context, R.drawable.green_bomb_row02_step02_fr02);
        greenBombFrames[1][2][0] = ContextCompat.getDrawable(context, R.drawable.green_bomb_row02_step03_fr01);
        greenBombFrames[1][2][1] = ContextCompat.getDrawable(context, R.drawable.green_bomb_row02_step03_fr02);
        // Ряд 2 (Ближний)
        greenBombFrames[2][0][0] = ContextCompat.getDrawable(context, R.drawable.green_bomb_row01_step01_fr01);
        greenBombFrames[2][0][1] = ContextCompat.getDrawable(context, R.drawable.green_bomb_row01_step01_fr02);
        greenBombFrames[2][1][0] = ContextCompat.getDrawable(context, R.drawable.green_bomb_row01_step02_fr01);
        greenBombFrames[2][1][1] = ContextCompat.getDrawable(context, R.drawable.green_bomb_row01_step02_fr02);
        greenBombFrames[2][2][0] = ContextCompat.getDrawable(context, R.drawable.green_bomb_row01_step03_fr01);
        greenBombFrames[2][2][1] = ContextCompat.getDrawable(context, R.drawable.green_bomb_row01_step03_fr02);

        // --- СИНИЕ БОМБЫ (O) ---
        // Ряд 0
        blueBombFrames[0][0][0] = ContextCompat.getDrawable(context, R.drawable.blue_bomb_row03_step01_fr01);
        blueBombFrames[0][0][1] = ContextCompat.getDrawable(context, R.drawable.blue_bomb_row03_step01_fr02);
        blueBombFrames[0][1][0] = ContextCompat.getDrawable(context, R.drawable.blue_bomb_row03_step02_fr01);
        blueBombFrames[0][1][1] = ContextCompat.getDrawable(context, R.drawable.blue_bomb_row03_step02_fr02);
        blueBombFrames[0][2][0] = ContextCompat.getDrawable(context, R.drawable.blue_bomb_row03_step03_fr01);
        blueBombFrames[0][2][1] = ContextCompat.getDrawable(context, R.drawable.blue_bomb_row03_step03_fr02);
        // Ряд 1
        blueBombFrames[1][0][0] = ContextCompat.getDrawable(context, R.drawable.blue_bomb_row02_step01_fr01);
        blueBombFrames[1][0][1] = ContextCompat.getDrawable(context, R.drawable.blue_bomb_row02_step01_fr02);
        blueBombFrames[1][1][0] = ContextCompat.getDrawable(context, R.drawable.blue_bomb_row02_step02_fr01);
        blueBombFrames[1][1][1] = ContextCompat.getDrawable(context, R.drawable.blue_bomb_row02_step02_fr02);
        blueBombFrames[1][2][0] = ContextCompat.getDrawable(context, R.drawable.blue_bomb_row02_step03_fr01);
        blueBombFrames[1][2][1] = ContextCompat.getDrawable(context, R.drawable.blue_bomb_row02_step03_fr02);
        // Ряд 2
        blueBombFrames[2][0][0] = ContextCompat.getDrawable(context, R.drawable.blue_bomb_row01_step01_fr01);
        blueBombFrames[2][0][1] = ContextCompat.getDrawable(context, R.drawable.blue_bomb_row01_step01_fr02);
        blueBombFrames[2][1][0] = ContextCompat.getDrawable(context, R.drawable.blue_bomb_row01_step02_fr01);
        blueBombFrames[2][1][1] = ContextCompat.getDrawable(context, R.drawable.blue_bomb_row01_step02_fr02);
        blueBombFrames[2][2][0] = ContextCompat.getDrawable(context, R.drawable.blue_bomb_row01_step03_fr01);
        blueBombFrames[2][2][1] = ContextCompat.getDrawable(context, R.drawable.blue_bomb_row01_step03_fr02);

        // --- АНИМАЦИЯ ИСЧЕЗНОВЕНИЯ ---
        greenDisappearFrames[0][0] = ContextCompat.getDrawable(context, R.drawable.green_bomb_row03_disappear_fr01);
        greenDisappearFrames[0][1] = ContextCompat.getDrawable(context, R.drawable.green_bomb_row03_disappear_fr02);
        greenDisappearFrames[1][0] = ContextCompat.getDrawable(context, R.drawable.green_bomb_row02_disappear_fr01);
        greenDisappearFrames[1][1] = ContextCompat.getDrawable(context, R.drawable.green_bomb_row02_disappear_fr02);
        greenDisappearFrames[2][0] = ContextCompat.getDrawable(context, R.drawable.green_bomb_row01_disappear_fr01);
        greenDisappearFrames[2][1] = ContextCompat.getDrawable(context, R.drawable.green_bomb_row01_disappear_fr02);

        blueDisappearFrames[0][0] = ContextCompat.getDrawable(context, R.drawable.blue_bomb_row03_disappear_fr01);
        blueDisappearFrames[0][1] = ContextCompat.getDrawable(context, R.drawable.blue_bomb_row03_disappear_fr02);
        blueDisappearFrames[1][0] = ContextCompat.getDrawable(context, R.drawable.blue_bomb_row02_disappear_fr01);
        blueDisappearFrames[1][1] = ContextCompat.getDrawable(context, R.drawable.blue_bomb_row02_disappear_fr02);
        blueDisappearFrames[2][0] = ContextCompat.getDrawable(context, R.drawable.blue_bomb_row01_disappear_fr01);
        blueDisappearFrames[2][1] = ContextCompat.getDrawable(context, R.drawable.blue_bomb_row01_disappear_fr02);
    }

    public void updateBoardState(char[][] newBoard, int turn) {
        this.currentGlobalTurn = turn;
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if ((boardData[i][j] == 'X' || boardData[i][j] == 'O') && (newBoard[i][j] == ' ' || newBoard[i][j] == '\0')) {
                    startDisappearAnimation(i, j, boardData[i][j]);
                }
                if ((boardData[i][j] == ' ' || boardData[i][j] == '\0') && (newBoard[i][j] == 'X' || newBoard[i][j] == 'O')) {
                    placementTurn[i][j] = turn;
                }
                if (newBoard[i][j] == ' ' || newBoard[i][j] == '\0') {
                    placementTurn[i][j] = -1;
                }
                boardData[i][j] = newBoard[i][j];
            }
        }
        invalidate();
    }

    private void startDisappearAnimation(final int r, final int c, char type) {
        disappearType[r][c] = type;
        disappearFrame[r][c] = 0;

        // Звук взрыва
        if (explosionSoundId != 0) {
            soundPool.play(explosionSoundId, 1, 1, 0, 0, 1);
        }

        invalidate();

        animationHandler.postDelayed(() -> {
            disappearFrame[r][c] = 1;
            invalidate();
            animationHandler.postDelayed(() -> {
                disappearFrame[r][c] = -1;
                invalidate();
            }, 150);
        }, 150);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        for (int r = 0; r < 3; r++) {
            for (int c = 0; c < 3; c++) {
                if (disappearFrame[r][c] != -1) {
                    Drawable dImg = (disappearType[r][c] == 'X') ?
                            greenDisappearFrames[r][disappearFrame[r][c]] :
                            blueDisappearFrames[r][disappearFrame[r][c]];
                    drawPerspectiveBomb(canvas, r, c, dImg);
                } else {
                    char type = boardData[r][c];
                    if (type == 'X' || type == 'O') {
                        int age = currentGlobalTurn - placementTurn[r][c];
                        int stepIndex = Math.max(0, Math.min(age / 2, 2));
                        int frameIndex = animationTick;
                        Drawable bombImg = (type == 'X') ? greenBombFrames[r][stepIndex][frameIndex] : blueBombFrames[r][stepIndex][frameIndex];
                        drawPerspectiveBomb(canvas, r, c, bombImg);
                    }
                }
            }
        }
    }

    private void drawPerspectiveBomb(Canvas canvas, int r, int c, Drawable bomb) {
        if (bomb == null || cellRegions[r][c] == null) return;
        Rect bounds = cellRegions[r][c].getBounds();
        float centerX = bounds.centerX();
        float centerY = bounds.centerY();
        float manualScale = 0.4f;
        int imgWidth = (int) (bomb.getIntrinsicWidth() * manualScale);
        int imgHeight = (int) (bomb.getIntrinsicHeight() * manualScale);
        float yOffset = (r == 0) ? bounds.height() * 0.40f : (r == 1) ? bounds.height() * 0.35f : bounds.height() * 0.15f;
        int left = (int) (centerX - imgWidth / 2);
        int top = (int) (centerY - imgHeight / 2 - yOffset);
        bomb.setBounds(left, top, left + imgWidth, top + imgHeight);
        bomb.draw(canvas);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        buildTouchRegions(w, h);
    }

    private void buildTouchRegions(int w, int h) {
        // РАЗДВИГАЕМ НИЖНИЙ РЯД (0.02f и 0.98f)
        float leftTop = w * 0.18f, rightTop = w * 0.82f;
        float leftBottom = w * 0.02f, rightBottom = w * 0.98f;

        // ПОДНИМАЕМ ВСЮ СЕТКУ ВЫШЕ
        float topY = h * 0.08f;
        float bottomY = h * 0.80f;

        for (int row = 0; row < 3; row++) {
            float yStart = lerp(topY, bottomY, row / 3f);
            float yEnd = lerp(topY, bottomY, (row + 1) / 3f);

            for (int col = 0; col < 3; col++) {
                float x1 = lerp(lerp(leftTop, leftBottom, row / 3f), lerp(rightTop, rightBottom, row / 3f), col / 3f);
                float x2 = lerp(lerp(leftTop, leftBottom, row / 3f), lerp(rightTop, rightBottom, row / 3f), (col + 1) / 3f);
                float x3 = lerp(lerp(leftTop, leftBottom, (row + 1) / 3f), lerp(rightTop, rightBottom, (row + 1) / 3f), (col + 1) / 3f);
                float x4 = lerp(lerp(leftTop, leftBottom, (row + 1) / 3f), lerp(rightTop, rightBottom, (row + 1) / 3f), col / 3f);

                Path path = new Path();
                path.moveTo(x1, yStart);
                path.lineTo(x2, yStart);
                path.lineTo(x3, yEnd);
                path.lineTo(x4, yEnd);
                path.close();

                Region region = new Region();
                region.setPath(path, new Region(0, 0, w, h));
                cellRegions[row][col] = region;
            }
        }
    }

    private float lerp(float s, float e, float t) { return s + (e - s) * t; }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            for (int r = 0; r < 3; r++) {
                for (int c = 0; c < 3; c++) {
                    if (cellRegions[r][c] != null && cellRegions[r][c].contains((int)event.getX(), (int)event.getY())) {
                        if (listener != null) listener.onCellTouched(r, c);
                        return true;
                    }
                }
            }
        }
        return super.onTouchEvent(event);
    }

    public void setOnCellTouchListener(OnCellTouchListener listener) {
        this.listener = listener;
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        animationHandler.removeCallbacks(animationRunnable);
        if (soundPool != null) {
            soundPool.release();
            soundPool = null;
        }
    }
}