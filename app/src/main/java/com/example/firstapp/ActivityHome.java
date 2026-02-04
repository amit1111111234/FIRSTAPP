package com.example.firstapp;

import android.Manifest;
import android.content.Intent; // Essential for opening new pages
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.ImageView; // Essential for the + button
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ActivityHome extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private FusedLocationProviderClient fusedLocationClient;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // Initialize Location Client
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        // --- USERNAME LOGIC ---
        TextView userNameDisplay = findViewById(R.id.userNameDisplay);
        String name = getIntent().getStringExtra("USER_NAME");
        if (name != null && !name.isEmpty()) {
            userNameDisplay.setText(name);
        } else {
            if (FirebaseAuth.getInstance().getCurrentUser() != null) {
                String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users").child(uid).child("name");
                ref.get().addOnSuccessListener(snapshot -> {
                    if (snapshot.exists()) userNameDisplay.setText(snapshot.getValue(String.class));
                });
            }
        }

        // Initialize Map
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        // --- THE FIX: ADD POINT BUTTON (+) ---
        // We find the ImageView from your XML and tell it to open ActivityAddPoint
        ImageView btnAdd = findViewById(R.id.btnAdd);
        btnAdd.setOnClickListener(v -> {
            Intent intent = new Intent(ActivityHome.this, ActivityAddPoint.class);
            startActivity(intent);
        });

        // Home Button Centers on Current Location
        findViewById(R.id.btnHome).setOnClickListener(v -> getDeviceLocation());

        // Search Button
        findViewById(R.id.btnSearch).setOnClickListener(v -> openSearchFragment(new SearchFragment()));
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
        updateLocationUI();
        getDeviceLocation();
    }

    private void updateLocationUI() {
        if (mMap == null) return;
        try {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                mMap.setMyLocationEnabled(true);
                mMap.getUiSettings().setMyLocationButtonEnabled(false);
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
            }
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }

    private void getDeviceLocation() {
        try {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                fusedLocationClient.getLastLocation().addOnSuccessListener(this, location -> {
                    if (location != null) {
                        LatLng currentLatLng = new LatLng(location.getLatitude(), location.getLongitude());
                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 15f));
                    }
                });
            }
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }

    private void openSearchFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.main, fragment)
                .addToBackStack(null)
                .commit();
    }
}