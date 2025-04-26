package com.myolin.ctabustracker.Volley;

import android.annotation.SuppressLint;
import android.net.Uri;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.myolin.ctabustracker.Activity.MainActivity;
import com.myolin.ctabustracker.Activity.PredictionsActivity;
import com.myolin.ctabustracker.Model.Prediction;
import com.myolin.ctabustracker.Model.Route;
import com.myolin.ctabustracker.Model.Stop;
import com.myolin.ctabustracker.Model.Vehicle;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class BusVolley {

    private static final String TAG = "BusVolley";

    private static final String API_KEY = "r79sRhbQAGWfd7agRgxv4MNNz";

    private static final String ROUTES_URL = "https://www.ctabustracker.com/bustime/api/v2/getroutes";
    private static final String ROUTES_DIRECTION_URL = "https://www.ctabustracker.com/bustime/api/v2/getdirections";
    private static final String STOPS_URL = "https://www.ctabustracker.com/bustime/api/v2/getstops";
    private static final String PREDICTIONS_URL = "https://www.ctabustracker.com/bustime/api/v2/getpredictions";
    private static final String VEHICLES_URL = "https://www.ctabustracker.com/bustime/api/v2/getvehicles";

    @SuppressLint("StaticFieldLeak")
    private static MainActivity mainActivity;
    private static PredictionsActivity predictionsActivity;
    private static RequestQueue queue;
    private static RequestQueue queue2;

    private static final ArrayList<Route> updated = new ArrayList<>();

    public static void loadBusRoutes(MainActivity mainActivityIn) {
        mainActivity = mainActivityIn;
        queue = Volley.newRequestQueue(mainActivity);

        Uri.Builder buildURL = Uri.parse(ROUTES_URL).buildUpon();
        buildURL.appendQueryParameter("key", API_KEY);
        buildURL.appendQueryParameter("format", "json");
        String urlToUse = buildURL.build().toString();

        // response listener
        Response.Listener<JSONObject> listener = response -> parseBusRoutesJSON(response.toString());

        // error listener
        Response.ErrorListener error = error1 -> {
            mainActivity.acceptFail("ALL ROUTES");
            Log.d(TAG, "loadBusRoutes: " + error1.toString());
        };

        // Request a string response from the provided URL
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, urlToUse,
                null, listener, error);

        //Add the request to the Request queue
        queue.add(jsonObjectRequest);
    }

    private static void parseBusRoutesJSON(String s) {
        ArrayList<Route> routes = new ArrayList<>();

        try {
            JSONObject jObjMain = new JSONObject(s);

            // "bustime-response" object
            JSONObject bustimeResponse = jObjMain.getJSONObject("bustime-response");

            // "routes" json array section
            JSONArray routesArray = bustimeResponse.getJSONArray("routes");
            for(int i = 0; i < routesArray.length(); i++) {
                JSONObject routeObj = (JSONObject) routesArray.get(i);

                // get all data fields of a single route
                String routeNum = routeObj.getString("rt");
                String routeName = routeObj.getString("rtnm");
                String routeColor = routeObj.getString("rtclr");

                Route r = new Route(routeNum, routeName, routeColor);
                routes.add(r);
            }
            mainActivity.acceptRoutes(routes);
            Log.d(TAG, "parseBusRoutesJSON: " + routes);
        } catch (Exception e) {
            Log.d(TAG, "parseBusRoutesJSON: error " + e.getMessage());
        }
    }

    public static void downloadAllRouteDirection(ArrayList<Route> routes) {
        updated.clear();
        updated.addAll(routes);
        for (int i = 0; i < routes.size(); i++) {
            loadRouteDirection(routes.get(i).getRouteNum(), i);
        }
    }

    public static void acceptDir(ArrayList<String> directions, int i) {
        updated.get(i).addDirections(directions);
        if (i == updated.size() - 1) {
            mainActivity.acceptDirections(updated);
        }
    }

    public static void loadRouteDirection(String routeNum, int i) {
        Uri.Builder buildURL = Uri.parse(ROUTES_DIRECTION_URL).buildUpon();
        buildURL.appendQueryParameter("key", API_KEY);
        buildURL.appendQueryParameter("format", "json");
        buildURL.appendQueryParameter("rt", routeNum);
        String urlToUse = buildURL.build().toString();

        // response listener
        Response.Listener<JSONObject> listener = response -> parseRouteDirJSON(response.toString(), i);

        // error listener
        Response.ErrorListener error = error1 -> {
            mainActivity.acceptFail("ALL ROUTE DIRECTIONS");
            Log.d(TAG, "loadRouteDirection: " + error1.toString());
        };

        // Request a string response from the provided URL
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, urlToUse,
                null, listener, error);

        //Add the request to the Request queue
        queue.add(jsonObjectRequest);
    }

    private static void parseRouteDirJSON(String s, int pos) {
        ArrayList<String> directions = new ArrayList<>();

        try {
            JSONObject jObjMain = new JSONObject(s);

            // "bustime-response" object
            JSONObject bustimeResponse = jObjMain.getJSONObject("bustime-response");

            // "directions" json array section
            JSONArray directionsArray = bustimeResponse.getJSONArray("directions");
            for (int i = 0; i < directionsArray.length(); i++) {
                JSONObject dirObj = (JSONObject) directionsArray.get(i);

                // get all data fields of a single direction
                String dir = dirObj.getString("dir");

                directions.add(dir);

                //loadStops(dir, r);
            }
            acceptDir(directions, pos);
        } catch (Exception e) {
            Log.d(TAG, "parseRouteDirJSON: " + e.getMessage());
        }
    }

    public static void downloadAllStops(ArrayList<Route> routes) {
        updated.clear();
        updated.addAll(routes);
        for (int i = 0; i < routes.size(); i++) {
            for (String dir : routes.get(i).getDirections()) {
                loadStops(dir, routes.get(i).getRouteNum(), i);
            }
        }
    }

    public static void acceptStops(String direction, ArrayList<Stop> stops, int i) {
        updated.get(i).addStops(direction, stops);
        if (i == updated.size() - 1) {
            mainActivity.acceptStops(updated);
        }
    }

    public static void loadStops(String direction, String routeNum, int i) {
        Uri.Builder buildURL = Uri.parse(STOPS_URL).buildUpon();
        buildURL.appendQueryParameter("key", API_KEY);
        buildURL.appendQueryParameter("format", "json");
        buildURL.appendQueryParameter("rt", routeNum);
        buildURL.appendQueryParameter("dir", direction);
        String urlToUse = buildURL.build().toString();

        // response listener
        Response.Listener<JSONObject> listener = response -> parseStopsJSON(response.toString(), direction, i);

        // error listener
        Response.ErrorListener error = error1 -> {
            mainActivity.acceptFail("ALL STOPS");
            Log.d(TAG, "loadStops: " + error1.toString());
        };

        // Request a string response from the provided URL
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, urlToUse,
                null, listener, error);

        //Add the request to the Request queue
        queue.add(jsonObjectRequest);
    }

    private static void parseStopsJSON(String s, String direction, int pos) {
        ArrayList<Stop> stops = new ArrayList<>();

        try {
            JSONObject jObjMain = new JSONObject(s);

            // "bustime-response" object
            JSONObject bustimeResponse = jObjMain.getJSONObject("bustime-response");

            // "stops" json array section
            JSONArray stopsArray = bustimeResponse.getJSONArray("stops");
            for (int i = 0; i < stopsArray.length(); i++) {
                JSONObject stopObj = (JSONObject) stopsArray.get(i);

                // get all data fields of a single stop
                String stopId = stopObj.getString("stpid");
                String stopName = stopObj.getString("stpnm");
                double stopLatitude = stopObj.getDouble("lat");
                double stopLongitude = stopObj.getDouble("lon");

                stops.add(new Stop(stopId, stopName, stopLatitude, stopLongitude));
            }
            acceptStops(direction, stops, pos);
        } catch (Exception e) {
            Log.d(TAG, "parseStopsJSON: " + e.getMessage());
        }
    }

    public static void loadPredictions(String routeNum, String stopId, PredictionsActivity predictionsActivityIn) {
        predictionsActivity = predictionsActivityIn;
        queue2 = Volley.newRequestQueue(predictionsActivity);

        Uri.Builder buildURL = Uri.parse(PREDICTIONS_URL).buildUpon();
        buildURL.appendQueryParameter("key", API_KEY);
        buildURL.appendQueryParameter("format", "json");
        buildURL.appendQueryParameter("rt", routeNum);
        buildURL.appendQueryParameter("stpid", stopId);
        String urlToUse = buildURL.build().toString();

        // response listener
        Response.Listener<JSONObject> listener = response -> parsePredictionsJSON(response.toString());

        // error listener
        Response.ErrorListener error = error1 -> Log.d(TAG, "loadPredictions: " + error1.toString());

        // Request a string response from the provided URL
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, urlToUse,
                null, listener, error);

        //Add the request to the Request queue
        queue2.add(jsonObjectRequest);
    }

    private static void parsePredictionsJSON(String s) {
        ArrayList<Prediction> predictions = new ArrayList<>();

        try {
            JSONObject jObjMain = new JSONObject(s);

            // "bustime-response" object
            JSONObject bustimeResponse = jObjMain.getJSONObject("bustime-response");

            // "prd" json array section
            JSONArray predictionsArray = bustimeResponse.getJSONArray("prd");
            for (int i = 0; i < predictionsArray.length(); i++) {
                JSONObject prdObj = (JSONObject) predictionsArray.get(i);

                // get all data fields of a single prediction
                String vehicleId = prdObj.getString("vid");
                String routeDirection = prdObj.getString("rtdir");
                String destination = prdObj.getString("des");
                String arrivalTime = prdObj.getString("prdtm");
                boolean isDelayed = prdObj.getBoolean("dly");
                String arrivalInMinutes = prdObj.getString("prdctdn");

                predictions.add(new Prediction(vehicleId, routeDirection, destination, arrivalTime,
                        isDelayed, arrivalInMinutes));
            }
            Log.d(TAG, "parsePredictionsJSON: prediction size = " + predictions.size());
            predictionsActivity.acceptPredictions(predictions);
        } catch (Exception e) {
            Log.d(TAG, "parsePredictionsJSON: " + e.getMessage());
        }
    }

    public static void loadVehicles(String vehicleId) {
        Uri.Builder buildURL = Uri.parse(VEHICLES_URL).buildUpon();
        buildURL.appendQueryParameter("key", API_KEY);
        buildURL.appendQueryParameter("format", "json");
        buildURL.appendQueryParameter("vid", vehicleId);
        String urlToUse = buildURL.build().toString();

        // response listener
        Response.Listener<JSONObject> listener = response -> parseVehiclesJSON(response.toString());

        // error listener
        Response.ErrorListener error = error1 -> Log.d(TAG, "loadVehicles: " + error1.toString());

        // Request a string response from the provided URL
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, urlToUse,
                null, listener, error);

        //Add the request to the Request queue
        queue2.add(jsonObjectRequest);
    }

    private static void parseVehiclesJSON(String s) {
        ArrayList<Vehicle> vehicles = new ArrayList<>();
        try {
            JSONObject jObjMain = new JSONObject(s);

            // "bustime-response" object
            JSONObject bustimeResponse = jObjMain.getJSONObject("bustime-response");

            // "vehicle" json array section
            JSONArray vehiclesArray = bustimeResponse.getJSONArray("vehicle");
            for (int i = 0; i < vehiclesArray.length(); i++) {
                JSONObject vObj = (JSONObject) vehiclesArray.get(i);

                // get all data fields of a single prediction
                double latitude = vObj.getDouble("lat");
                double longitude = vObj.getDouble("lon");

                vehicles.add(new Vehicle(latitude, longitude));
            }
            predictionsActivity.acceptVehicle(vehicles.get(0));
        } catch (Exception e) {
            Log.d(TAG, "parseVehiclesJSON: " + e.getMessage());
        }
    }

}
