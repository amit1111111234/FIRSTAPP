package com.example.firstapp;

import java.io.Serializable;

public class MapPoint implements Serializable {
    public String title;
    public String description;
    public double latitude;
    public double longitude;
    public String userId;
    public String locationName;

    public MapPoint() {}

    public MapPoint(String title, String description, double lat, double lng, String userId, String locationName) {
        this.title = title;
        this.description = description;
        this.latitude = lat;
        this.longitude = lng;
        this.userId = userId;
        this.locationName = locationName;
    }
}