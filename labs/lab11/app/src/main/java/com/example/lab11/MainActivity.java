package com.example.lab11;

// Импорты для работы с намерениями (переходы между экранами)
import android.content.Intent;
// Импорты для сохранения состояния активности
import android.os.Bundle;
// Импорты для кнопок
import android.widget.Button;
// Импорты для базового класса активности
import androidx.appcompat.app.AppCompatActivity;

/**
 * MainActivity - главный экран приложения
 *
 * Содержит три кнопки для навигации:
 * - Камера (переход в CameraActivity)
 * - Галерея (переход в GalleryActivity)
 * - Медиа (переход в MediaActivity)
 */
public class MainActivity extends AppCompatActivity {

    // Объявление кнопок для навигации
    Button bCamera;    // Кнопка перехода к камере
    Button bGallery;   // Кнопка перехода к галерее
    Button bMusic;     // Кнопка перехода к медиаплееру

    /**
     * Метод вызывается при создании активности
     * @param savedInstanceState сохранённое состояние (если есть)
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Вызов метода родительского класса
        super.onCreate(savedInstanceState);

        // Установка layout (интерфейса) для этой активности
        // Файл layout находится в res/layout/activity_main.xml
        setContentView(R.layout.activity_main);

        // Инициализация кнопок - находим их по ID из XML
        // R.id.bCamera - это ID кнопки в XML файле
        bCamera = findViewById(R.id.bCamera);
        bGallery = findViewById(R.id.bGallery);
        bMusic = findViewById(R.id.bMusic);

        // ===== ОБРАБОТЧИК КНОПКИ "КАМЕРА" =====
        // setOnClickListener устанавливает обработчик нажатия
        bCamera.setOnClickListener(v -> {
            // Создаём намерение (Intent) для перехода
            // MainActivity.this - текущая активность
            // CameraActivity.class - целевая активность
            Intent intent = new Intent(MainActivity.this, CameraActivity.class);

            // Запускаем новую активность
            startActivity(intent);
        });

        // ===== ОБРАБОТЧИК КНОПКИ "ГАЛЕРЕЯ" =====
        bGallery.setOnClickListener(v -> {
            // Создаём намерение для перехода в галерею
            Intent intent = new Intent(MainActivity.this, GalleryActivity.class);
            // Запускаем активность
            startActivity(intent);
        });

        // ===== ОБРАБОТЧИК КНОПКИ "МЕДИА" =====
        bMusic.setOnClickListener(v -> {
            // Создаём намерение для перехода в медиаплеер
            Intent intent = new Intent(MainActivity.this, MediaActivity.class);
            // Запускаем активность
            startActivity(intent);
        });
    }
}