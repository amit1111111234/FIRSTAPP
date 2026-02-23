package com.example.firstapp;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

public class ActivityHome extends AppCompatActivity implements OnMapReadyCallback, SearchFragment.OnSearchClickListener {

    private GoogleMap mMap;
    private FusedLocationProviderClient fusedLocationClient;
    private TextView tvWelcome; // This will handle the user name display

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // INITIALIZE VIEW - matching the ID in your XML
        tvWelcome = findViewById(R.id.tvWelcome);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) mapFragment.getMapAsync(this);

        // Fetch name from Firestore immediately
        fetchAndShowName();

        findViewById(R.id.btnAdd).setOnClickListener(v -> startActivity(new Intent(this, ActivityAddPoint.class)));
        findViewById(R.id.btnHome).setOnClickListener(v -> getDeviceLocation());
        findViewById(R.id.btnSearch).setOnClickListener(v -> {
            getSupportFragmentManager().beginTransaction()
                    .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
                    .replace(R.id.fragment_container, new SearchFragment())
                    .addToBackStack(null)
                    .commit();
        });
    }

    @Override
    public void onPointSelected(MapPoint p) {
        if (p != null && mMap != null) {
            LatLng location = new LatLng(p.latitude, p.longitude);
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(location, 17f), 1000, null);
            PointDetailsDialogFragment dialog = PointDetailsDialogFragment.newInstance(p);
            dialog.show(getSupportFragmentManager(), "point_details");
        }
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
            getDeviceLocation();
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }
        loadPointsFromFirebase();
        mMap.setOnMarkerClickListener(marker -> {
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(marker.getPosition(), 15f));
            MapPoint p = (MapPoint) marker.getTag();
            if (p != null) {
                onPointSelected(p);
            }
            return true;
        });
    }

    private void loadPointsFromFirebase() {
        FirebaseFirestore.getInstance().collection("Points")
                .addSnapshotListener((value, error) -> {
                    if (error != null) return;
                    if (value != null && mMap != null) {
                        mMap.clear();
                        for (QueryDocumentSnapshot doc : value) {
                            MapPoint p = doc.toObject(MapPoint.class);
                            Marker m = mMap.addMarker(new MarkerOptions()
                                    .position(new LatLng(p.latitude, p.longitude))
                                    .title(p.title)
                                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
                            if (m != null) m.setTag(p);
                        }
                    }
                });
    }

    private void getDeviceLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.getLastLocation().addOnSuccessListener(location -> {
                if (location != null && mMap != null) {
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 15f));
                }
            });
        }
    }

    private void fetchAndShowName() {
        String uid = FirebaseAuth.getInstance().getUid();
        if (uid != null) {
            FirebaseFirestore.getInstance().collection("Users").document(uid)
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists() && tvWelcome != null) {
                            String name = documentSnapshot.getString("name");
                            if (name != null && !name.isEmpty()) {
                                tvWelcome.setText(name);
                            }
                        }
                    });
        }
    }
}