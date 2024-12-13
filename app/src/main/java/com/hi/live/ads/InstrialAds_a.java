package com.hi.live.ads;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import com.facebook.ads.AbstractAdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.hi.live.SessionManager__a;
import com.hi.live.models.AdvertisementRoot;


public class InstrialAds_a {

    private static final String TAG = "interadjanu";
    OnInterstitialAdListnear onInterstitialAdListnear;
    SessionManager__a sessionManager;
    AdvertisementRoot.Google campaignGoogle;
    AdvertisementRoot.Facebook campaignFacebook;
    private Context context;
    private InterstitialAd mInterstitialAd;
    private com.facebook.ads.InterstitialAd interstitialAdfb;
    private AdvertisementRoot.Google google;

    public InstrialAds_a(Context context) {
        this.context = context;
//        initAds();
    }

    public OnInterstitialAdListnear getOnInterstitialAdListnear() {
        return onInterstitialAdListnear;
    }

    public void setOnInterstitialAdListnear(OnInterstitialAdListnear onInterstitialAdListnear) {
        this.onInterstitialAdListnear = onInterstitialAdListnear;
    }
    private void initAds() {


        if (!sessionManager.getUser().isIsVIP() && Boolean.TRUE.equals(sessionManager.getAdsKeys().getGoogle().isShow())) {

            google = sessionManager.getAdsKeys().getGoogle();


            AdRequest adRequest = new AdRequest.Builder().build();
            InterstitialAd.load(context, (google != null) ? google.getInterstrial() : "", adRequest, new InterstitialAdLoadCallback() {
                @Override
                public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                    super.onAdFailedToLoad(loadAdError);
                    Log.d("TAG", "onAdFailedToLoad: google 1 " + loadAdError.getMessage());
                    initFacebook();
                }

                @Override
                public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                    super.onAdLoaded(interstitialAd);
                    mInterstitialAd = interstitialAd;

                    mInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                        @Override
                        public void onAdDismissedFullScreenContent() {
                            // Called when fullscreen content is dismissed.
                            Log.d("TAG", "The ad was dismissed.");
                            onInterstitialAdListnear.onAdClosed();
                        }


                        @Override
                        public void onAdShowedFullScreenContent() {
                            // Called when fullscreen content is shown.
                            // Make sure to set your reference to null so you don't
                            // show it a second time.
                            mInterstitialAd = null;
                            Log.d("TAG", "The ad was shown.");
                        }
                    });

                }

            });


            // mInterstitialAd.setAdUnitId(sessionManager.getAdmobInt());

        } else {
            if (sessionManager.getAdsKeys().getGoogle() != null && sessionManager.getAdsKeys().getGoogle().isShow()) {
                initFacebook();
            }
        }


    }


    private void initFacebook() {


        if (!sessionManager.getUser().isIsVIP() && Boolean.TRUE.equals(sessionManager.getAdsKeys().getFacebook().isShow())) {
            Log.d(TAG, "initAds: fb");
            interstitialAdfb = new com.facebook.ads.InterstitialAd(context, sessionManager.getAdsKeys().getFacebook().getInterstrial() != null ? sessionManager.getAdsKeys().getFacebook().getInterstrial() : "");

            interstitialAdfb.loadAd(
                    interstitialAdfb.buildLoadAdConfig()
                            .withAdListener(new AbstractAdListener() {


                            })
                            .build());
        }


    }

    public boolean showAds() {
        if (mInterstitialAd != null) {
            mInterstitialAd.show((Activity) context);
        } else if (interstitialAdfb != null && interstitialAdfb.isAdLoaded()) {
            interstitialAdfb.show();
        } else {
            return false;
        }
        return true;
    }

    public interface OnInterstitialAdListnear {
        void onAdLoded(InterstitialAd mInterstitialAd);

        void onAdClosed();
    }
}
