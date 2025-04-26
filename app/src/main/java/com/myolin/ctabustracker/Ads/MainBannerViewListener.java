package com.myolin.ctabustracker.Ads;

import android.util.Log;

import com.myolin.ctabustracker.Activity.MainActivity;
import com.unity3d.services.banners.BannerErrorInfo;
import com.unity3d.services.banners.BannerView;

public class MainBannerViewListener implements BannerView.IListener {

    private static final String TAG = "BannerViewListener";
    private final MainActivity mainActivity;

    public MainBannerViewListener(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }

    @Override
    public void onBannerLoaded(BannerView bannerAdView) {
        Log.d(TAG, "onBannerLoaded: MainActivity");
    }

    @Override
    public void onBannerShown(BannerView bannerAdView) {
        Log.d(TAG, "onBannerShown: MainActivity");
    }

    @Override
    public void onBannerClick(BannerView bannerAdView) {
        Log.d(TAG, "onBannerClick: MainActivity");
    }

    @Override
    public void onBannerFailedToLoad(BannerView bannerAdView, BannerErrorInfo errorInfo) {
        Log.d(TAG, "onBannerFailedToLoad: MainActivity");
        mainActivity.loadFailed(errorInfo.errorMessage);
    }

    @Override
    public void onBannerLeftApplication(BannerView bannerView) {
        Log.d(TAG, "onBannerLeftApplication: MainActivity");
    }
}
