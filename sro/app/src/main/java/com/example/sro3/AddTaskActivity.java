package com.example.sro3;

// Импортируем необходимые классы
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

/**
 * Активность для добавления новой задачи
 *
 * Назначение:
 * - Позволяет пользователю ввести название задачи
 * - Выбор категории (необязательно)
 * - Выбор даты и времени дедлайна
 * - Возвращает данные в MainActivity через Intent
 *
 * Реализует дополнительное задание №3: Две активности
 */
public class AddTaskActivity extends AppCompatActivity {

    // ==================== ПОЛЯ КЛАССА ====================

    // Поля ввода из интерфейса
    private EditText etName;        // Название задачи
    private EditText etCategory;    // Категория
    private TextView tvDeadline;    // Выбранная дата дедлайна

    // Календарь для хранения выбранной даты и времени
    private Calendar cal = Calendar.getInstance();

    // ==================== ЖИЗНЕННЫЙ ЦИКЛ ====================

    /**
     * Метод вызывается при создании активности
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Устанавливаем макет интерфейса
        setContentView(R.layout.activity_add_task);

        // Находим элементы интерфейса по ID
        etName = findViewById(R.id.etName);
        etCategory = findViewById(R.id.etCategory);
        tvDeadline = findViewById(R.id.tvDeadline);
        Button btnSave = findViewById(R.id.btnSave);

        // Устанавливаем обработчики событий
        tvDeadline.setOnClickListener(v -> pickDateTime());
        btnSave.setOnClickListener(v -> save());
    }

    /**
     * Диалог выбора даты и времени дедлайна
     *
     * Сначала показываем DatePicker, затем TimePicker
     */
    private void pickDateTime() {
        // Получаем текущие значения для начальной установки
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);

        // Создаём и показываем диалог выбора даты
        new DatePickerDialog(this, (d, y, m, dayOfMonth) -> {
            // Устанавливаем выбранную дату
            cal.set(y, m, dayOfMonth);

            // После выбора даты показываем диалог выбора времени
            new TimePickerDialog(this, (t, hour, minute) -> {
                // Устанавливаем выбранное время
                cal.set(Calendar.HOUR_OF_DAY, hour);
                cal.set(Calendar.MINUTE, minute);

                // Форматируем и отображаем выбранную дату и время
                String dateStr = new SimpleDateFormat(
                        "dd.MM HH:mm", Locale.getDefault()
                ).format(cal.getTime());

                tvDeadline.setText(dateStr);
            }, 12, 0, true).show(); // 12:00, 24-часовой формат

        }, year, month, day).show();
    }

    /**
     * Сохранение задачи и возврат данных в MainActivity
     */
    private void save() {
        // Получаем текст из поля названия
        String name = etName.getText().toString().trim();

        // Проверяем, что название не пустое
        if (name.isEmpty()) {
            Toast.makeText(this, "Введите название", Toast.LENGTH_SHORT).show();
            return; // Прерываем выполнение
        }

        // Создаём Intent для передачи данных назад
        Intent result = new Intent();

        // Добавляем данные в Intent (ключ-значение)
        result.putExtra("name", name);
        result.putExtra("deadline", cal.getTimeInMillis());
        result.putExtra("category", etCategory.getText().toString().trim());

        // Устанавливаем результат и завершаем активность
        setResult(RESULT_OK, result);
        finish(); // Закрываем эту активность
    }
}