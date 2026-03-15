package com.example.sro3;

// Импортируем классы для работы с SQLite
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

// Импортируем константы из контрактного класса
import static com.example.sro3.TaskContract.TaskEntry;

public class DBHelper extends SQLiteOpenHelper {

    // Имя файла базы данных (хранится в /data/data/com.example.sro3/databases/)
    private static final String DATABASE_NAME = "sro3.db";

    // Версия базы данных (увеличивать при изменении схемы)
    private static final int DATABASE_VERSION = 1;

    /**
     * Конструктор класса
     *
     * @param ctx Контекст приложения (нужен для доступа к ресурсам)
     *
     * Параметры суперкласса:
     * 1. context - контекст приложения
     * 2. name - имя файла базы данных
     * 3. factory - фабрика курсоров (null = стандартная)
     * 4. version - версия базы данных (для миграций)
     */
    public DBHelper(Context ctx) {
        super(ctx, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /**
     * Метод вызывается ОДИН РАЗ при первом создании базы данных
     *
     * @param db Объект SQLiteDatabase для выполнения SQL-запросов
     *
     * Здесь создаётся схема таблицы (структура данных)
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        // SQL-запрос для создания таблицы задач
        String sql = "CREATE TABLE " + TaskEntry.TABLE + " (" +
                TaskEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +  // Автоинкрементируемый ID
                TaskEntry.NAME + " TEXT NOT NULL, " +                      // Название (обязательно)
                TaskEntry.DEADLINE + " INTEGER, " +                        // Дедлайн (число)
                TaskEntry.CATEGORY + " TEXT)";                             // Категория (текст)

        // Выполняем SQL-запрос
        db.execSQL(sql);
    }

    /**
     * Метод вызывается при обновлении версии базы данных
     *
     * @param db Объект SQLiteDatabase
     * @param oldV Старая версия базы данных
     * @param newV Новая версия базы данных
     *
     * Используется для миграции данных при обновлении приложения
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldV, int newV) {
        // Простой вариант: удаляем старую таблицу и создаём новую
        // В реальном проекте нужно сохранять данные!
        db.execSQL("DROP TABLE IF EXISTS " + TaskEntry.TABLE);

        // Создаём таблицу заново с новой схемой
        onCreate(db);
    }

    /**
     * Метод вызывается при понижении версии базы данных
     * (например, при установке старой версии приложения)
     */
    @Override
    public void onDowngrade(SQLiteDatabase db, int oldV, int newV) {
        // Вызываем onUpgrade для обработки
        onUpgrade(db, oldV, newV);
    }
}