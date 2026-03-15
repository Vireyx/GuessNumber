package com.example.sro3;

// Импортируем необходимые классы
import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import static com.example.sro3.TaskContract.TaskEntry;

/**
 * ContentProvider для предоставления доступа к данным
 *
 * Назначение:
 * - Предоставляет стандартизированный интерфейс к данным БД
 * - Позволяет другим приложениям читать/записывать задачи
 * - Защищает доступ через разрешения (permissions)
 *
 * Реализует дополнительное задание №4: ContentProvider
 */
public class TaskProvider extends ContentProvider {

    // ==================== КОНСТАНТЫ ====================

    // Помощник базы данных
    private DBHelper db;

    // Authority - уникальный идентификатор провайдера
    // Должен совпадать с android:authorities в AndroidManifest.xml
    private static final String AUTH = "com.example.sro3.provider";

    // Базовый URI для доступа к задачам
    // Пример: content://com.example.sro3.provider/tasks
    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTH + "/tasks");

    // UriMatcher для различения типов URI
    private static final UriMatcher UM = new UriMatcher(UriMatcher.NO_MATCH);

    // Коды для разных типов URI
    private static final int ALL_TASKS = 1;      // Все задачи
    private static final int ONE_TASK = 2;       // Одна задача по ID

    // Статический блок для регистрации URI шаблонов
    static {
        UM.addURI(AUTH, "tasks", ALL_TASKS);      // content://.../tasks
        UM.addURI(AUTH, "tasks/#", ONE_TASK);     // content://.../tasks/5
    }

    // ==================== ЖИЗНЕННЫЙ ЦИКЛ ====================

    /**
     * Вызывается при создании провайдера
     * Инициализируем базу данных
     */
    @Override
    public boolean onCreate() {
        db = new DBHelper(getContext());
        return true;
    }

    // ==================== CRUD ОПЕРАЦИИ ====================

    /**
     * SELECT: Чтение данных из базы
     *
     * @param uri URI для определения какие данные читать
     * @param projection Какие колонки вернуть
     * @param selection Условие WHERE
     * @param selectionArgs Значения для условия
     * @param sortOrder Сортировка
     * @return Cursor с результатами запроса
     */
    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, String[] projection,
                        String selection, String[] selectionArgs, String sortOrder) {
        // Получаем базу данных для чтения
        SQLiteDatabase d = db.getReadableDatabase();
        Cursor cursor;

        // Определяем тип запроса по URI
        if (UM.match(uri) == ONE_TASK) {
            // Запрос одной задачи по ID
            // Пример URI: content://.../tasks/5
            cursor = d.query(
                    TaskEntry.TABLE, projection,
                    TaskEntry._ID + "=?",
                    new String[]{uri.getLastPathSegment()},
                    null, null, sortOrder
            );
        } else {
            // Запрос всех задач
            // Пример URI: content://.../tasks
            cursor = d.query(
                    TaskEntry.TABLE, projection,
                    selection, selectionArgs,
                    null, null, sortOrder
            );
        }

        // Устанавливаем URI для уведомлений об изменениях
        cursor.setNotificationUri(
                getContext().getContentResolver(), uri
        );

        return cursor;
    }

    /**
     * INSERT: Вставка новой записи
     *
     * @param uri URI таблицы
     * @param values Данные для вставки (ContentValues)
     * @return URI новой записи
     */
    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, ContentValues values) {
        // Вставляем запись в базу данных
        long id = db.getWritableDatabase()
                .insert(TaskEntry.TABLE, null, values);

        // Уведомляем об изменении данных
        getContext().getContentResolver().notifyChange(uri, null);

        // Возвращаем URI новой записи
        // Пример: content://.../tasks/5
        return ContentUris.withAppendedId(uri, id);
    }

    /**
     * DELETE: Удаление записей
     *
     * @param uri URI для определения что удалять
     * @param selection Условие WHERE
     * @param selectionArgs Значения для условия
     * @return Количество удалённых строк
     */
    @Override
    public int delete(@NonNull Uri uri, String selection, String[] selectionArgs) {
        int count;
        SQLiteDatabase d = db.getWritableDatabase();

        // Определяем тип удаления по URI
        if (UM.match(uri) == ONE_TASK) {
            // Удаление одной задачи по ID
            count = d.delete(
                    TaskEntry.TABLE,
                    TaskEntry._ID + "=?",
                    new String[]{uri.getLastPathSegment()}
            );
        } else {
            // Удаление по условию
            count = d.delete(TaskEntry.TABLE, selection, selectionArgs);
        }

        // Уведомляем об изменении данных
        getContext().getContentResolver().notifyChange(uri, null);

        return count;
    }

    /**
     * UPDATE: Обновление записей
     * (Не реализовано в этой версии)
     */
    @Override
    public int update(@NonNull Uri uri, ContentValues values,
                      String selection, String[] selectionArgs) {
        return 0;
    }

    /**
     * Возвращает MIME тип данных
     * (Не реализовано в этой версии)
     */
    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }
}