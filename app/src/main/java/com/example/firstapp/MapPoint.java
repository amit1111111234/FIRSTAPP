package com.example.firstapp;

public class MapPoint {
    public String title;
    public String description;
    public double latitude;
    public double longitude;
    public String userId;

    public MapPoint() {} // Required for Firebase

    public MapPoint(String title, String description, double lat, double lng, String userId) {
        this.title = title;
        this.description = description;
        this.latitude = lat;
        this.longitude = lng;
        this.userId = userId;
    }
}
