package com.example.lab8_9_gestures;

import android.os.Bundle;
import android.app.Activity;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.widget.TextView;
import android.view.GestureDetector;


public class MainActivity extends Activity
        implements GestureDetector.OnGestureListener,
        GestureDetector.OnDoubleTapListener {

    private TextView tvOutput;
    private GestureDetector mDetector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvOutput = findViewById(R.id.textView1);
        tvOutput.setText("Готов к распознаванию жестов!\n");

        // Создание детектора жестов
        mDetector = new GestureDetector(this, this);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        this.mDetector.onTouchEvent(event);
        return super.onTouchEvent(event);
    }

    // ========== Методы OnGestureListener ==========

    @Override
    public boolean onDown(MotionEvent event) {
        appendOutput("✓ onDown: нажатие обнаружено");
        return true; // Важно вернуть true для продолжения обработки
    }

    @Override
    public void onShowPress(MotionEvent event) {
        appendOutput("→ onShowPress: нажатие удерживается");
    }

    @Override
    public boolean onSingleTapUp(MotionEvent event) {
        appendOutput("☝ onSingleTapUp: одинарное касание завершено");
        return true;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2,
                            float distanceX, float distanceY) {
        appendOutput("↕ onScroll: перемещение (dx=" +
                String.format("%.1f", distanceX) +
                ", dy=" + String.format("%.1f", distanceY) + ")");
        return true;
    }

    @Override
    public void onLongPress(MotionEvent event) {
        appendOutput("🕐 onLongPress: долгое нажатие");
    }

    @Override
    public boolean onFling(MotionEvent event1, MotionEvent event2,
                           float velocityX, float velocityY) {
        appendOutput("⚡ onFling: быстрый свайп (vX=" +
                String.format("%.0f", velocityX) +
                ", vY=" + String.format("%.0f", velocityY) + ")");
        return true;
    }

    // ========== Методы OnDoubleTapListener ==========

    @Override
    public boolean onSingleTapConfirmed(MotionEvent event) {
        appendOutput("✅ onSingleTapConfirmed: подтверждённое одинарное касание");
        return true;
    }

    @Override
    public boolean onDoubleTap(MotionEvent event) {
        appendOutput("👆👆 onDoubleTap: двойное касание");
        return true;
    }

    @Override
    public boolean onDoubleTapEvent(MotionEvent event) {
        // Срабатывает между двумя тапами двойного касания
        if (event.getAction() == MotionEvent.ACTION_MOVE) {
            appendOutput("🔄 onDoubleTapEvent: движение после первого тапа");
        }
        return true;
    }

    // Вспомогательный метод для добавления текста в TextView
    private void appendOutput(String text) {
        runOnUiThread(() -> {
            String current = tvOutput.getText().toString();
            // Ограничиваем количество строк для удобства
            if (current.split("\n").length > 15) {
                String[] lines = current.split("\n");
                StringBuilder sb = new StringBuilder();
                for (int i = lines.length - 10; i < lines.length; i++) {
                    sb.append(lines[i]).append("\n");
                }
                tvOutput.setText(sb.toString());
            }
            tvOutput.append(text + "\n");
        });
    }
}