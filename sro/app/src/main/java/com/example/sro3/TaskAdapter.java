package com.example.sro3;

// Импортируем необходимые классы
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Адаптер для RecyclerView
 *
 * Назначение:
 * - Связывает данные (список Task) с элементами интерфейса
 * - Создаёт и настраивает элементы списка (ViewHolder)
 * - Обрабатывает клики по элементам
 *
 * Реализует дополнительное задание №2: RecyclerView
 */
public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.VH> {

    // ==================== ПОЛЯ КЛАССА ====================

    // Список задач для отображения
    private List<Task> list;

    // Интерфейс для обработки удаления задачи
    private final OnDelete onDelete;

    /**
     * Интерфейс для обратного вызова при удалении
     * Позволяет адаптеру сообщать MainActivity об удалении
     */
    interface OnDelete {
        void onDelete(long id);
    }

    /**
     * Конструктор адаптера
     *
     * @param list Список задач для отображения
     * @param onDelete Обработчик удаления задачи
     */
    public TaskAdapter(List<Task> list, OnDelete onDelete) {
        this.list = list;
        this.onDelete = onDelete;
    }

    // ==================== ViewHolder ====================

    /**
     * ViewHolder хранит ссылки на элементы интерфейса
     * Избегает постоянного поиска view по ID (оптимизация)
     */
    static class VH extends RecyclerView.ViewHolder {
        TextView name;        // Название задачи
        TextView deadline;    // Дедлайн
        TextView category;    // Категория
        ImageButton del;      // Кнопка удаления

        /**
         * Конструктор ViewHolder
         * Находит все элементы по ID
         */
        VH(View v) {
            super(v);
            name = v.findViewById(R.id.tvName);
            deadline = v.findViewById(R.id.tvDeadline);
            category = v.findViewById(R.id.tvCategory);
            del = v.findViewById(R.id.btnDelete);
        }
    }

    // ==================== МЕТОДЫ АДАПТЕРА ====================

    /**
     * Создаёт новый ViewHolder
     * Вызывается когда нужно создать новый элемент списка
     */
    @Override
    public VH onCreateViewHolder(ViewGroup parent, int viewType) {
        // Загружаем макет элемента из XML
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_task, parent, false);

        // Создаём и возвращаем ViewHolder
        return new VH(view);
    }

    /**
     * Привязывает данные к ViewHolder
     * Вызывается для каждого элемента списка
     *
     * @param holder ViewHolder для заполнения данными
     * @param position Позиция элемента в списке
     */
    @Override
    public void onBindViewHolder(VH holder, int position) {
        // Получаем задачу для текущей позиции
        Task task = list.get(position);

        // Устанавливаем название задачи
        holder.name.setText(task.name);

        // Форматируем и отображаем дедлайн
        String dateStr = task.deadline > 0 ?
                new SimpleDateFormat("dd.MM HH:mm", Locale.getDefault())
                        .format(new Date(task.deadline))
                : "Нет срока";
        holder.deadline.setText(dateStr);

        // Устанавливаем категорию (если есть)
        holder.category.setText(task.category.isEmpty() ? "" : "📁 " + task.category);
        holder.category.setVisibility(
                task.category.isEmpty() ? View.GONE : View.VISIBLE
        );

        // 🔴 ПРОВЕРКА: Просрочена ли задача
        // Сравниваем дедлайн с текущим временем
        boolean isOverdue = task.deadline > 0 &&
                task.deadline < System.currentTimeMillis();

        // Меняем внешний вид для просроченных задач
        if (isOverdue) {
            holder.name.setTextColor(Color.RED);
            holder.deadline.setTextColor(Color.RED);
            holder.itemView.setBackgroundColor(
                    Color.parseColor("#FFE5E5") // Розовый фон
            );
        } else {
            holder.name.setTextColor(Color.BLACK);
            holder.deadline.setTextColor(Color.GRAY);
            holder.itemView.setBackgroundColor(Color.WHITE);
        }

        // Устанавливаем обработчик кнопки удаления
        holder.del.setOnClickListener(v -> onDelete.onDelete(task.id));
    }

    /**
     * Возвращает количество элементов в списке
     */
    @Override
    public int getItemCount() {
        return list.size();
    }
}