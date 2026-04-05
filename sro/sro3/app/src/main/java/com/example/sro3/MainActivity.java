package com.example.sro3;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private DBHelper db;
    private RecyclerView rv;
    private TaskAdapter adapter;
    private List<Task> tasks = new ArrayList<>();
    private ActivityResultLauncher<Intent> addTaskLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        db = new DBHelper(this);

        addTaskLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    Intent data = result.getData();
                    String name = data.getStringExtra("name");
                    long deadline = data.getLongExtra("deadline", 0);
                    String category = data.getStringExtra("category");
                    addTask(name, deadline, category);
                    loadTasks();
                    Toast.makeText(this, "Задача добавлена", Toast.LENGTH_SHORT).show();
                }
            }
        );

        rv = findViewById(R.id.recyclerView);
        rv.setLayoutManager(new LinearLayoutManager(this));
        adapter = new TaskAdapter(tasks, this::deleteTask);
        rv.setAdapter(adapter);

        FloatingActionButton fab = findViewById(R.id.fabAdd);
        fab.setOnClickListener(v -> {
            Intent intent = new Intent(this, AddTaskActivity.class);
            addTaskLauncher.launch(intent);
        });
        loadTasks();
    }

    public void addTask(String name, long deadline, String category) {
        SQLiteDatabase d = db.getWritableDatabase();
        android.content.ContentValues cv = new android.content.ContentValues();
        cv.put(TaskContract.TaskEntry.NAME, name);
        cv.put(TaskContract.TaskEntry.DEADLINE, deadline);
        cv.put(TaskContract.TaskEntry.CATEGORY, category);
        d.insert(TaskContract.TaskEntry.TABLE, null, cv);
    }

    public void loadTasks() {
        tasks.clear();
        SQLiteDatabase d = db.getReadableDatabase();
        Cursor c = d.query(TaskContract.TaskEntry.TABLE, null, null, null, null, null, TaskContract.TaskEntry.DEADLINE + " ASC");
        if (c.moveToFirst()) {
            int id = c.getColumnIndex(TaskContract.TaskEntry._ID);
            int name = c.getColumnIndex(TaskContract.TaskEntry.NAME);
            int dl = c.getColumnIndex(TaskContract.TaskEntry.DEADLINE);
            int cat = c.getColumnIndex(TaskContract.TaskEntry.CATEGORY);
            do { tasks.add(new Task(c.getLong(id), c.getString(name), c.getLong(dl), c.getString(cat))); } while (c.moveToNext());
        }
        c.close();
        adapter.notifyDataSetChanged();
    }

    public void deleteTask(long id) {
        db.getWritableDatabase().delete(TaskContract.TaskEntry.TABLE, TaskContract.TaskEntry._ID + "=?", new String[]{String.valueOf(id)});
        loadTasks();
    }
    
    @Override protected void onDestroy() { super.onDestroy(); db.close(); }
}
