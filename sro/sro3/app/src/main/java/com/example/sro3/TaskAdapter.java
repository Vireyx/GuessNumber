package com.example.sro3;

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

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.VH> {
    private List<Task> list;
    private final OnDelete onDelete;
    interface OnDelete { void onDelete(long id); }

    public TaskAdapter(List<Task> list, OnDelete onDelete) { this.list = list; this.onDelete = onDelete; }

    static class VH extends RecyclerView.ViewHolder {
        TextView name, deadline, category; ImageButton del;
        VH(View v) { super(v); name = v.findViewById(R.id.tvName); deadline = v.findViewById(R.id.tvDeadline); category = v.findViewById(R.id.tvCategory); del = v.findViewById(R.id.btnDelete); }
    }

    @Override public VH onCreateViewHolder(ViewGroup parent, int viewType) {
        return new VH(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_task, parent, false));
    }

    @Override public void onBindViewHolder(VH holder, int position) {
        Task task = list.get(position);
        holder.name.setText(task.name);
        String dateStr = task.deadline > 0 ? new SimpleDateFormat("dd.MM HH:mm", Locale.getDefault()).format(new Date(task.deadline)) : "Нет срока";
        holder.deadline.setText(dateStr);
        holder.category.setText(task.category.isEmpty() ? "" : "📁 " + task.category);
        holder.category.setVisibility(task.category.isEmpty() ? View.GONE : View.VISIBLE);

        boolean isOverdue = task.deadline > 0 && task.deadline < System.currentTimeMillis();
        if (isOverdue) {
            holder.name.setTextColor(Color.RED);
            holder.deadline.setTextColor(Color.RED);
            holder.itemView.setBackgroundColor(Color.parseColor("#FFE5E5"));
        } else {
            holder.name.setTextColor(Color.BLACK);
            holder.deadline.setTextColor(Color.GRAY);
            holder.itemView.setBackgroundColor(Color.WHITE);
        }
        holder.del.setOnClickListener(v -> onDelete.onDelete(task.id));
    }

    @Override public int getItemCount() { return list.size(); }
}
