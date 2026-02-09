package com.example.guessnumber;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private TextView tvInfo;
    private TextView tvAttempts;
    private EditText etInput;

    private int secretNumber;   // загаданное число
    private int attempts;       // количество попыток
    private boolean gameFinished; // игра закончена или нет

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvInfo = findViewById(R.id.tvInfo);
        tvAttempts = findViewById(R.id.tvAttempts);
        etInput = findViewById(R.id.editText1);

        startNewGame();
    }

    // запуск новой игры
    private void startNewGame() {
        secretNumber = (int) (Math.random() * 10) + 1; // число от 1 до 100
        attempts = 0;
        gameFinished = false;

        tvInfo.setText(getString(R.string.try_to_guess));
        tvAttempts.setText(getString(R.string.attempts_zero));
        etInput.setText("");

        Button bControl = findViewById(R.id.button1);
        bControl.setText(getString(R.string.button_guess));
    }

    // обработка нажатия кнопки
    public void onClick(View view) {
        if (gameFinished) {
            // если игра уже закончилась — начинаем новую
            startNewGame();
            return;
        }

        String text = etInput.getText().toString().trim();

        // ничего не ввели
        if (text.isEmpty()) {
            tvInfo.setText(getString(R.string.error_empty));
            return;
        }

        int guess;
        try {
            guess = Integer.parseInt(text);
        } catch (NumberFormatException e) {
            tvInfo.setText(getString(R.string.error_not_number));
            etInput.setText("");
            return;
        }

        // число вне диапазона
        if (guess < 1 || guess > 100) {
            tvInfo.setText(getString(R.string.error_out_of_range));
            return;
        }

        // увеличиваем количество попыток
        attempts++;
        tvAttempts.setText(getString(R.string.attempts_format, attempts));

        // сравниваем с загаданным числом
        if (guess > secretNumber) {
            tvInfo.setText(getString(R.string.hint_less));     // нужно меньше
        } else if (guess < secretNumber) {
            tvInfo.setText(getString(R.string.hint_greater));  // нужно больше
        } else {
            // угадал
            tvInfo.setText(getString(R.string.win_text));
            Button bControl = findViewById(R.id.button1);
            bControl.setText(getString(R.string.button_play_again));
            gameFinished = true;
        }

        etInput.setText("");
    }
}
