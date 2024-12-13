package com.hi.live.ads;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import com.facebook.ads.AbstractAdListener;
import com.facebook.ads.InterstitialAd;
import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;
import com.hi.live.SessionManager__a;

public class RewardAds {

    private static final String TAG = "rewardads";
    RewardAdListnear rewardAdListnear;
    SessionManager__a sessionManager;
    private RewardedAd rewardedAd;
    private Context context;
    private InterstitialAd interstitialAdfb;

    FullScreenContentCallback callBack = new FullScreenContentCallback() {
        @Override
        public void onAdShowedFullScreenContent() {
            // Called when ad is shown.
            Log.d(TAG, "Ad was shown.");
        }

        @Override
        public void onAdFailedToShowFullScreenContent(AdError adError) {
            // Called when ad fails to show.
            Log.d(TAG, "Ad failed to show.");
            rewardAdListnear.onEarned();
        }

        @Override
        public void onAdDismissedFullScreenContent() {
            // Called when ad is dismissed.
            // Set the ad reference to null so you don't show the ad a second time.
            Log.d(TAG, "Ad was dismissed.");
            rewardedAd = null;
        }
    };

    public RewardAds(Context context) {
        this.context = context;
        sessionManager = new SessionManager__a(context);

        if (sessionManager.getAdsKeys().getGoogle() != null && sessionManager.getAdsKeys().getGoogle().isShow()) {
            Log.d(TAG, "RewardAds: initGoogle");
            initGoogle();
        } else if (sessionManager.getAdsKeys().getFacebook() != null && sessionManager.getAdsKeys().getFacebook().isShow()) {
            Log.d(TAG, "RewardAds: initFacebook");
            initFacebook();
        }

    }

    public RewardAdListnear getRewardAdListnear() {
        return rewardAdListnear;
    }

    public void setRewardAdListnear(RewardAdListnear rewardAdListnear) {
        this.rewardAdListnear = rewardAdListnear;
    }

    private void initGoogle() {

        AdRequest adRequest = new AdRequest.Builder().build();
        RewardedAd.load(context, sessionManager.getAdsKeys().getGoogle().getReward(),
                adRequest, new RewardedAdLoadCallback() {
                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        // Handle the error.
                        Log.d(TAG, loadAdError.getMessage());
                        Log.d(TAG, "onAdFailedToLoad: " + loadAdError);
                        rewardedAd = null;
                        initFacebook();
                    }

                    @Override
                    public void onAdLoaded(@NonNull RewardedAd rewardedAd) {
                        RewardAds.this.rewardedAd = rewardedAd;
                        rewardedAd.setFullScreenContentCallback(callBack);
                        Log.d(TAG, "Ad was loaded.");
                    }
                });


    }


    private void initFacebook() {
        interstitialAdfb = new com.facebook.ads.InterstitialAd(context, sessionManager.getAdsKeys().getFacebook().getReward());
        interstitialAdfb.loadAd(
                interstitialAdfb.buildLoadAdConfig()
                        .withAdListener(new AbstractAdListener() {

                        })
                        .build());

    }


    public void showAd(Activity activity) {


        if (rewardedAd != null) {
            Activity activityContext = activity;
            rewardedAd.show(activityContext, rewardItem -> {
                // Handle the reward.
                Log.d(TAG, "The user earned the reward.");
                rewardAdListnear.onEarned();
            });
        } else {
            Log.d(TAG, "The rewarded ad wasn't ready yet.");
        }
    }


    public interface RewardAdListnear {
        void onAdClosed();

        void onEarned();
    }

}
