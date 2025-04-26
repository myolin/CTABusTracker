package com.myolin.ctabustracker.Model;

import androidx.annotation.NonNull;

import com.google.gson.Gson;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

public class Route implements Serializable {

    private final String routeNum;
    private final String routeName;
    private final String routeColor;
    private final ArrayList<String> directions;
    private final HashMap<String, ArrayList<Stop>> stops;

    public Route(String routeNum, String routeName, String routeColor) {
        this.routeNum = routeNum;
        this.routeName = routeName;
        this.routeColor = routeColor;
        this.directions = new ArrayList<>();
        this.stops = new HashMap<>();
    }

    public String getRouteNum() {
        return routeNum;
    }

    public String getRouteName() {
        return routeName;
    }

    public String getRouteColor() {
        return routeColor;
    }

    public ArrayList<String> getDirections() {
        return directions;
    }

    public void addDirections(ArrayList<String> directions) {
        this.directions.addAll(directions);
    }

    // key -> direction (NorthBound, EastBound, etc..)
    public void addStops(String key, ArrayList<Stop> value) {
        this.stops.put(key, value);
    }

    public ArrayList<Stop> getStops(String key) {
        return stops.get(key);
    }

    @NonNull
    @Override
    public String toString() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }
}
