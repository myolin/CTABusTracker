package com.myolin.ctabustracker.Utils;

public class Utility {

    private static final double EARTH_RADIUS = 6371; // in km

    public static double getDistance(double lat1, double lon1, double lat2, double lon2) {
        double latDistance  = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return EARTH_RADIUS * c * 1000; // convert to meters
    }

    // lat1, lon1 - starting location (your current location)
    // lat2, lon2 - bus stop location
    // bearing from current location to bus stop location
    public static String getBearing(double lat1, double lon1, double lat2, double lon2) {
        double lat1Rad = Math.toRadians(lat1);
        double lon1Rad = Math.toRadians(lon1);
        double lat2Rad = Math.toRadians(lat2);
        double lon2Rad = Math.toRadians(lon2);

        // calculate difference in longitude
        double deltaLon = lon2Rad - lon1Rad;

        // Calculate the bearing using the formula
        double y = Math.sin(deltaLon) * Math.cos(lat2Rad);
        double x = Math.cos(lat1Rad) * Math.sin(lat2Rad) -
                Math.sin(lat1Rad) * Math.cos(lat2Rad) * Math.cos(deltaLon);
        double bearingRad = Math.atan2(y, x);

        // Convert the bearing from radians to degrees
        double bearingDeg = Math.toDegrees(bearingRad);

        // Normalize the bearing to 0 - 360)
        double degrees = (bearingDeg + 360) % 360;

        if (degrees >= 337.5 || degrees < 22.5)
            return "north";
        if (degrees >= 22.5 && degrees < 67.5)
            return "northeast";
        if (degrees >= 67.5 && degrees < 112.5)
            return "east";
        if (degrees >= 112.5 && degrees < 157.5)
            return "southeast";
        if (degrees >= 157.5 && degrees < 202.5)
            return "south";
        if (degrees >= 202.5 && degrees < 247.5)
            return "southwest";
        if (degrees >= 247.5 && degrees < 292.5)
            return "west";
        if (degrees >= 292.5)
            return "northwest";
        return "X"; // default "X" for undefined bearing
    }
}
