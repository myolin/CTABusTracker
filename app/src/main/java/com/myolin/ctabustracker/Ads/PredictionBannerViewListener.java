package com.myolin.ctabustracker.Ads;

import android.util.Log;

import com.myolin.ctabustracker.Activity.PredictionsActivity;
import com.unity3d.services.banners.BannerErrorInfo;
import com.unity3d.services.banners.BannerView;

public class PredictionBannerViewListener implements BannerView.IListener {

    private static final String TAG = "PredictionBannerViewListener";
    private final PredictionsActivity predictionsActivity;

    public PredictionBannerViewListener(PredictionsActivity predictionsActivity) {
        this.predictionsActivity = predictionsActivity;
    }

    @Override
    public void onBannerLoaded(BannerView bannerAdView) {
        Log.d(TAG, "onBannerLoaded: PredictionsActivity");
    }

    @Override
    public void onBannerShown(BannerView bannerAdView) {
        Log.d(TAG, "onBannerShown: PredictionsActivity");
    }

    @Override
    public void onBannerClick(BannerView bannerAdView) {
        Log.d(TAG, "onBannerClick: PredictionsActivity");
    }

    @Override
    public void onBannerFailedToLoad(BannerView bannerAdView, BannerErrorInfo errorInfo) {
        Log.d(TAG, "onBannerFailedToLoad: PredictionsActivity");
        predictionsActivity.loadFailed(errorInfo.errorMessage);
    }


    @Override
    public void onBannerLeftApplication(BannerView bannerView) {
        Log.d(TAG, "onBannerLeftApplication: PredictionsActivity");
    }
}
