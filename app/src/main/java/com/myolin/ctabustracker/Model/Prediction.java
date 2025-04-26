package com.myolin.ctabustracker.Model;

public class Prediction {

    private final String vehicleId;
    private final String routeDirection;
    private final String destination;
    private final String arrivalTime;
    private final boolean isDelayed;
    private final String arrivalInMinutes;

    public Prediction(String vehicleId, String routeDirection, String destination,
                      String arrivalTime, boolean isDelayed, String arrivalInMinutes) {
        this.vehicleId = vehicleId;
        this.routeDirection = routeDirection;
        this.destination = destination;
        this.arrivalTime = arrivalTime;
        this.isDelayed = isDelayed;
        this.arrivalInMinutes = arrivalInMinutes;
    }

    public String getVehicleId() {
        return vehicleId;
    }

    public String getRouteDirection() {
        return routeDirection;
    }

    public String getRouteDestination() {
        return destination;
    }

    public String getArrivalTime() {
        return arrivalTime;
    }

    public boolean isDelayed() {
        return isDelayed;
    }

    public String getArrivalInMinutes() {
        return arrivalInMinutes;
    }
}
