package com.example.sro3;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class AddTaskActivity extends AppCompatActivity {
    private EditText etName, etCategory;
    private TextView tvDeadline;
    private Calendar cal = Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);
        etName = findViewById(R.id.etName);
        etCategory = findViewById(R.id.etCategory);
        tvDeadline = findViewById(R.id.tvDeadline);
        Button btnSave = findViewById(R.id.btnSave);

        tvDeadline.setOnClickListener(v -> pickDateTime());
        btnSave.setOnClickListener(v -> save());
    }

    private void pickDateTime() {
        new DatePickerDialog(this, (d, y, m, day) -> {
            cal.set(y, m, day);
            new TimePickerDialog(this, (t, h, min) -> {
                cal.set(Calendar.HOUR_OF_DAY, h);
                cal.set(Calendar.MINUTE, min);
                tvDeadline.setText(new SimpleDateFormat("dd.MM HH:mm", Locale.getDefault()).format(cal.getTime()));
            }, 12, 0, true).show();
        }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void save() {
        String name = etName.getText().toString().trim();
        if (name.isEmpty()) { Toast.makeText(this, "Введите название", Toast.LENGTH_SHORT).show(); return; }
        Intent result = new Intent();
        result.putExtra("name", name);
        result.putExtra("deadline", cal.getTimeInMillis());
        result.putExtra("category", etCategory.getText().toString().trim());
        setResult(RESULT_OK, result);
        finish();
    }
}
