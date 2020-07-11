package com.example.medapptest.model;

public class GeoFenceData {
    private String name;
    private double lat, lon, accuracy;

    public GeoFenceData() {
    }

    public GeoFenceData(String name, double lat, double lon, double accuracy ) {
        this.name = name;
        this.lat = lat;
        this.lon = lon;
        this.accuracy = accuracy;
    }

    public String getName() {
        return name;
    }

    public double getLon() {
        return lon;
    }

    public double getLat() {
        return lat;
    }

    public double getAccuracy() {
        return accuracy;
    }
}
