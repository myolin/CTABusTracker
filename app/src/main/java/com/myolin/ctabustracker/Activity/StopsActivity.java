package com.myolin.ctabustracker.Activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.ColorUtils;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.myolin.ctabustracker.Adapter.StopAdapter;
import com.myolin.ctabustracker.Ads.StopBannerUnityInitializationListener;
import com.myolin.ctabustracker.Ads.StopBannerViewListener;
import com.myolin.ctabustracker.Model.Alert;
import com.myolin.ctabustracker.Model.Route;
import com.myolin.ctabustracker.Model.Stop;
import com.myolin.ctabustracker.R;
import com.myolin.ctabustracker.Utils.Utility;
import com.myolin.ctabustracker.Volley.AlertsVolley;
import com.myolin.ctabustracker.databinding.ActivityStopsBinding;
import com.unity3d.ads.UnityAds;
import com.unity3d.services.banners.BannerView;
import com.unity3d.services.banners.UnityBannerSize;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Locale;
import java.util.TreeMap;

public class StopsActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "StopsActivity";

    private ActivityStopsBinding binding;
    private Route r;
    private String direction;
    private int backgroundColor;
    private int textColor;

    private final ArrayList<Stop> allStops = new ArrayList<>();
    private final ArrayList<Stop> displayStops = new ArrayList<>();

    private StopAdapter stopAdapter;

    private SharedPreferences alert_cache;
    private final ArrayList<String> alertsId = new ArrayList<>();

    private double latitude;
    private double longitude;

    private static final String unityGameID = "5781747";
    private static final boolean testMode = false;
    private static final String bannerPlacement = "Banner_Android_2";
    private BannerView.IListener bannerListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityStopsBinding.inflate(getLayoutInflater());
        setContentView(binding.main);
        ViewCompat.setOnApplyWindowInsetsListener(binding.main, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        alert_cache = getSharedPreferences("ALERT_CACHE", Context.MODE_PRIVATE);
        loadAlertsId();

        Intent intent = getIntent();

        if (intent.hasExtra("ROUTE") && intent.hasExtra("DIRECTION") &&
            intent.hasExtra("LATITUDE") && intent.hasExtra("LONGITUDE")) {
            r = (Route) intent.getSerializableExtra("ROUTE");
            direction = intent.getStringExtra("DIRECTION");
            latitude = intent.getDoubleExtra("LATITUDE", 0);
            longitude = intent.getDoubleExtra("LONGITUDE", 0);

            if (r != null && direction != null) {
                AlertsVolley.loadAlerts(this, r.getRouteNum());

                this.binding.toolbar2.setTitle("\tRoute " + r.getRouteNum() + " - " + r.getRouteName());
                this.binding.toolbar2.setTitleTextColor(Color.WHITE);
                setSupportActionBar(binding.toolbar2);
                if (getSupportActionBar() != null) {
                    getSupportActionBar().setIcon(R.drawable.bus_icon);
                }

                backgroundColor = Color.parseColor(r.getRouteColor());
                float luminance = (float) ColorUtils.calculateLuminance(backgroundColor);
                textColor = Color.BLACK;
                if (luminance < 0.25) {
                    textColor = Color.WHITE;
                }

                this.binding.stopDirection.setBackgroundColor(backgroundColor);
                this.binding.stopDirection.setText(String.format(Locale.getDefault(), "%s Stops", direction));
                this.binding.stopDirection.setTextColor(textColor);

                stopAdapter = new StopAdapter(displayStops, this, backgroundColor,
                        textColor, latitude, longitude);
                binding.recycler2.setAdapter(stopAdapter);
                binding.recycler2.setLayoutManager(new LinearLayoutManager(this));

                allStops.addAll(r.getStops(direction));
                filterStopsWithinDistance();
            }
        }

        bannerListener = new StopBannerViewListener(this);
        // Initialize the Unity Ads SDK
        UnityAds.initialize(this, unityGameID, testMode,
                new StopBannerUnityInitializationListener(this));
    }

    private void filterStopsWithinDistance() {
        double minDistance = Double.parseDouble(getString(R.string.MIN_DISTANCE)); // meters
        TreeMap<Double, Stop> temp = new TreeMap<>();
        for (Stop s : allStops) {
            double dist = Utility.getDistance(latitude, longitude, s.getStopLatitude(), s.getStopLongitude());
            if (dist <= minDistance) {
                temp.put(dist, s);
            }
        }
        displayStops.addAll(new ArrayList<>(temp.values()));
        stopAdapter.notifyItemRangeChanged(0, displayStops.size());
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent(this, PredictionsActivity.class);
        int pos = binding.recycler2.getChildLayoutPosition(v);
        intent.putExtra("STOP", displayStops.get(pos));
        intent.putExtra("RT", r.getRouteNum());
        intent.putExtra("RT_NAME", r.getRouteName());
        intent.putExtra("DIR", direction);
        intent.putExtra("BG_COLOR", backgroundColor);
        intent.putExtra("TEXT_COLOR", textColor);
        startActivity(intent);
    }

    private void loadAlertsId() {
        if (alert_cache.contains("alertsId")) {
            Gson gson = new Gson();
            String json = alert_cache.getString("alertsId", null);
            Type type = new TypeToken<ArrayList<String>>() {}.getType();
            alertsId.addAll(gson.fromJson(json, type));
        }
    }

    public void acceptAlerts(ArrayList<Alert> alerts) {
        for (Alert alert : alerts) {
            if (!alertsId.contains(alert.getId())) {
                alertsId.add(alert.getId());
                showDialog(alert.getHeadline(), alert.getMessage());
            }
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        SharedPreferences.Editor editor = alert_cache.edit();
        Gson gson = new Gson();
        String json = gson.toJson(alertsId);
        editor.putString("alertsId", json);
        editor.apply();
        Log.d(TAG, "onStop: AlertsID = " + alertsId);
    }

    private void showDialog(String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title);
        builder.setIcon(R.drawable.bus_icon_black);

        Spanned styledMessage = Html.fromHtml(message, Html.FROM_HTML_MODE_LEGACY);
        builder.setMessage(styledMessage);

        builder.setPositiveButton("OK", ((dialog, which) -> {}));

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    public void showBanner() {
        BannerView bottomBanner = new BannerView(this, bannerPlacement,
                UnityBannerSize.getDynamicSize(this));
        bottomBanner.setListener(bannerListener);

        binding.banner2.addView(bottomBanner);
        bottomBanner.load();
    }

    public void initFailed(String s) {
        Toast.makeText(this, s, Toast.LENGTH_SHORT).show();
    }

    public void loadFailed(String s) {
        Toast.makeText(this, s, Toast.LENGTH_SHORT).show();
    }
}