package com.example.lab11;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.VideoView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;


public class MediaActivity extends AppCompatActivity {

    // Элементы интерфейса
    VideoView videoView;              // VideoView для воспроизведения
    EditText et_MediaPath;            // Поле для пути к файлу
    Button b_SelectFile, b_Start, b_Pause, b_Resume, b_Stop;  // Кнопки
    CheckBox chb_Loop;                // Чекбокс зацикливания

    // Флаг паузы
    boolean isPaused = false;

    // Код запроса файла
    static final int FILE_SELECT_CODE = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_media);

        // Находим элементы интерфейса
        videoView = findViewById(R.id.videoView);
        et_MediaPath = findViewById(R.id.et_MediaPath);
        b_SelectFile = findViewById(R.id.b_SelectFile);
        b_Start = findViewById(R.id.b_Start);
        b_Pause = findViewById(R.id.b_Pause);
        b_Resume = findViewById(R.id.b_Resume);
        b_Stop = findViewById(R.id.b_Stop);
        chb_Loop = findViewById(R.id.chb_Loop);

        // Кнопка "Выбрать файл"
        b_SelectFile.setOnClickListener(v -> openFilePicker());

        // Кнопка "Старт"
        b_Start.setOnClickListener(v -> startVideo());

        // Кнопка "Пауза"
        b_Pause.setOnClickListener(v -> pauseVideo());

        // Кнопка "Продолжить"
        b_Resume.setOnClickListener(v -> resumeVideo());

        // Кнопка "Стоп"
        b_Stop.setOnClickListener(v -> stopVideo());

        // Обработчик окончания видео
        videoView.setOnCompletionListener(mp -> {
            // Если включен цикл - начинаем сначала
            if (chb_Loop.isChecked()) {
                videoView.start();
            }
        });
    }

    /**
     * Открывает выбор файла видео
     */
    private void openFilePicker() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("video/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);

        try {
            startActivityForResult(
                    Intent.createChooser(intent, "Выберите видео"),
                    FILE_SELECT_CODE
            );
        } catch (Exception e) {
            Toast.makeText(this, "Ошибка: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Обработка результата выбора файла
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == FILE_SELECT_CODE && resultCode == RESULT_OK) {
            if (data != null) {
                // Получаем URI выбранного файла
                Uri uri = data.getData();
                if (uri != null) {
                    // Показываем путь
                    et_MediaPath.setText(uri.toString());
                    Toast.makeText(this, "Файл выбран", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    /**
     * Запускает воспроизведение видео
     */
    private void startVideo() {
        String path = et_MediaPath.getText().toString();

        if (!path.isEmpty()) {
            try {
                // Устанавливаем путь к видео
                videoView.setVideoURI(Uri.parse(path));
                // Запускаем
                videoView.start();
                isPaused = false;
                Toast.makeText(this, "Воспроизведение...", Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                Toast.makeText(this, "Ошибка: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Выберите файл", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Ставит на паузу
     */
    private void pauseVideo() {
        if (videoView.isPlaying()) {
            videoView.pause();
            isPaused = true;
            Toast.makeText(this, "Пауза", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Продолжает воспроизведение
     */
    private void resumeVideo() {
        if (isPaused && !videoView.isPlaying()) {
            videoView.start();
            isPaused = false;
            Toast.makeText(this, "Продолжение", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Останавливает воспроизведение
     */
    private void stopVideo() {
        if (videoView.isPlaying()) {
            videoView.stopPlayback();
            isPaused = false;
            Toast.makeText(this, "Остановлено", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Пауза при сворачивании
        if (videoView.isPlaying()) {
            videoView.pause();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Очистка ресурсов
        videoView.stopPlayback();
    }
}