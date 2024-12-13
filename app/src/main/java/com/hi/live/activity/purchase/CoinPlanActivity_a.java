package com.hi.live.activity.purchase;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.databinding.DataBindingUtil;

import com.google.gson.JsonObject;
import com.hi.live.R;
import com.hi.live.SessionManager__a;
import com.hi.live.activity.BaseActivity_a;
import com.hi.live.ads.InstrialAds_a;
import com.hi.live.databinding.ActivityCoinPlanBinding;
import com.hi.live.models.BecomeVipMemberRoot;
import com.hi.live.models.RestResponse;
import com.hi.live.models.SettingsRoot;
import com.hi.live.retrofit.Const_a;
import com.hi.live.retrofit.RetrofitBuilder_a;
import com.razorpay.PaymentResultListener;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CoinPlanActivity_a extends BaseActivity_a implements PaymentResultListener {
    public static final String TAG = "coinPlanAct";
    private static final String STR_VIP = "You Are Vip Member";
    ActivityCoinPlanBinding binding;
    SessionManager__a sessionManager;
    List<String> paymentGateways = new ArrayList<>();
    SettingsRoot.Data setting;
    InstrialAds_a instrialAds;

    private String userId;
    private String selectedPlanId;
    private boolean isVip;
    private String CurrentCurrency = "";


    public String getCurrentCurrency() {
        return CurrentCurrency;
    }

    public void setCurrentCurrency(String currentCurrency) {
        CurrentCurrency = currentCurrency;
    }

    public List<String> getPaymentGateways() {
        return paymentGateways;
    }

    public void setPaymentGateways(List<String> paymentGateways) {
        this.paymentGateways = paymentGateways;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_coin_plan);
        sessionManager = new SessionManager__a(this);
        userId = sessionManager.getUser().getId();
        setting = sessionManager.getSetting();
        instrialAds = new InstrialAds_a(this);

        Intent intent = getIntent();
        isVip = intent.getBooleanExtra("isVip", false);
        String country = sessionManager.getStringValue(Const_a.Country);


        if (setting.isGooglePaySwitch()) {
            paymentGateways.add("google pay");
        }
        if (country.equalsIgnoreCase("INDIA") && setting.isRazorPaySwitch()) {
            paymentGateways.add("razor pay");
        }
        if (setting.isStripeSwitch()) {
            paymentGateways.add("stripe");
        }
        Log.d(TAG, "onCreate: " + paymentGateways.size());

        initView(isVip);

    }


    @SuppressLint("CommitTransaction")
    private void initView(boolean isVip) {
        getSupportFragmentManager().beginTransaction().add(R.id.containerPurchase, isVip ? new VipPlanListFragment_a() : new PlanListFragment_a()).commit();
    }

    public void onClickBack(View view) {
        onBackPressed();
    }

    @Override
    public void onBackPressed() {
        instrialAds.showAds();
        super.onBackPressed();
    }

    @Override
    public void onPaymentError(int i, String s) {
        Log.d(TAG, "onPaymentError: " + s);
        Toast.makeText(this, s, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onPaymentSuccess(String s) {
        Log.d(TAG, "onPaymentSuccess: ");
        Toast.makeText(this, "Purchased", Toast.LENGTH_SHORT).show();

        if (isVip) {
            becomeVipMember(selectedPlanId);
        } else {
            callPurchaseDoneApi(selectedPlanId);
        }

    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d("TAG", "onActivityResult: ");
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void callPurchaseDoneApi(String planId) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("user_id", userId);
        jsonObject.addProperty("plan_id", planId);
        Call<RestResponse> call = RetrofitBuilder_a.create().purchaseCoin(Const_a.DEVKEY, jsonObject);
        call.enqueue(new Callback<RestResponse>() {
            @Override
            public void onResponse(Call<RestResponse> call, Response<RestResponse> response) {
                if (response.code() == 200) {
                    if (response.body().isStatus()) {
                        Toast.makeText(CoinPlanActivity_a.this, "Purchased", Toast.LENGTH_SHORT).show();

                    } else {
                        Toast.makeText(CoinPlanActivity_a.this, "Something Went Wrong", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<RestResponse> call, Throwable t) {
                Toast.makeText(CoinPlanActivity_a.this, "Something Went Wrong..", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void setSelectedPlanId(String selectedPlanId, boolean isVip) {
        this.selectedPlanId = selectedPlanId;
        this.isVip = isVip;
    }

    public void becomeVipMember(String planId) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("user_id", userId);
        jsonObject.addProperty("plan_id", planId);
        Call<BecomeVipMemberRoot> call = RetrofitBuilder_a.create().becomeVip(Const_a.DEVKEY, jsonObject);
        call.enqueue(new Callback<BecomeVipMemberRoot>() {
            @Override
            public void onResponse(Call<BecomeVipMemberRoot> call, Response<BecomeVipMemberRoot> response) {
                if (response.code() == 200) {
                    if (response.body().isStatus() && response.body().getUser() != null) {
                        Toast.makeText(CoinPlanActivity_a.this, STR_VIP, Toast.LENGTH_SHORT).show();
                        sessionManager.saveUser(response.body().getUser());

                    } else {
                        Toast.makeText(CoinPlanActivity_a.this, STR_VIP, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(CoinPlanActivity_a.this, STR_VIP, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<BecomeVipMemberRoot> call, Throwable t) {
                Toast.makeText(CoinPlanActivity_a.this, "Something Went Wrong..", Toast.LENGTH_SHORT).show();
            }
        });
    }

}