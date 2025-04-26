package com.myolin.ctabustracker.Model;

public class Vehicle {

    private final double latitude;
    private final double longitude;

    public Vehicle(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }
}
