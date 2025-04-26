package com.myolin.ctabustracker.Ads;

import android.util.Log;

import com.myolin.ctabustracker.Activity.PredictionsActivity;
import com.unity3d.ads.IUnityAdsInitializationListener;
import com.unity3d.ads.UnityAds;

public class PredictionUnityInitializationListener implements IUnityAdsInitializationListener {

    private static final String TAG = "PredictionUnityInitializationListener";
    private final PredictionsActivity predictionsActivity;

    public PredictionUnityInitializationListener(PredictionsActivity predictionsActivity) {
        this.predictionsActivity = predictionsActivity;
    }

    @Override
    public void onInitializationComplete() {
        Log.d(TAG, "onInitializationComplete: PredictionsActivity");
        predictionsActivity.showBanner();
    }

    @Override
    public void onInitializationFailed(UnityAds.UnityAdsInitializationError error, String message) {
        Log.d(TAG, "onInitializationFailed: PredictionsActivity");
        predictionsActivity.initFailed(message);
    }
}
