package com.example.lab8_9_gestures;

import android.os.Bundle;
import android.app.Activity;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.widget.TextView;

public class SubsetGestActivity extends Activity {

    private GestureDetector mDetector;  // ← Изменили тип
    private TextView tvOut;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subset_gest);

        tvOut = findViewById(R.id.textView1);
        tvOut.setText("Распознаются только: onFling и onLongPress\n");

        // Используем SimpleOnGestureListener для переопределения только нужных методов
        mDetector = new GestureDetector(this, new MyGestListener());  // ← Убрали Compat
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mDetector.onTouchEvent(event);
        return super.onTouchEvent(event);
    }

    // Внутренний класс-слушатель жестов
    class MyGestListener extends GestureDetector.SimpleOnGestureListener {

        @Override
        public boolean onDown(MotionEvent e) {
            // Обязательно возвращаем true, иначе другие жесты не сработают!
            return true;
        }

        @Override
        public boolean onFling(MotionEvent event1, MotionEvent event2,
                               float velocityX, float velocityY) {
            String direction;
            if (Math.abs(velocityX) > Math.abs(velocityY)) {
                direction = velocityX > 0 ? "→ ВПРАВО" : "← ВЛЕВО";
            } else {
                direction = velocityY > 0 ? "↓ ВНИЗ" : "↑ ВВЕРХ";
            }

            appendOutput("⚡ СВАЙП " + direction +
                    " (скорость: " + String.format("%.0f", Math.sqrt(velocityX*velocityX + velocityY*velocityY)) + ")");
            return true;
        }

        @Override
        public void onLongPress(MotionEvent event) {
            appendOutput("🕐 ДОЛГОЕ НАЖАТИЕ распознано!");
        }

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            appendOutput("ℹ️ Одинарный тап НЕ обрабатывается (не включён в подмножество)");
            return true;
        }
    }

    private void appendOutput(String text) {
        runOnUiThread(() -> {
            String current = tvOut.getText().toString();
            if (current.split("\n").length > 15) {
                String[] lines = current.split("\n");
                StringBuilder sb = new StringBuilder();
                for (int i = lines.length - 10; i < lines.length; i++) {
                    sb.append(lines[i]).append("\n");
                }
                tvOut.setText(sb.toString());
            }
            tvOut.append(text + "\n");
        });
    }
}