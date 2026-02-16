package com.example.firstapp;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class ActivityAddPoint extends AppCompatActivity implements OnMapReadyCallback {

    private EditText etPointName, etSoldierName, etDescription;
    private Button btnAddPoint;
    private GoogleMap pickerMap;
    private double selectedLat, selectedLng;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_point);

        etPointName = findViewById(R.id.etPointName);
        etSoldierName = findViewById(R.id.etSoldierName);
        etDescription = findViewById(R.id.etDescription);
        btnAddPoint = findViewById(R.id.btnAddPointToMap);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.mapPicker);
        if (mapFragment != null) mapFragment.getMapAsync(this);

        btnAddPoint.setOnClickListener(v -> savePoint());
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        pickerMap = googleMap;
        // Default focus (e.g., Tel Aviv area)
        LatLng initial = new LatLng(32.0853, 34.7818);
        pickerMap.moveCamera(CameraUpdateFactory.newLatLngZoom(initial, 12f));

        pickerMap.setOnCameraIdleListener(() -> {
            LatLng center = pickerMap.getCameraPosition().target;
            selectedLat = center.latitude;
            selectedLng = center.longitude;
        });
    }

    private void savePoint() {
        String title = etPointName.getText().toString().trim();
        String soldier = etSoldierName.getText().toString().trim();
        String desc = etDescription.getText().toString().trim();

        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            Toast.makeText(this, "Log in first!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (title.isEmpty()) {
            Toast.makeText(this, "Please enter a name", Toast.LENGTH_SHORT).show();
            return;
        }

        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        String fullInfo = soldier + "\n" + desc;

        // --- THE FIX ---
        // We use the soldier's name as the 'locationName' for the popup,
        // or you can set this to a generic string like "Memorial Point"
        String locationValue = soldier.isEmpty() ? "Memorial Site" : "In memory of " + soldier;

        // Creating the point with ALL 6 arguments required by your MapPoint constructor
        MapPoint point = new MapPoint(title, fullInfo, selectedLat, selectedLng, uid, locationValue);

        FirebaseFirestore.getInstance().collection("Points")
                .add(point)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(this, "Saved to Firestore!", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(this, "Error: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        Log.e("AddPoint", task.getException().getMessage());
                    }
                });
    }
}