package com.example.sro3;

// Импортируем BaseColumns для автоматического добавления поля _ID
import android.provider.BaseColumns;

/**
 * Контрактный класс для схемы базы данных
 *
 * Назначение:
 * - Хранит имена таблиц и колонок в виде констант
 * - Предотвращает ошибки при опечатках в названиях
 * - Упрощает изменение схемы БД (меняем в одном месте)
 * - Делает код более читаемым и поддерживаемым
 */
public class TaskContract {

    // Приватный конструктор — класс не должен создаваться как объект
    // Это только набор констант
    private TaskContract() {}

    /**
     * Внутренний класс для описания таблицы задач
     * Реализует BaseColumns для автоматического поля _ID
     */
    public static class TaskEntry implements BaseColumns {
        // Имя таблицы в базе данных
        public static final String TABLE = "tasks";

        // Название задачи (обязательное поле)
        public static final String NAME = "name";

        // Дедлайн задачи (хранится как timestamp - миллисекунды)
        public static final String DEADLINE = "deadline";

        // Категория задачи (необязательное поле)
        public static final String CATEGORY = "category";
    }
}