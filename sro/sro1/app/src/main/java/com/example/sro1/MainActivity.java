// Пакет проекта (оставь тот, который у тебя уже был сверху)
package com.example.sro1;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

// Один экран приложения + обработчик нажатий на все кнопки
public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    // "Экран" калькулятора
    private TextView tvDisplay;

    // Текущее вводимое число
    private String current = "0";

    // Операция: "+", "-", "*", "/"
    private String operator = "";

    // Первое число (до операции)
    private long firstNumber = 0;

    // Флаг: начинаем ввод нового числа или продолжаем старое
    private boolean isNewNumber = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Подключаем разметку activity_main.xml
        setContentView(R.layout.activity_main);

        // Находим TextView по id
        tvDisplay = findViewById(R.id.tvDisplay);

        // Массив id для кнопок-цифр
        int[] numberIds = {
                R.id.btn0, R.id.btn1, R.id.btn2, R.id.btn3, R.id.btn4,
                R.id.btn5, R.id.btn6, R.id.btn7, R.id.btn8, R.id.btn9
        };

        // Вешаем один и тот же OnClickListener (this) на все цифры
        for (int id : numberIds) {
            findViewById(id).setOnClickListener(this);
        }

        // Кнопки операций
        int[] opIds = { R.id.btnPlus, R.id.btnMinus, R.id.btnMul, R.id.btnDiv };
        for (int id : opIds) {
            findViewById(id).setOnClickListener(this);
        }

        // Кнопки "C" и "="
        findViewById(R.id.btnClear).setOnClickListener(this);
        findViewById(R.id.btnEqual).setOnClickListener(this);

        // Начальный текст
        tvDisplay.setText("0");
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();

        // Очистка
        if (id == R.id.btnClear) {
            clearAll();
            return;
        }

        // Равно
        if (id == R.id.btnEqual) {
            calculate();
            return;
        }

        // Операции
        if (id == R.id.btnPlus || id == R.id.btnMinus
                || id == R.id.btnMul || id == R.id.btnDiv) {
            chooseOperator(id);
            return;
        }

        // Если это цифра — берём текст с кнопки
        Button b = (Button) v;
        String digit = b.getText().toString();
        appendDigit(digit);
    }

    // Добавляем цифру к текущему числу
    private void appendDigit(String digit) {
        if (isNewNumber) {
            current = "";
            isNewNumber = false;
        }

        if (current.equals("0")) {
            current = digit;
        } else {
            current += digit;
        }

        tvDisplay.setText(current);
    }

    // Выбираем операцию +  -  *  /
    private void chooseOperator(int id) {
        if (!current.isEmpty()) {
            firstNumber = Long.parseLong(current);
        }

        if (id == R.id.btnPlus) {
            operator = "+";
        } else if (id == R.id.btnMinus) {
            operator = "-";
        } else if (id == R.id.btnMul) {
            operator = "*";
        } else if (id == R.id.btnDiv) {
            operator = "/";
        }

        isNewNumber = true;   // Следующее число вводим с нуля
    }

    // Считаем результат firstNumber (операция) current
    private void calculate() {
        if (operator.isEmpty() || isNewNumber) {
            // Нечего считать
            return;
        }

        long second = Long.parseLong(current);
        long result = 0;

        switch (operator) {
            case "+":
                result = firstNumber + second;
                break;
            case "-":
                result = firstNumber - second;
                break;
            case "*":
                result = firstNumber * second;
                break;
            case "/":
                if (second == 0) {
                    tvDisplay.setText("Error");
                    operator = "";
                    isNewNumber = true;
                    return;
                } else {
                    result = firstNumber / second;
                }
                break;
        }

        current = String.valueOf(result);
        tvDisplay.setText(current);

        // Готовимся к следующей операции
        firstNumber = result;
        operator = "";
        isNewNumber = true;
    }

    // Сброс всего
    private void clearAll() {
        current = "0";
        operator = "";
        firstNumber = 0;
        isNewNumber = true;
        tvDisplay.setText("0");
    }
}