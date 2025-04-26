package com.myolin.ctabustracker.Ads;

import android.util.Log;

import com.myolin.ctabustracker.Activity.MainActivity;
import com.unity3d.ads.IUnityAdsInitializationListener;
import com.unity3d.ads.UnityAds;

public class MainUnityInitializationListener implements IUnityAdsInitializationListener {

    private static final String TAG = "UnityInitializationList";
    private final MainActivity mainActivity;

    public MainUnityInitializationListener(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }

    @Override
    public void onInitializationComplete() {
        Log.d(TAG, "onInitializationComplete: ");
        mainActivity.showBanner();
    }

    @Override
    public void onInitializationFailed(UnityAds.UnityAdsInitializationError error, String message) {
        Log.d(TAG, "onInitializationFailed: ");
        mainActivity.initFailed(message);
    }
}
