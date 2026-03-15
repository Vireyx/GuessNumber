package com.example.lab8_9_gestures;

import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class ChoiceActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choice);

        TextView tvTitle = findViewById(R.id.tvTitle);
        Button btnAllGestures = findViewById(R.id.btnAllGestures);
        Button btnSubsetGestures = findViewById(R.id.btnSubsetGestures);

        tvTitle.setText("Лабораторная 8-9:\nРаспознавание жестов");

        btnAllGestures.setOnClickListener(v -> {
            Intent intent = new Intent(ChoiceActivity.this, MainActivity.class);
            startActivity(intent);
        });

        btnSubsetGestures.setOnClickListener(v -> {
            Intent intent = new Intent(ChoiceActivity.this, SubsetGestActivity.class);
            startActivity(intent);
        });
    }
}