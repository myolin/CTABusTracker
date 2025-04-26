package com.myolin.ctabustracker.Activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.util.Linkify;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.splashscreen.SplashScreen;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.myolin.ctabustracker.Adapter.RouteAdapter;
import com.myolin.ctabustracker.Ads.MainBannerViewListener;
import com.myolin.ctabustracker.Ads.MainUnityInitializationListener;
import com.myolin.ctabustracker.Model.Route;
import com.myolin.ctabustracker.R;
import com.myolin.ctabustracker.Volley.BusVolley;
import com.myolin.ctabustracker.databinding.ActivityMainBinding;
import com.unity3d.ads.UnityAds;
import com.unity3d.services.banners.BannerView;
import com.unity3d.services.banners.UnityBannerSize;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Objects;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "MainActivity";

    private static final int LOCATION_REQUEST = 111;
    private FusedLocationProviderClient fusedLocationClient;

    private ConnectivityManager connectivityManager;

    private final ArrayList<Route> allRoutes = new ArrayList<>();
    private final ArrayList<Route> displayRoutes = new ArrayList<>();

    private SharedPreferences cache;

    private ActivityMainBinding binding;
    private RouteAdapter routeAdapter;
    private View selectedView;

    private static final String unityGameID = "5781747";
    private static final boolean testMode = false;
    private static final String bannerPlacement = "Banner_Android";
    private BannerView.IListener bannerListener;

    private boolean keepOn = true;
    private static final long minSplashTime = 2000;
    private long startTime;

    private double latitude;
    private double longitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        connectivityManager = getSystemService(ConnectivityManager.class);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        cache = getSharedPreferences("CACHE", Context.MODE_PRIVATE);
        binding = ActivityMainBinding.inflate(getLayoutInflater());

        routeAdapter = new RouteAdapter(displayRoutes, this);
        binding.recycler.setAdapter(routeAdapter);
        binding.recycler.setLayoutManager(new LinearLayoutManager(this));

        startTime = System.currentTimeMillis();

        if (checkNetwork()) {
            if (cache.contains("routes") && cache.contains("time")) {
                long currentTime = System.currentTimeMillis();
                long savedTime = cache.getLong("time", 0);
                if (currentTime - savedTime < 86400000) {
                    loadData();
                    Log.d(TAG, "onCreate: from cache");
                    Toast.makeText(this, "From Cache", Toast.LENGTH_SHORT).show();
                } else {
                    BusVolley.loadBusRoutes(this);
                    Log.d(TAG, "onCreate: from volley");
                    Toast.makeText(this, "From Volley", Toast.LENGTH_SHORT).show();
                }

            } else {
                BusVolley.loadBusRoutes(this);
                Log.d(TAG, "onCreate: from volley");
                Toast.makeText(this, "From Volley", Toast.LENGTH_SHORT).show();
            }
        }

        SplashScreen.installSplashScreen(this)
                .setKeepOnScreenCondition(() -> {
                            //Log.d(TAG, "shouldKeepOnScreen: " + (System.currentTimeMillis() - startTime));
                            return keepOn || (System.currentTimeMillis() - startTime <= minSplashTime);
                });

        setContentView(binding.main);
        ViewCompat.setOnApplyWindowInsetsListener(binding.main, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        if (checkAppPermissions()) {
            determineLocation();
        }

        binding.toolbar.setTitle(R.string.main_title);
        binding.toolbar.setTitleTextColor(Color.WHITE);
        Objects.requireNonNull(binding.toolbar.getOverflowIcon()).setTint(Color.WHITE);
        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setIcon(R.drawable.bus_icon);
        }

        addTextChangeListener();

        bannerListener = new MainBannerViewListener(this);
        // Initialize the Unity Ads SDK
        UnityAds.initialize(this, unityGameID, testMode,
                new MainUnityInitializationListener(this));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuItem info = menu.add("Info");
        info.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        info.setIcon(R.drawable.info_icon);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getTitle() == "Info") {
            infoDialog();
        }
        return super.onOptionsItemSelected(item);
    }

    // click listener called by recycler viewHolder clicks
    @Override
    public void onClick(View v) {
        selectedView = v;
        int pos = binding.recycler.getChildLayoutPosition(v);
        Route r = displayRoutes.get(pos);
        buildPopup(r);
    }

    private void buildPopup(Route r) {
        PopupMenu popupMenu = new PopupMenu(this, selectedView);

        // add(int groupId, int itemId, int order, int titleRes)
        for (int i = 0; i < r.getDirections().size(); i++) {
            popupMenu.getMenu().add(Menu.NONE, i, Menu.NONE, r.getDirections().get(i));
        }

        popupMenu.setOnMenuItemClickListener(menuItem -> {
            String selectedTitle = (String) menuItem.getTitle();
            Intent intent = new Intent(this, StopsActivity.class);
            intent.putExtra("ROUTE", r);
            intent.putExtra("DIRECTION", selectedTitle);
            intent.putExtra("LATITUDE", latitude);
            intent.putExtra("LONGITUDE", longitude);
            startActivity(intent);
            return true;
        });
        popupMenu.show();
    }

    public void acceptFail(String failure) {
        keepOn = false;
        Log.d(TAG, "acceptFail: Failed to download API data - " + failure);
    }

    public void acceptRoutes(ArrayList<Route> routesIn) {
        allRoutes.addAll(routesIn);
        BusVolley.downloadAllRouteDirection(allRoutes);
    }

    public void acceptDirections(ArrayList<Route> routesIn) {
        allRoutes.clear();
        allRoutes.addAll(routesIn);
        BusVolley.downloadAllStops(allRoutes);
    }

    public void acceptStops(ArrayList<Route> routesIn) {
        allRoutes.clear();
        allRoutes.addAll(routesIn);
        displayRoutes.addAll(routesIn);
        routeAdapter.notifyItemRangeChanged(0, displayRoutes.size());
        saveData();
        keepOn = false;
    }

    // Check location permission
    private boolean checkAppPermissions() {
        // Check perm - if not then start the  request and return
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[] {
                    Manifest.permission.ACCESS_FINE_LOCATION }, LOCATION_REQUEST);
            return false;
        }
        return true;
    }

    // Location permission result callback
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == LOCATION_REQUEST) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                determineLocation();
            } else if (shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)) {
                showRationaleAlert();
            } else {
                showAppExitDialog();
            }
        }
    }

    // Check Internet connection
    public boolean checkNetwork() {
        Network currentNetwork = connectivityManager.getActiveNetwork();
        if (currentNetwork == null) {
            showNoInternetDialog();
            return false;
        }
        return true;
    }

    // already checked for location permission, so no need to recheck location permission
    private void determineLocation() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST);
            return;
        }

        //location permission is granted
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, location -> {
                    if (location != null) {
                        // get current location lat and long
                        latitude = location.getLatitude();
                        longitude = location.getLongitude();
                        Log.d(TAG, "determineLocation: " + latitude + "," + longitude);
                    } else {
                        // location not available, show dialog and exit app
                        showNoLocationAvailableDialog();
                    }
                });
    }

    // Location permission rationale alert dialog
    private void showRationaleAlert() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.CustomAlertDialog);
        builder.setTitle("Fine Accuracy Needed");
        builder.setIcon(R.drawable.bus_icon_black);
        builder.setMessage("This application needs Fine Accuracy permission in order to determine" +
                " the bus stops closest to your location. It will not function properly without" +
                " it. Will you allow it?");
        builder.setPositiveButton("YES", ((dialog, which) -> checkAppPermissions()));
        builder.setNegativeButton("NO THANKS", ((dialog, which) -> showAppExitDialog()));

        AlertDialog alertDialog = builder.create();
        alertDialog.setCancelable(false);
        alertDialog.setCanceledOnTouchOutside(false);

        alertDialog.show();
    }

    // Both location permission and rationale got denied, and show exit app dialog
    private void showAppExitDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.CustomAlertDialog);
        builder.setTitle("Fine Accuracy Needed");
        builder.setIcon(R.drawable.bus_icon_black);
        builder.setMessage("This application needs Fine Accuracy permission in order to determine" +
                " the bus stops closest to your location. It will not function properly without" +
                " it. Please reinstall the application again and allow this permission.");
        builder.setPositiveButton("OK", (dialog, which) ->
        {
            this.finish();
            System.exit(0);
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.setCancelable(false);
        alertDialog.setCanceledOnTouchOutside(false);

        alertDialog.show();
    }

    // If there is no internet connection, show the dialog and exit app
    private void showNoInternetDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.CustomAlertDialog);
        builder.setTitle("Bus Tracker - CTA");
        builder.setMessage("Unable to contact Bus Tracker API due to network problem. Please" +
                " check your network connection.");
        builder.setPositiveButton("OK", (dialog, which) -> {
            this.finish();
            System.exit(0);
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.setCancelable(false);
        alertDialog.setCanceledOnTouchOutside(false);

        alertDialog.show();
    }

    // If there is no location available, show the dialog and exit app
    private void showNoLocationAvailableDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.CustomAlertDialog);
        builder.setTitle("Bus Tracker - CTA");
        builder.setMessage("Unable to determine device location. If this is an emulator, please" +
                " set a location.");
        builder.setPositiveButton("OK", (dialog, which) -> {
            this.finish();
            System.exit(0);
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.setCancelable(false);
        alertDialog.setCanceledOnTouchOutside(false);

        alertDialog.show();
    }

    private void infoDialog() {
        LayoutInflater inflater = LayoutInflater.from(this);
        @SuppressLint("InflateParams") final View view = inflater.inflate(R.layout.info_dialog, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.CustomAlertDialog);
        builder.setTitle("Bus Tracker - CTA");
        builder.setIcon(R.drawable.splash_logo);
        builder.setView(view);
        TextView url = view.findViewById(R.id.infoDialogUrl);
        Linkify.addLinks(url, Linkify.WEB_URLS);
        url.setLinkTextColor(getResources().getColor(R.color.title, this.getTheme()));
        builder.setPositiveButton("OK", (((dialog, which) -> {})));
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void clickURL(View v) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(getString(R.string.info_dialog_url)));
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }

    // Load routes, stops and stops direction from shared preference
    private void loadData() {
        Gson gson = new Gson();
        String json = cache.getString("routes", null);
        Type type = new TypeToken<ArrayList<Route>>() {}.getType();
        allRoutes.addAll(gson.fromJson(json, type));
        displayRoutes.addAll(allRoutes);
        routeAdapter.notifyItemRangeChanged(0, displayRoutes.size());
        keepOn = false;
    }

    // Save all routes, stops and stops direction to shared preference
    private void saveData() {
        SharedPreferences.Editor editor = cache.edit();
        Gson gson = new Gson();
        String json = gson.toJson(allRoutes);
        editor.putString("routes", json);
        editor.putLong("time", System.currentTimeMillis());
        editor.apply();
    }

    // text change listener for search input text filter
    public void addTextChangeListener() {
        binding.searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable editable) {
                ArrayList<Route> temp = new ArrayList<>();
                for (Route s : allRoutes) {
                    String routeNum = s.getRouteNum().toLowerCase();
                    String routeName = s.getRouteName().toLowerCase();
                    String input = editable.toString().toLowerCase();
                    if (routeNum.contains(input) || routeName.contains(input)) {
                        temp.add(s);
                    }
                }
                int size = allRoutes.size();
                displayRoutes.clear();
                routeAdapter.notifyItemRangeRemoved(0, size);
                displayRoutes.addAll(temp);
                routeAdapter.notifyItemRangeChanged(0, displayRoutes.size());
            }
        });
    }

    public void showBanner() {
        BannerView bottomBanner = new BannerView(this, bannerPlacement,
                UnityBannerSize.getDynamicSize(this));
        bottomBanner.setListener(bannerListener);

        binding.banner.addView(bottomBanner);
        bottomBanner.load();
    }

    public void initFailed(String s) {
        Toast.makeText(this, s, Toast.LENGTH_SHORT).show();
    }

    public void loadFailed(String s) {
        Toast.makeText(this, s, Toast.LENGTH_SHORT).show();
    }

}