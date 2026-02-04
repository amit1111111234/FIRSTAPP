package com.example.firstapp;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Locale;

public class ActivityAddPoint extends AppCompatActivity {

    // These names now match exactly what you use in onCreate
    EditText etPointName, etSoldierName, etDescription, etCoordinates, etLocationName;
    Button btnAddPoint;

    private double selectedLat = 0, selectedLng = 0;
    private String title, desc, uid;
    private FusedLocationProviderClient fusedLocationClient;
    private Location currentLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_point);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
//        fetchCurrentLocation();

        // Initializing views
        etPointName = findViewById(R.id.etPointName);
        etSoldierName = findViewById(R.id.etSoldierName);
        etDescription = findViewById(R.id.etDescription);
        etCoordinates = findViewById(R.id.etCoordinates);
        etLocationName = findViewById(R.id.etLocationName);
        btnAddPoint = findViewById(R.id.btnAddPointToMap);

        // Click to get Coordinates
        etCoordinates.setOnClickListener(v -> {
            if (currentLocation != null) {
                selectedLat = currentLocation.getLatitude();
                selectedLng = currentLocation.getLongitude();
                etCoordinates.setText(String.format(Locale.getDefault(), "%.6f, %.6f", selectedLat, selectedLng));
            } else {
//                fetchCurrentLocation();
                Toast.makeText(this, "Fetching GPS... try again in a moment", Toast.LENGTH_SHORT).show();
            }
        });

        // Click to Save (The fix for btnSave error)
        btnAddPoint.setOnClickListener(v -> {
            String title = etPointName.getText().toString().trim();
            String desc = etDescription.getText().toString().trim();

            if (FirebaseAuth.getInstance().getCurrentUser() == null) {
                Toast.makeText(this, "Error: User not logged in", Toast.LENGTH_SHORT).show();
                return;
            }

            String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

            if (title.isEmpty() || selectedLat == 0) {
                Toast.makeText(this, "Please fill in Name and click Coordinates", Toast.LENGTH_SHORT).show();
                return;
            }

            saveMapPoint();
        });
    }
            // Create and Save the Object


    private void fetchCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.getLastLocation().addOnSuccessListener(location -> {
                if (location != null) currentLocation = location;
            });
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 100);
        }
    }

    private void saveMapPoint() {
        MapPoint newPoint = new MapPoint(title, desc, selectedLat, selectedLng, uid);

            FirebaseDatabase.getInstance().getReference("Points")
                    .push()
                    .setValue(newPoint)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(this, "Point Added Successfully!", Toast.LENGTH_SHORT).show();
                        finish();
                    })
                    .addOnFailureListener(e -> Toast.makeText(this, "Failed: " + e.getMessage(), Toast.LENGTH_SHORT).show());
        };
    }
