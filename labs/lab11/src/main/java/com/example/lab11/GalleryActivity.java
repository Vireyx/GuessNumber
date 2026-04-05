package com.example.lab11;

// Импорты для работы с изображениями
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
// Импорты для сохранения состояния
import android.os.Bundle;
// Импорты для элементов интерфейса
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
// Импорты для базового класса
import androidx.appcompat.app.AppCompatActivity;

// Импорты для работы с файлами
import java.io.File;
import java.util.ArrayList;
import java.util.List;


public class GalleryActivity extends AppCompatActivity {

    // ===== ЭЛЕМЕНТЫ ИНТЕРФЕЙСА =====
    ImageView imageImageView1;  // ImageView для отображения фотографии
    TextView tvImageInfo;       // TextView для показа информации о фото
    Button bPrevious;           // Кнопка "Предыдущее фото"
    Button bNext;               // Кнопка "Следующее фото"

    // ===== ПЕРЕМЕННЫЕ ДЛЯ РАБОТЫ С ФОТО =====
    List<File> imageFiles;      // Список файлов изображений
    int currentIndex;           // Текущий индекс фото в списке (начинается с 0)

    /**
     * Метод вызывается при создании активности
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Устанавливаем layout галереи
        setContentView(R.layout.activity_gallery);

        // ===== ИНИЦИАЛИЗАЦИЯ ЭЛЕМЕНТОВ ИНТЕРФЕЙСА =====
        // Находим элементы по их ID из XML файла
        imageImageView1 = findViewById(R.id.imageImageView1);
        tvImageInfo = findViewById(R.id.tvImageInfo);
        bPrevious = findViewById(R.id.bPrevious);  // ID в XML: bPrevious
        bNext = findViewById(R.id.bNext);          // ID в XML: bNext

        // Загружаем список всех фотографий из папки
        loadImageFiles();

        // Проверяем есть ли фотографии
        if (!imageFiles.isEmpty()) {
            // Если есть - показываем первое фото (индекс 0)
            displayImage();
        } else {
            // Если нет - показываем сообщение
            Toast.makeText(this, "Нет фотографий", Toast.LENGTH_SHORT).show();
        }

        // ===== ОБРАБОТЧИК КНОПКИ "ПРЕДЫДУЩЕЕ" =====
        bPrevious.setOnClickListener(v -> {
            // Проверяем что не первое фото (currentIndex > 0)
            if (currentIndex > 0) {
                // Уменьшаем индекс (переходим к предыдущему)
                currentIndex--;
                // Обновляем отображение
                displayImage();
            }
        });

        // ===== ОБРАБОТЧИК КНОПКИ "СЛЕДУЮЩЕЕ" =====
        bNext.setOnClickListener(v -> {
            // Проверяем что не последнее фото
            // size() - 1 это индекс последнего элемента
            if (currentIndex < imageFiles.size() - 1) {
                // Увеличиваем индекс (переходим к следующему)
                currentIndex++;
                // Обновляем отображение
                displayImage();
            }
        });
    }

    /**
     * Загружает все JPG файлы из папки CameraApp
     */
    private void loadImageFiles() {
        // Создаём новый пустой список
        imageFiles = new ArrayList<>();

        // Создаём объект File для папки с фото
        // getExternalFilesDir(null) - возвращает путь к папке приложения
        // "CameraApp" - подпапка где хранятся наши фото
        File dir = new File(getExternalFilesDir(null), "CameraApp");

        // Проверяем что папка существует и это действительно папка
        if (dir.exists() && dir.isDirectory()) {
            // Получаем все файлы в папке
            File[] files = dir.listFiles();

            // Проверяем что файлы есть (listFiles может вернуть null)
            if (files != null) {
                // Проходим по всем файлам
                for (File file : files) {
                    // Проверяем что файл имеет расширение .jpg (в нижнем регистре)
                    if (file.getName().toLowerCase().endsWith(".jpg")) {
                        // Добавляем файл в список
                        imageFiles.add(file);
                    }
                }
            }
        }

        // Сортируем файлы по имени (хронологически)
        // (f1, f2) -> f1.getName().compareTo(f2.getName()) - лямбда для сравнения
        imageFiles.sort((f1, f2) -> f1.getName().compareTo(f2.getName()));
    }

    /**
     * Отображает текущее фото (по индексу currentIndex)
     */
    private void displayImage() {
        // Если список пуст - ничего не делаем
        if (imageFiles.isEmpty()) return;

        // Получаем файл текущего фото по индексу
        File imageFile = imageFiles.get(currentIndex);

        // Декодируем файл в Bitmap (изображение в памяти)
        // decodeFile автоматически определяет формат (JPG, PNG и т.д.)
        Bitmap bitmap = BitmapFactory.decodeFile(imageFile.getAbsolutePath());

        // Проверяем что изображение успешно загружено
        if (bitmap != null) {
            // Устанавливаем изображение в ImageView
            imageImageView1.setImageBitmap(bitmap);

            // Формируем и показываем информацию о фото:
            // - Имя файла
            // - Размеры (ширина x высота)
            // - Номер фото (текущий / всего)
            tvImageInfo.setText(String.format(
                    "%s\n%d x %d\n%d / %d",
                    imageFile.getName(),              // Имя файла
                    bitmap.getWidth(),                // Ширина в пикселях
                    bitmap.getHeight(),               // Высота в пикселях
                    currentIndex + 1,                 // Номер (с 1, а не с 0)
                    imageFiles.size()                 // Всего фото
            ));
        }
    }

    /**
     * Метод вызывается когда активность становится видимой
     * Перезагружаем список фото (могли добавиться новые)
     */
    @Override
    protected void onResume() {
        super.onResume();
        // Перезагружаем список файлов
        loadImageFiles();
        // Показываем текущее фото
        if (!imageFiles.isEmpty()) displayImage();
    }
}