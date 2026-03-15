package com.example.sro3;

// ========== ИМПОРТЫ С КОММЕНТАРИЯМИ ==========

// Intent — для запуска другой активности (переход на экран добавления)
import android.content.Intent;

// Cursor — для чтения данных из SQLite (итератор по результатам запроса)
import android.database.Cursor;

// SQLiteDatabase — основной класс для работы с базой данных
import android.database.sqlite.SQLiteDatabase;

// Bundle — для сохранения состояния активности при повороте экрана
import android.os.Bundle;

// Log — вывод отладочных сообщений в Logcat
import android.util.Log;

// Toast — короткие всплывающие уведомления для пользователя
import android.widget.Toast;

// AppCompatActivity — базовый класс активности с поддержкой новых функций
import androidx.appcompat.app.AppCompatActivity;

// ActivityResultLauncher — современный способ получения результата из активности
import androidx.activity.result.ActivityResultLauncher;

// ActivityResultLauncher — лаунчер для запуска активности
import androidx.activity.result.ActivityResultLauncher;

// ActivityResultContracts — контракты (в подпакете contract!)
import androidx.activity.result.contract.ActivityResultContracts;

// LinearLayoutManager — менеджер расположения для вертикального списка
import androidx.recyclerview.widget.LinearLayoutManager;

// RecyclerView — современный компонент для отображения прокручиваемых списков
import androidx.recyclerview.widget.RecyclerView;

// FloatingActionButton — плавающая круглая кнопка с иконкой (+)
import com.google.android.material.floatingactionbutton.FloatingActionButton;

// ArrayList — динамический список для хранения задач в памяти
import java.util.ArrayList;

// List — интерфейс списка для типизации коллекции
import java.util.List;

// Импортируем константы из контрактного класса (имена таблиц и колонок)
import static com.example.sro3.TaskContract.TaskEntry;


/**
 * ГЛАВНАЯ АКТИВНОСТЬ ПРИЛОЖЕНИЯ
 *
 * Назначение:
 * - Отображает список задач в RecyclerView
 * - Содержит CRUD-операции для работы с базой данных
 * - Запускает активность добавления задачи
 *
 * Реализует задания:
 * - №1: Отдельные функции для работы с БД
 * - №2: RecyclerView для отображения списка
 * - №3: Две активности (главная + добавление)
 */
public class MainActivity extends AppCompatActivity {

    // ==================== ПОЛЯ КЛАССА ====================

    // Помощник для работы с базой данных (создание, открытие, миграции)
    private DBHelper db;

    // RecyclerView — компонент для эффективного отображения списка
    private RecyclerView rv;

    // Адаптер — связывает данные (список задач) с элементами интерфейса
    private TaskAdapter adapter;

    // Список задач — хранит данные, полученные из базы данных
    private List<Task> tasks = new ArrayList<>();

    // 🔥 СОВРЕМЕННЫЙ СПОСОБ: лаунчер для получения результата из другой активности
    // Заменяет устаревший startActivityForResult()
    private ActivityResultLauncher<Intent> addTaskLauncher;


    // ==================== ЖИЗНЕННЫЙ ЦИКЛ АКТИВНОСТИ ====================

    /**
     * Вызывается при первом создании активности
     * Здесь инициализируем все компоненты интерфейса и логики
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Устанавливаем макет интерфейса из XML-файла
        setContentView(R.layout.activity_main);

        // Создаём экземпляр помощника базы данных
        // При первом запуске автоматически создастся файл БД
        db = new DBHelper(this);

        // Регистрируем лаунчер для обработки результата из AddTaskActivity
        registerAddTaskLauncher();

        // Настраиваем RecyclerView (список задач)
        setupRecyclerView();

        // Настраиваем кнопку добавления задачи (FloatingActionButton)
        setupFabButton();

        // Загружаем и отображаем задачи из базы данных
        loadTasks();
    }

    /**
     * 🔥 РЕГИСТРАЦИЯ ЛАУНЧЕРА (замена onActivityResult)
     *
     * Современный API для получения результата из запущенной активности
     */
    private void registerAddTaskLauncher() {
        addTaskLauncher = registerForActivityResult(
                // Контракт: запускаем активность и ждём результат
                new ActivityResultContracts.StartActivityForResult(),

                // Обработчик результата (вызывается автоматически при возврате)
                result -> {
                    // Проверяем, что активность завершилась успешно
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {

                        // Извлекаем данные из возвращённого Intent
                        Intent data = result.getData();
                        String name = data.getStringExtra("name");
                        long deadline = data.getLongExtra("deadline", 0);
                        String category = data.getStringExtra("category");

                        // Добавляем задачу в базу данных
                        addTask(name, deadline, category);

                        // Обновляем список на экране
                        loadTasks();

                        // Показываем уведомление пользователю
                        Toast.makeText(this, "✓ Задача добавлена: " + name, Toast.LENGTH_SHORT).show();
                    }
                }
        );
    }

    /**
     * НАСТРОЙКА RECYCLERVIEW
     *
     * Инициализирует список для отображения задач
     */
    private void setupRecyclerView() {
        // Находим RecyclerView в макете по его ID
        rv = findViewById(R.id.recyclerView);

        // Устанавливаем менеджер компоновки: вертикальный список
        rv.setLayoutManager(new LinearLayoutManager(this));

        // Создаём адаптер и передаём ему:
        // - список данных (tasks)
        // - метод для обработки удаления (this::deleteTask)
        adapter = new TaskAdapter(tasks, this::deleteTask);

        // Устанавливаем адаптер в RecyclerView
        rv.setAdapter(adapter);
    }

