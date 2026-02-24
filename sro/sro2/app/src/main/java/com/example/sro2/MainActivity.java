package com.example.sro2;         // Пакет проекта

import android.content.Intent;    // Для перехода на вторую Activity
import android.os.Bundle;         // Для метода onCreate
import android.util.Log;          // Для вывода сообщений в Logcat
import android.view.View;         // Для обработки кликов
import android.widget.Button;     // Класс кнопки

import androidx.appcompat.app.AppCompatActivity;  // Базовый класс Activity

// MainActivity — первое окно приложения
public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    // Метка для Logcat
    private static final String TAG = "States";

    // Кнопка перехода ко второму окну
    private Button btnActTwo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Важно: вызвать родительскую реализацию
        super.onCreate(savedInstanceState);

        // Привязываем разметку activity_main.xml к этому окну
        setContentView(R.layout.activity_main);

        // Лог — метод onCreate вызван
        Log.d(TAG, "MainActivity: onCreate()");

        // Находим кнопку по ID из разметки
        btnActTwo = findViewById(R.id.btnActTwo);

        // Назначаем обработчик клика (эта же Activity)
        btnActTwo.setOnClickListener(this);
    }

    // Обработка нажатий на View (кнопки)
    @Override
    public void onClick(View v) {
        // Проверяем: нажата ли именно наша кнопка перехода
        if (v.getId() == R.id.btnActTwo) {

            // Создаём явный Intent: откуда → куда
            Intent intent = new Intent(MainActivity.this, ActivityTwo.class);

            // Запускаем второе окно (ActivityTwo)
            startActivity(intent);
        }
    }

    // Ниже — методы жизненного цикла с логами, чтобы отслеживать состояние

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "MainActivity: onStart()");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "MainActivity: onResume()");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "MainActivity: onPause()");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "MainActivity: onStop()");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "MainActivity: onDestroy()");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d(TAG, "MainActivity: onRestart()");
    }
}