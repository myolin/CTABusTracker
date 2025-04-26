package com.myolin.ctabustracker.Model;

import java.io.Serializable;

public class Stop implements Serializable {

    private final String stopId;
    private final String stopName;
    private final double stopLatitude;
    private final double stopLongitude;

    public Stop(String stopId, String stopName, double stopLatitude, double stopLongitude) {
        this.stopId = stopId;
        this.stopName = stopName;
        this.stopLatitude = stopLatitude;
        this.stopLongitude = stopLongitude;
    }

    public String getStopId() {
        return stopId;
    }

    public String getStopName() {
        return stopName;
    }

    public double getStopLatitude() {
        return stopLatitude;
    }

    public double getStopLongitude() {
        return stopLongitude;
    }

}
