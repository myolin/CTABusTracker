package com.myolin.ctabustracker.Activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.myolin.ctabustracker.Adapter.PredictionAdapter;
import com.myolin.ctabustracker.Ads.PredictionBannerViewListener;
import com.myolin.ctabustracker.Ads.PredictionUnityInitializationListener;
import com.myolin.ctabustracker.Model.Prediction;
import com.myolin.ctabustracker.Model.Stop;
import com.myolin.ctabustracker.Model.Vehicle;
import com.myolin.ctabustracker.R;
import com.myolin.ctabustracker.Utils.Utility;
import com.myolin.ctabustracker.Volley.BusVolley;
import com.myolin.ctabustracker.databinding.ActivityPredictionsBinding;
import com.unity3d.ads.UnityAds;
import com.unity3d.services.banners.BannerView;
import com.unity3d.services.banners.UnityBannerSize;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class PredictionsActivity extends AppCompatActivity implements View.OnClickListener {

    private ActivityPredictionsBinding binding;
    private Stop stop;
    private String routeNum;
    private Prediction selectedPrediction;

    private static final String unityGameID = "5781747";
    private static final boolean testMode = false;
    private static final String bannerPlacement = "Banner_Android_3";
    private BannerView.IListener bannerListener;

    private final ArrayList<Prediction> displayPredictions = new ArrayList<>();

    private PredictionAdapter predictionAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPredictionsBinding.inflate(getLayoutInflater());
        setContentView(binding.main);
        ViewCompat.setOnApplyWindowInsetsListener(binding.main, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Intent intent = getIntent();

        stop = (Stop) intent.getSerializableExtra("STOP");
        routeNum = intent.getStringExtra("RT");
        String routeName = intent.getStringExtra("RT_NAME");
        String direction = intent.getStringExtra("DIR");
        int backgroundColor = intent.getIntExtra("BG_COLOR", Color.WHITE);
        int textColor = intent.getIntExtra("TEXT_COLOR", Color.BLACK);

        predictionAdapter = new PredictionAdapter(displayPredictions, this,
                backgroundColor, textColor);
        binding.recycler3.setAdapter(predictionAdapter);
        binding.recycler3.setLayoutManager(new LinearLayoutManager(this));

        BusVolley.loadPredictions(routeNum, stop.getStopId(), this);

        binding.toolbar3.setTitle("\tRoute" + routeNum + " - " + routeName);
        binding.toolbar3.setTitleTextColor(Color.WHITE);
        setSupportActionBar(binding.toolbar3);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setIcon(R.drawable.bus_icon);
        }

        binding.predictionsStopName.setText(String.format(Locale.getDefault(), "%s (%s)",
                stop.getStopName(), direction));
        binding.predictionsStopName.setTextColor(textColor);
        binding.predictionsStopName.setBackgroundColor(backgroundColor);

        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("hh:mm:ss a");
        binding.predictionsCurrentTime.setText(String.format(Locale.getDefault(), "%s",
                sdf.format(new Date())));
        binding.predictionsCurrentTime.setTextColor(textColor);
        binding.predictionsCurrentTime.setBackgroundColor(backgroundColor);

        bannerListener = new PredictionBannerViewListener(this);
        // Initialize the Unity Ads SDK
        UnityAds.initialize(this, unityGameID, testMode,
                new PredictionUnityInitializationListener(this));

        binding.main.setOnRefreshListener(() -> {
            BusVolley.loadPredictions(routeNum, stop.getStopId(), this);
            binding.predictionsCurrentTime.setText(String.format(Locale.getDefault(), "%s",
                    sdf.format(new Date())));
        });
    }

    public void acceptPredictions(ArrayList<Prediction> predictions) {
        displayPredictions.clear();
        displayPredictions.addAll(predictions);
        predictionAdapter.notifyItemRangeChanged(0, displayPredictions.size());
        binding.main.setRefreshing(false);
    }

    @Override
    public void onClick(View v) {
        int pos = binding.recycler3.getChildLayoutPosition(v);
        selectedPrediction = displayPredictions.get(pos);
        BusVolley.loadVehicles(selectedPrediction.getVehicleId());
    }

    public void acceptVehicle(Vehicle v) {
        String vehicleId = selectedPrediction.getVehicleId();
        String arrivalTimeInMinutes = selectedPrediction.getArrivalInMinutes();
        double distanceStopToBus = Utility.getDistance(stop.getStopLatitude(),stop.getStopLongitude(),
                v.getLatitude(), v.getLongitude());
        String distance;
        if (distanceStopToBus >= 1000) {
            distance = String.format(Locale.getDefault(), "%.1f kilometers", (distanceStopToBus/1000));
        } else {
            distance = String.format(Locale.getDefault(), "%.1f meters", distanceStopToBus);
        }

        String title = "Bus #" + vehicleId;
        String message = "Bus #" + vehicleId + " is " + distance + " (" + arrivalTimeInMinutes  +
                " min) away " + "from the " + stop.getStopName() + " stop.";

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setIcon(R.drawable.bus_icon_black);
        builder.setNeutralButton("SHOW ON MAP", (dialog, which) ->
                goToMap(v.getLatitude(), v.getLongitude(), vehicleId));
        builder.setPositiveButton("OK", (dialog, which) -> {});


        AlertDialog alertDialog = builder.create();
        alertDialog.setCancelable(false);
        alertDialog.setCanceledOnTouchOutside(false);

        alertDialog.show();
    }

    public void goToMap(double latitude, double longitude, String vehicleId) {
        String geo = "geo:" + latitude + longitude + "?q=" + latitude + "," + longitude + "(Bus " +
                 vehicleId + ")";
        Uri mapUri = Uri.parse(geo);

        Intent mapIntent = new Intent(Intent.ACTION_VIEW, mapUri);

        // Check if there is an app that can handle geo intents
        if (mapIntent.resolveActivity(getPackageManager()) != null) {
            startActivity(mapIntent);
        } else {
            Toast.makeText(this, "No Application found that handles ACTION_VIEW (geo) intents", Toast.LENGTH_SHORT).show();
        }
        //Log.d("PredictionsActivity", "goToMap: lat = " + latitude + ", long = " + longitude);
        Log.d("PredictionsActivity", "goToMap: geoUriString = " + geo);
    }

    public void showBanner() {
        BannerView bottomBanner = new BannerView(this, bannerPlacement,
                UnityBannerSize.getDynamicSize(this));
        bottomBanner.setListener(bannerListener);

        binding.banner3.addView(bottomBanner);
        bottomBanner.load();
    }

    public void initFailed(String s) {
        Toast.makeText(this, s, Toast.LENGTH_SHORT).show();
    }

    public void loadFailed(String s) {
        Toast.makeText(this, s, Toast.LENGTH_SHORT).show();
    }

}