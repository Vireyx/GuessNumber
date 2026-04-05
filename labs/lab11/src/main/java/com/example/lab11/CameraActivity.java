package com.example.lab11;

// Импорты для работы с разрешениями
import android.Manifest;
import android.content.pm.PackageManager;
// Импорты для работы с изображениями
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.Surface;
import android.graphics.Matrix;
// Импорты для работы с камерой (устаревший API, но работает)
import android.hardware.Camera;
// Импорты для сохранения состояния
import android.os.Bundle;
// Импорты для работы с поверхностью камеры
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.WindowManager;
// Импорты для элементов интерфейса
import android.widget.Button;
import android.widget.Toast;
// Импорты для аннотаций
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
// Импорты для работы с разрешениями (совместимость)
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

// Импорты для работы с файлами
import java.io.File;
import java.io.FileOutputStream;
// Импорты для форматирования даты
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


@SuppressWarnings("deprecation")  // Подавляем предупреждения о устаревшем API
public class CameraActivity extends AppCompatActivity
        // Реализуем интерфейсы для работы с поверхностью и callback'ами
        implements SurfaceHolder.Callback, Camera.PictureCallback {

    // ===== ЭЛЕМЕНТЫ ИНТЕРФЕЙСА =====
    SurfaceView surfaceCamera;  // SurfaceView для предпросмотра камеры
    Button bCameraShot;         // Кнопка для снимка

    // ===== ОБЪЕКТ КАМЕРЫ =====
    Camera camera;              // Объект камеры (управление камерой)

    // ===== КОНСТАНТЫ =====
    // Код запроса разрешения (чтобы понять какой запрос вернулся)
    private static final int CAMERA_PERMISSION_CODE = 100;
    // ID камеры (0 = задняя камера, 1 = фронтальная)
    private static final int CAMERA_ID = 0;

    /**
     * Метод вызывается при создании активности
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Устанавливаем layout камеры
        setContentView(R.layout.activity_camera);

        // ===== ИНИЦИАЛИЗАЦИЯ ЭЛЕМЕНТОВ =====
        // Находим SurfaceView для предпросмотра
        surfaceCamera = findViewById(R.id.surfaceCamera);
        // Находим кнопку снимка
        bCameraShot = findViewById(R.id.bCameraShot);

        // ===== ПРОВЕРКА РАЗРЕШЕНИЯ НА КАМЕРУ =====
        // Проверяем есть ли у нас разрешение на камеру
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            // Разрешения нет - запрашиваем у пользователя
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA},  // Какое разрешение
                    CAMERA_PERMISSION_CODE);                    // Код запроса
        } else {
            // Разрешение уже есть - инициализируем камеру
            initCamera();
        }

        // ===== ОБРАБОТЧИК КНОПКИ "СДЕЛАТЬ СНИМОК" =====
        bCameraShot.setOnClickListener(v -> {
            // Проверяем что камера инициализирована
            if (camera != null) {
                // Делаем снимок
                // null, null, this - callback'и для shutter, raw, jpeg
                camera.takePicture(null, null, this);
                // Показываем сообщение
                Toast.makeText(this, "Снимок...", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Инициализация камеры - добавляем callback для поверхности
     * Вызывается после получения разрешения
     */
    private void initCamera() {
        // Добавляем callback'и для SurfaceHolder
        // Они будут вызваны когда поверхность создана/изменена/уничтожена
        surfaceCamera.getHolder().addCallback(this);
    }

    // ==========================================
    // ===== ИНТЕРФЕЙС SurfaceHolder.Callback =====
    // ==========================================

    /**
     * Вызывается когда поверхность создана
     * Здесь мы открываем камеру и настраиваем предпросмотр
     */
    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        try {
            // Освобождаем старую камеру если она есть
            if (camera != null) {
                camera.release();  // Освобождаем ресурсы
                camera = null;     // Обнуляем ссылку
            }

            // ===== ВЫБОР КАМЕРЫ =====
            int cameraId = CAMERA_ID;  // По умолчанию задняя камера
            Camera.CameraInfo info = new Camera.CameraInfo();
            int numberOfCameras = Camera.getNumberOfCameras();

            // Проверяем что выбранная камера существует
            if (cameraId >= numberOfCameras) {
                cameraId = 0;  // Если нет - берём первую
            }

            // Получаем информацию о камере
            Camera.getCameraInfo(cameraId, info);
            // Открываем камеру
            camera = Camera.open(cameraId);

            // ===== ВЫЧИСЛЕНИЕ ПРАВИЛЬНОГО УГЛА ПОВОРОТА =====
            // Получаем текущую ориентацию экрана
            int rotation = getWindowManager().getDefaultDisplay().getRotation();
            int degrees = 0;

            // Переводим константы rotation в градусы
            switch (rotation) {
                case Surface.ROTATION_0: degrees = 0; break;
                case Surface.ROTATION_90: degrees = 90; break;
                case Surface.ROTATION_180: degrees = 180; break;
                case Surface.ROTATION_270: degrees = 270; break;
            }

            // Вычисляем правильный угол поворота
            int result;
            if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                // Для фронтальной камеры (зеркальное отображение)
                result = (info.orientation + degrees) % 360;
                result = (360 - result) % 360;
            } else {
                // Для задней камеры
                result = (info.orientation - degrees + 360) % 360;
            }

            // Устанавливаем угол поворота предпросмотра
            camera.setDisplayOrientation(result);

            // Устанавливаем поверхность для предпросмотра
            camera.setPreviewDisplay(holder);

            // Запускаем предпросмотр
            camera.startPreview();

            // Показываем сообщение что камера готова
            Toast.makeText(this, "Камера готова", Toast.LENGTH_SHORT).show();

        } catch (Exception e) {
            // Если ошибка - показываем сообщение
            Toast.makeText(this, "Ошибка: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Вызывается когда поверхность изменилась (размер/формат)
     */
    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        // Если камера есть - запускаем предпросмотр
        if (camera != null) {
            camera.startPreview();
        }
    }

    /**
     * Вызывается когда поверхность уничтожается
     * Освобождаем ресурсы камеры
     */
    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        // Если камера есть - освобождаем
        if (camera != null) {
            camera.stopPreview();  // Останавливаем предпросмотр
            camera.release();      // Освобождаем ресурсы
            camera = null;         // Обнуляем ссылку
        }
    }

    // ==========================================
    // ===== ИНТЕРФЕЙС Camera.PictureCallback =====
    // ==========================================

    /**
     * Вызывается когда фото сделано (JPEG данные готовы)
     * @param data байты JPEG изображения
     * @param camera объект камеры
     */
    @Override
    public void onPictureTaken(byte[] data, Camera camera) {
        // Сохраняем фото с правильным поворотом
        saveRotatedPhoto(data);

        // Возобновляем предпросмотр для следующего снимка
        camera.startPreview();
    }

    /**
     * Сохраняет фотографию с правильным поворотом
     * @param data байты JPEG изображения
     */
    private void saveRotatedPhoto(byte[] data) {
        try {
            // ===== ГЕНЕРАЦИЯ ИМЕНИ ФАЙЛА =====
            // Создаём имя файла с текущей датой и временем
            // yyyyMMdd_HHmmss = 20250325_143022.jpg
            String fileName = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault())
                    .format(new Date()) + ".jpg";

            // ===== СОЗДАНИЕ ПАПКИ =====
            // Создаём папку для фото в хранилище приложения
            File dir = new File(getExternalFilesDir(null), "CameraApp");
            // Создаём папку если её нет (mkdirs создаёт все родительские папки)
            dir.mkdirs();
            // Создаём объект файла
            File file = new File(dir, fileName);

            // ===== ДЕКОДИРОВАНИЕ JPEG =====
            // Преобразуем байты в Bitmap
            Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);

            // ===== ВЫЧИСЛЕНИЕ УГЛА ПОВОРОТА =====
            // Получаем информацию о камере
            Camera.CameraInfo info = new Camera.CameraInfo();
            Camera.getCameraInfo(CAMERA_ID, info);

            // Получаем ориентацию экрана
            int rotation = getWindowManager().getDefaultDisplay().getRotation();
            int degrees = 0;

            switch (rotation) {
                case Surface.ROTATION_0: degrees = 0; break;
                case Surface.ROTATION_90: degrees = 90; break;
                case Surface.ROTATION_180: degrees = 180; break;
                case Surface.ROTATION_270: degrees = 270; break;
            }

            // Вычисляем угол поворота
            int rotateAngle;
            if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                // Для фронтальной камеры
                rotateAngle = (info.orientation + degrees) % 360;
            } else {
                // Для задней камеры
                rotateAngle = (info.orientation - degrees + 360) % 360;
            }

            // ===== ПОВОРОТ ИЗОБРАЖЕНИЯ =====
            // Если угол не 0 - поворачиваем Bitmap
            if (rotateAngle != 0) {
                // Создаём матрицу поворота
                Matrix matrix = new Matrix();
                // Устанавливаем угол поворота
                matrix.postRotate(rotateAngle);

                // Создаём повёрнутый Bitmap
                bitmap = Bitmap.createBitmap(
                        bitmap,              // Исходный bitmap
                        0, 0,                // Начальные координаты
                        bitmap.getWidth(),   // Ширина
                        bitmap.getHeight(),  // Высота
                        matrix,              // Матрица преобразования
                        true                 // Фильтр для сглаживания
                );
            }

            // ===== СОХРАНЕНИЕ ФАЙЛА =====
            // Создаём FileOutputStream для записи файла
            FileOutputStream fos = new FileOutputStream(file);

            // Сжимаем Bitmap в JPEG и записываем в файл
            // JPEG, 100 - качество 100% (максимальное)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);

            // Закрываем поток
            fos.close();

            // Показываем сообщение об успехе
            Toast.makeText(this, "Фото: " + fileName, Toast.LENGTH_LONG).show();

        } catch (Exception e) {
            // Если ошибка - показываем сообщение
            Toast.makeText(this, "Ошибка сохранения", Toast.LENGTH_SHORT).show();
        }
    }

    // ==========================================
    // ===== ОБРАБОТКА ЗАПРОСА РАЗРЕШЕНИЙ =====
    // ==========================================

    /**
     * Вызывается когда пользователь ответил на запрос разрешений
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        // Проверяем что это наш запрос
        if (requestCode == CAMERA_PERMISSION_CODE) {
            // Проверяем что разрешение дали
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Разрешение дали - инициализируем камеру
                initCamera();
                Toast.makeText(this, "Разрешение получено", Toast.LENGTH_SHORT).show();
            } else {
                // Разрешение НЕ дали - показываем сообщение и закрываем
                Toast.makeText(this, "Нужно разрешение камеры!", Toast.LENGTH_LONG).show();
                finish();  // Закрываем активность
            }
        }
    }

    // ==========================================
    // ===== ЖИЗНЕННЫЙ ЦИКЛ АКТИВНОСТИ =====
    // ==========================================

    /**
     * Вызывается когда активность становится видимой
     * Перезапускаем камеру если нужно
     */
    @Override
    protected void onResume() {
        super.onResume();

        // Если камера null и разрешение есть - инициализируем
        if (camera == null && ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED) {
            initCamera();
        }
    }

    /**
     * Вызывается когда активность теряет фокус (сворачивается)
     * Освобождаем камеру
     */
    @Override
    protected void onPause() {
        super.onPause();

        // Если камера есть - освобождаем
        if (camera != null) {
            camera.stopPreview();  // Останавливаем предпросмотр
            camera.release();      // Освобождаем ресурсы
            camera = null;         // Обнуляем ссылку
        }
    }
}