package com.myolin.ctabustracker.Volley;

import android.net.Uri;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.myolin.ctabustracker.Activity.StopsActivity;
import com.myolin.ctabustracker.Model.Alert;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class AlertsVolley {

    private static final String TAG = "AlertsVolley";

    private static final String ALERTS_URL = "https://www.transitchicago.com/api/1.0/alerts.aspx";

    private static StopsActivity stopsActivity;

    public static void loadAlerts(StopsActivity stopsActivityIn, String routeNum) {
        stopsActivity = stopsActivityIn;
        RequestQueue queue = Volley.newRequestQueue(stopsActivity);

        Uri.Builder buildURL = Uri.parse(ALERTS_URL).buildUpon();
        buildURL.appendQueryParameter("routeid", routeNum);
        buildURL.appendQueryParameter("activeonly", "true");
        buildURL.appendQueryParameter("outputType", "JSON");
        String urlToUse = buildURL.build().toString();

        // response listener
        Response.Listener<JSONObject> listener = response -> parseAlertsJSON(response.toString());

        // error listener
        Response.ErrorListener error = error1 -> {};

        // Request a string response from the provided URL
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, urlToUse,
                null, listener, error);

        // Add the request to the Request Queue
        queue.add(jsonObjectRequest);
    }

    private static void parseAlertsJSON(String s) {
        ArrayList<Alert> alerts = new ArrayList<>();

        try {
            JSONObject jObjMain = new JSONObject(s);

            // "CTAAlerts" object
            JSONObject ctaAlerts = jObjMain.getJSONObject("CTAAlerts");

            // "CTAAlerts" -> "Alerts" JsonArray
            JSONArray alertsArray = ctaAlerts.getJSONArray("Alert");
            for (int i = 0; i < alertsArray.length(); i++) {
                JSONObject alertObj = (JSONObject) alertsArray.get(i);

                // get all data fields of a single alert
                String id = alertObj.getString("AlertId");
                String headline = alertObj.getString("Headline");

                JSONObject fullDescriptionObj = alertObj.getJSONObject("FullDescription");
                String message=  fullDescriptionObj.getString("#cdata-section");

                alerts.add(new Alert(id, headline, message));
            }
            stopsActivity.acceptAlerts(alerts);
        } catch (Exception e) {
            Log.d(TAG, "parseAlertsJSON: " + e.getMessage());
        }
    }
}
