package com.myolin.ctabustracker.Ads;

import android.util.Log;

import com.myolin.ctabustracker.Activity.StopsActivity;
import com.unity3d.services.banners.BannerErrorInfo;
import com.unity3d.services.banners.BannerView;

public class StopBannerViewListener implements BannerView.IListener {

    private static final String TAG = "StopBannerViewListener";
    private final StopsActivity stopsActivity;

    public StopBannerViewListener(StopsActivity stopsActivity) {
        this.stopsActivity = stopsActivity;
    }


    @Override
    public void onBannerLoaded(BannerView bannerAdView) {
        Log.d(TAG, "onBannerLoaded: StopsActivity");
    }

    @Override
    public void onBannerShown(BannerView bannerAdView) {
        Log.d(TAG, "onBannerShown: StopsActivity");
    }

    @Override
    public void onBannerClick(BannerView bannerAdView) {
        Log.d(TAG, "onBannerClick: StopsActivity");
    }

    @Override
    public void onBannerFailedToLoad(BannerView bannerAdView, BannerErrorInfo errorInfo) {
        Log.d(TAG, "onBannerFailedToLoad: StopsActivity");
        stopsActivity.loadFailed(errorInfo.errorMessage);
    }

    @Override
    public void onBannerLeftApplication(BannerView bannerView) {
        Log.d(TAG, "onBannerLeftApplication: StopsActivity");
    }
}
