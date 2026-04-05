package com.example.sro_4;

import android.Manifest;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class StudentActivity extends AppCompatActivity {

    private EditText etName;
    private Spinner spinnerGroup;
    private SeekBar seekBarAge;
    private TextView tvAgeValue, tvDateValue;
    private Button btnPickDate, btnTakePhoto, btnBack;
    private ImageView ivPhoto;

    private SharedPreferences prefs;
    private static final String PREFS_NAME = "student_settings";
    private static final int REQUEST_CAMERA = 100;
    private static final int PERMISSION_CAMERA = 101;
    private File photoFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student);

        prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);

        etName = findViewById(R.id.etName);
        spinnerGroup = findViewById(R.id.spinnerGroup);
        seekBarAge = findViewById(R.id.seekBarAge);
        tvAgeValue = findViewById(R.id.tvAgeValue);
        tvDateValue = findViewById(R.id.tvDateValue);
        btnPickDate = findViewById(R.id.btnPickDate);
        ivPhoto = findViewById(R.id.ivPhoto);
        btnTakePhoto = findViewById(R.id.btnTakePhoto);
        btnBack = findViewById(R.id.btnBack);

        String[] groups = {"ИВТ-11", "ИВТ-12", "ПИ-11", "ПИ-12"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, groups);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerGroup.setAdapter(adapter);

        seekBarAge.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                tvAgeValue.setText("Возраст: " + progress + " лет");
            }
            @Override public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        btnPickDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();
                String savedDate = tvDateValue.getText().toString();
                if (!savedDate.equals("Не выбрана") && !savedDate.isEmpty()) {
                    try {
                        String[] parts = savedDate.split("\\.");
                        int day = Integer.parseInt(parts[0]);
                        int month = Integer.parseInt(parts[1]) - 1;
                        int year = Integer.parseInt(parts[2]);
                        calendar.set(year, month, day);
                    } catch (Exception e) {}
                }
                DatePickerDialog datePickerDialog = new DatePickerDialog(
                    StudentActivity.this,
                    new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                            String date = String.format("%02d.%02d.%04d", dayOfMonth, month + 1, year);
                            tvDateValue.setText(date);
                        }
                    },
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH)
                );
                datePickerDialog.show();
            }
        });

        btnTakePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(StudentActivity.this, Manifest.permission.CAMERA)
                        != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(StudentActivity.this, 
                        new String[]{Manifest.permission.CAMERA}, PERMISSION_CAMERA);
                } else {
                    dispatchTakePictureIntent();
                }
            }
        });

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        loadData();
    }

    @Override
    protected void onPause() {
        super.onPause();
        saveData();
    }

    private void saveData() {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("name", etName.getText().toString());
        editor.putInt("group_index", spinnerGroup.getSelectedItemPosition());
        editor.putInt("age", seekBarAge.getProgress());
        editor.putString("date", tvDateValue.getText().toString());
        if (photoFile != null) {
            editor.putString("photo_filename", photoFile.getName());
        }
        editor.apply();
    }

    private void loadData() {
        etName.setText(prefs.getString("name", ""));
        int groupPos = prefs.getInt("group_index", 0);
        if(groupPos < spinnerGroup.getCount()) spinnerGroup.setSelection(groupPos);
        int age = prefs.getInt("age", 18);
        seekBarAge.setProgress(age);
        tvAgeValue.setText("Возраст: " + age + " лет");
        tvDateValue.setText(prefs.getString("date", "Не выбрана"));
        String fileName = prefs.getString("photo_filename", null);
        if (fileName != null) {
            File f = new File(getFilesDir(), "images/" + fileName);
            if (f.exists()) {
                Bitmap bitmap = BitmapFactory.decodeFile(f.getAbsolutePath());
                ivPhoto.setImageBitmap(bitmap);
                photoFile = f;
            }
        }
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            try {
                photoFile = createImageFile();
                Uri photoURI = FileProvider.getUriForFile(this,
                        getPackageName() + ".fileprovider", photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_CAMERA);
            } catch (IOException ex) {
                Toast.makeText(this, "Ошибка создания файла", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = new File(getFilesDir(), "images");
        if (!storageDir.exists()) storageDir.mkdirs();
        return File.createTempFile(imageFileName, ".jpg", storageDir);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CAMERA && resultCode == RESULT_OK) {
            if (photoFile != null) {
                Bitmap bitmap = BitmapFactory.decodeFile(photoFile.getAbsolutePath());
                ivPhoto.setImageBitmap(bitmap);
                saveData();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_CAMERA && grantResults.length > 0 && 
            grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            dispatchTakePictureIntent();
        } else {
            Toast.makeText(this, "Нет доступа к камере", Toast.LENGTH_SHORT).show();
        }
    }
}