    /**
     * НАСТРОЙКА КНОПКИ ДОБАВЛЕНИЯ (FAB)
     *
     * Обрабатывает нажатие на плавающую кнопку "+"
     */
    private void setupFabButton() {
        // Находим кнопку в макете по ID
        FloatingActionButton fab = findViewById(R.id.fabAdd);

        // Устанавливаем обработчик нажатия
        fab.setOnClickListener(v -> {
            // Создаём намерение для запуска активности добавления задачи
            Intent intent = new Intent(this, AddTaskActivity.class);

            // 🔥 ЗАПУСКАЕМ через современный лаунчер (вместо startActivityForResult)
            addTaskLauncher.launch(intent);
        });
    }


    // ==================== CRUD ОПЕРАЦИИ (Доп. задание №1) ====================

    /**
     * ✅ CREATE: Добавить новую задачу в базу данных
     *
     * @param name Название задачи (обязательно)
     * @param deadline Дедлайн в миллисекундах (может быть 0)
     * @param category Категория задачи (необязательно)
     */
    public void addTask(String name, long deadline, String category) {
        // Получаем базу данных в режиме записи
        SQLiteDatabase d = db.getWritableDatabase();

        // Создаём объект ContentValues для хранения данных в формате ключ-значение
        android.content.ContentValues cv = new android.content.ContentValues();

        // Заполняем данные для вставки в таблицу
        cv.put(TaskEntry.NAME, name);           // Название задачи
        cv.put(TaskEntry.DEADLINE, deadline);   // Дедлайн (timestamp)
        cv.put(TaskEntry.CATEGORY, category);   // Категория

        // Вставляем новую запись в таблицу

        long id = d.insert(TaskEntry.TABLE, null, cv);

        // Выводим лог для отладки (видно в Logcat при фильтре "DB_INSERT")
        Log.d("DB_INSERT", "Задача добавлена: ID=" + id + ", name=" + name);
    }

    /**
     * ✅ READ: Загрузить все задачи из базы данных
     *
     * Этот метод:
     * 1. Выполняет SELECT-запрос к базе данных
     * 2. Читает результаты через Cursor
     * 3. Создаёт объекты Task из каждой строки
     * 4. Обновляет адаптер для отображения в RecyclerView
     */
    public void loadTasks() {
        // Лог для отладки: начало загрузки
        Log.d("DB_LOAD", "=== Загрузка задач из БД ===");

        // Очищаем текущий список перед загрузкой новых данных
        tasks.clear();

        // Получаем базу данных в режиме чтения
        SQLiteDatabase d = db.getReadableDatabase();

        // Выполняем запрос SELECT
        // Параметры метода query():
        // 1. table — имя таблицы ("tasks")
        // 2. columns — какие колонки вернуть (null = все)
        // 3. selection — условие WHERE (null = без условия)
        // 4. selectionArgs — значения для подстановки в условие
        // 5. groupBy — группировка результатов
        // 6. having — фильтр для групп
        // 7. orderBy — сортировка ("deadline ASC" = сначала старые)
        Cursor c = d.query(
                TaskEntry.TABLE,                    // Таблица
                null,                               // Все колонки
                null, null, null, null,             // Без условий
                TaskEntry.DEADLINE + " ASC"         // Сортировка по дате
        );

        // Лог с количеством найденных записей
        Log.d("DB_LOAD", "Найдено задач: " + c.getCount());

        // Проверяем, есть ли данные в курсоре
        // moveToFirst() возвращает true, если курсор не пустой
        if (c.moveToFirst()) {
            // Получаем индексы колонок для быстрого доступа к данным
            // (вместо поиска по имени каждый раз)
            int idIndex = c.getColumnIndex(TaskEntry._ID);
            int nameIndex = c.getColumnIndex(TaskEntry.NAME);
            int deadlineIndex = c.getColumnIndex(TaskEntry.DEADLINE);
            int categoryIndex = c.getColumnIndex(TaskEntry.CATEGORY);

            // Проходим по всем строкам результата запроса
            do {
                // Создаём объект Task из данных текущей строки курсора
                tasks.add(new Task(
                        c.getLong(idIndex),         // ID задачи
                        c.getString(nameIndex),     // Название
                        c.getLong(deadlineIndex),   // Дедлайн
                        c.getString(categoryIndex)  // Категория
                ));
            } while (c.moveToNext());  // Переходим к следующей строке
        }

        // Закрываем курсор — освобождаем системные ресурсы
        c.close();

        // Уведомляем адаптер, что данные изменились
        // Это вызывает перерисовку списка в RecyclerView
        adapter.notifyDataSetChanged();
    }

    /**
     * ✅ DELETE: Удалить задачу по её ID
     *
     * @param id Уникальный идентификатор задачи для удаления
     */
    public void deleteTask(long id) {
        // Получаем базу данных в режиме записи
        SQLiteDatabase d = db.getWritableDatabase();

        // Удаляем запись из таблицы
        // Параметры метода delete():
        // 1. table — имя таблицы ("tasks")
        // 2. whereClause — условие WHERE с ? для параметра ("id = ?")
        // 3. whereArgs — массив значений для подстановки вместо ?
        d.delete(
                TaskEntry.TABLE,
                TaskEntry._ID + "=?",               // WHERE id = ?
                new String[]{String.valueOf(id)}    // Значение для подстановки
        );

        // Перезагружаем список после удаления
        // (чтобы удалить задачу и из отображения на экране)
        loadTasks();
    }


    // ==================== ЗАВЕРШЕНИЕ РАБОТЫ ====================

    /**
     * Вызывается при уничтожении активности
     * Закрываем подключение к базе данных для освобождения ресурсов
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        db.close(); // Закрываем базу данных
    }
}