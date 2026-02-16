package com.example.firstapp;

import java.io.Serializable;

// Implementing Serializable allows us to pass this object to the Fragment
public class MapPoint implements Serializable {
    public String title;
    public String description;
    public double latitude;
    public double longitude;
    public String userId;
    public String locationName; // Added for the location display

    public MapPoint() {} // Required for Firebase

    public MapPoint(String title, String description, double lat, double lng, String userId, String locationName) {
        this.title = title;
        this.description = description;
        this.latitude = lat;
        this.longitude = lng;
        this.userId = userId;
        this.locationName = locationName;
    }
}