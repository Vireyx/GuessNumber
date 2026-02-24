package com.example.sro2;         // Тот же пакет

import android.os.Bundle;         // Для onCreate
import android.util.Log;          // Для логов

import androidx.appcompat.app.AppCompatActivity;

// Второе окно приложения
public class ActivityTwo extends AppCompatActivity {

    // Та же метка для логов
    private static final String TAG = "States";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Привязываем к этой Activity разметку activity_two.xml
        setContentView(R.layout.activity_two);

        Log.d(TAG, "ActivityTwo: onCreate()");
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "ActivityTwo: onStart()");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "ActivityTwo: onResume()");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "ActivityTwo: onPause()");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "ActivityTwo: onStop()");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "ActivityTwo: onDestroy()");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d(TAG, "ActivityTwo: onRestart()");
    }
}