package com.example.lab12;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

public class MainActivity extends AppCompatActivity {

    TextView tvStatus, tvLat, tvLon;
    LocationManager locationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvStatus = findViewById(R.id.tvStatus);
        tvLat = findViewById(R.id.tvLat);
        tvLon = findViewById(R.id.tvLon);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        checkPermission();
    }

    private void checkPermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        } else {
            startGPS();
        }
    }

    private void startGPS() {
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            tvStatus.setText("GPS включен");
        } else {
            tvStatus.setText("GPS выключен");
        }

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                1000,
                1,
                new LocationListener() {
                    @Override
                    public void onLocationChanged(@NonNull Location location) {
                        tvLat.setText("Широта: " + location.getLatitude());
                        tvLon.setText("Долгота: " + location.getLongitude());
                    }

                    @Override
                    public void onProviderEnabled(@NonNull String provider) {
                        tvStatus.setText("GPS включен");
                    }

                    @Override
                    public void onProviderDisabled(@NonNull String provider) {
                        tvStatus.setText("GPS выключен");
                    }
                }
        );
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 1) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startGPS();
            } else {
                tvStatus.setText("Нет разрешения");
            }
        }
    }
}