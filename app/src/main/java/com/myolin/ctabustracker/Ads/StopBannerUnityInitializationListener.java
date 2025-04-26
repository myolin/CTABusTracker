package com.myolin.ctabustracker.Ads;

import android.util.Log;

import com.myolin.ctabustracker.Activity.StopsActivity;
import com.unity3d.ads.IUnityAdsInitializationListener;
import com.unity3d.ads.UnityAds;

public class StopBannerUnityInitializationListener implements IUnityAdsInitializationListener {

    private static final String TAG = "StopBannerUnityInitializationListener";
    private final StopsActivity stopsActivity;

    public StopBannerUnityInitializationListener(StopsActivity stopsActivity) {
        this.stopsActivity = stopsActivity;
    }

    @Override
    public void onInitializationComplete() {
        Log.d(TAG, "onInitializationComplete: StopsActivity");
        stopsActivity.showBanner();
    }

    @Override
    public void onInitializationFailed(UnityAds.UnityAdsInitializationError error, String message) {
        Log.d(TAG, "onInitializationFailed: StopsActivity");
        stopsActivity.initFailed(message);
    }
}
