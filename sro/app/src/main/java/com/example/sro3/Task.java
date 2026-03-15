package com.example.sro3;

/**
 * Модель данных для задачи (POJO - Plain Old Java Object)
 *
 * Назначение:
 * - Представляет одну запись из базы данных в виде Java-объекта
 * - Упрощает работу с данными (вместо Cursor используем объекты)
 * - Используется для передачи данных между компонентами
 */
public class Task {

    // Поля класса (соответствуют колонкам в таблице БД)
    public long id;           // Уникальный идентификатор задачи (из БД)
    public String name;       // Название задачи
    public String category;   // Категория задачи
    public long deadline;     // Дедлайн в миллисекундах (Unix timestamp)

    /**
     * Конструктор для создания объекта задачи
     *
     * @param id Идентификатор задачи (из БД)
     * @param name Название задачи
     * @param deadline Дедлайн (миллисекунды с 1 января 1970)
     * @param category Категория задачи
     */
    public Task(long id, String name, long deadline, String category) {
        this.id = id;
        this.name = name;
        this.deadline = deadline;
        this.category = category;
    }
}