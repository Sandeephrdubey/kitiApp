package com.hi.live.activity.purchase;

import android.app.Activity;
import android.util.Log;

import com.android.billingclient.api.AcknowledgePurchaseParams;
import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.ConsumeParams;
import com.android.billingclient.api.ConsumeResponseListener;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.android.billingclient.api.SkuDetailsParams;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jeelkhokhariya
 * on 04/08/21
 */
class MyPlayStoreBilling {

    private static final String TAG = "Myplaystorbeling";
    private final BillingClient billingClient;
    private final Activity activity;
    private final OnPurchaseComplete onPurchaseComplete;
    private boolean isConsumable = false;
    private boolean isConnected = false;

    public MyPlayStoreBilling(Activity activity, OnPurchaseComplete onPurchaseComplete) {

        PurchasesUpdatedListener purchasesUpdatedListener = (billingResult, purchases) -> {
            // To be implemented in a later section.
            if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK
                    && purchases != null) {


                if (!purchases.isEmpty()) {
                    handlePurchase(purchases.get(0));
                    Log.d(TAG, "MyPlayStoreBilling: succeccc=======");
                }
                for (Purchase purchase : purchases) {
                    Log.d(TAG, "onPurchasesUpdated: " + purchase);

                }
            } else if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.USER_CANCELED) {
                // Handle an error caused by a user cancelling the purchase flow.
                Log.d(TAG, "USER_CANCELED: ");
                onPurchaseComplete.onPurchaseResult(false);
            } else {
                // Handle any other error codes.
                onPurchaseComplete.onPurchaseResult(false);
                Log.d(TAG, "Error: ");
            }
        };
        billingClient = BillingClient.newBuilder(activity)
                .setListener(purchasesUpdatedListener)
                .enablePendingPurchases()
                .build();
        this.activity = activity;
        this.onPurchaseComplete = onPurchaseComplete;
        billingClient.startConnection(new BillingClientStateListener() {
            @Override
            public void onBillingSetupFinished(BillingResult billingResult) {
                if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                    isConnected = true;
                    onPurchaseComplete.onConnected(true);
                }
            }

            @Override
            public void onBillingServiceDisconnected() {
                isConnected = false;
            }
        });
    }

    void handlePurchase(Purchase purchase) {
        if (purchase.getPurchaseState() == Purchase.PurchaseState.PURCHASED) {
            if (!purchase.isAcknowledged()) {
                AcknowledgePurchaseParams acknowledgePurchaseParams =
                        AcknowledgePurchaseParams.newBuilder()
                                .setPurchaseToken(purchase.getPurchaseToken())
                                .build();
                billingClient.acknowledgePurchase(acknowledgePurchaseParams,
                        billingResult -> Log.d(TAG, "acknowledgePurchase: " + billingResult.getDebugMessage()));
                if (isConsumable) {
                    consumePurchase(purchase);
                }
            }
            onPurchaseComplete.onPurchaseResult(true);
        }
    }

    void consumePurchase(Purchase purchase) {
        ConsumeParams consumeParams =
                ConsumeParams.newBuilder()
                        .setPurchaseToken(purchase.getPurchaseToken())
                        .build();

        ConsumeResponseListener listener = (billingResult, purchaseToken) -> {
            if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                Log.d(TAG, "consumePurchase: OK");
            }
        };

        billingClient.consumeAsync(consumeParams, listener);
    }

    public void startPurchase(String productId, String skuType, boolean isConsumable) {
        Log.d(TAG, "startPurchase: " + isConnected);
        Log.d(TAG, "startPurchase: pid " + productId);
        if (isConnected) {
            this.isConsumable = isConsumable;
            List<String> skuList = new ArrayList<>();
            skuList.add(productId);
//            skuList.add("android.test.purchased");
            SkuDetailsParams.Builder params = SkuDetailsParams.newBuilder();
            params.setSkusList(skuList).setType(skuType);
            billingClient.querySkuDetailsAsync(params.build(),
                    (billingResult1, skuDetailsList) -> {
                        // Process the result.
                        Log.d(TAG, "startPurchase: sku " + skuDetailsList);
                        BillingFlowParams billingFlowParams = null;
                        if (skuDetailsList != null) {
                            Log.d(TAG, "startPurchase: " + skuDetailsList.get(0));
                            billingFlowParams = BillingFlowParams.newBuilder()
                                    .setSkuDetails(skuDetailsList.get(0))
                                    .build();
                        }
                        Log.d(TAG, "startPurchase: " + billingFlowParams);
                        if (billingFlowParams != null) {
                            billingClient.launchBillingFlow(activity, billingFlowParams);
                        }

                    });
        }
    }

//    public boolean isSubscriptionRunning() {
//        return billingClient.queryPurchases(BillingClient.SkuType.SUBS).getPurchasesList() != null
//                && !billingClient.queryPurchases(BillingClient.SkuType.SUBS).getPurchasesList().isEmpty();
//    }

    public void onDestroy() {
        if (isConnected)
            billingClient.endConnection();
    }

    public interface OnPurchaseComplete {

        void onConnected(boolean isConnect);

        void onPurchaseResult(boolean isPurchaseSuccess);
    }
}
