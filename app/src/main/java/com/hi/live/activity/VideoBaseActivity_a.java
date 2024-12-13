package com.hi.live.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.hi.live.R;
import com.hi.live.SessionManager__a;
import com.hi.live.adaptor.MoreCoinAdapter_a;
import com.hi.live.databinding.BottomSheetPlansBinding;
import com.hi.live.models.CountryDetailRoot;
import com.hi.live.models.PlanRoot;
import com.hi.live.retrofit.Const_a;
import com.hi.live.retrofit.RetrofitBuilder_a;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public abstract class VideoBaseActivity_a extends AppCompatActivity {
    private static final String TAG = "vidBaseact";
    Context context;
    SessionManager__a sessionManager;
    BottomSheetPlansBinding bottomSheetPlansBinding;
    private BottomSheetDialog bottomSheetDialog;

    private String selectedPlanId;
    private String CurrentCurrency;

    @Override
    protected void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sessionManager = new SessionManager__a(this);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SECURE);


        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);


        bottomSheetDialog = new BottomSheetDialog(this, R.style.CustomBottomSheetDialogTheme);
        bottomSheetDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        bottomSheetPlansBinding = DataBindingUtil.inflate(LayoutInflater.from(this), R.layout.bottom_sheet_plans, null, false);
        bottomSheetDialog.setContentView(bottomSheetPlansBinding.getRoot());

        bottomSheetPlansBinding.btnClose.setOnClickListener(v -> bottomSheetDialog.dismiss());


        String countryCOde = sessionManager.getStringValue(Const_a.Country_CODE);
        if (countryCOde.equals("")) {
            Toast.makeText(this, "Country Code Not Found", Toast.LENGTH_SHORT).show();
            return;
        }

        Call<CountryDetailRoot> call = RetrofitBuilder_a.getCurrency().getCurrencyCode(countryCOde);
        call.enqueue(new Callback<CountryDetailRoot>() {
            @Override
            public void onResponse(Call<CountryDetailRoot> call, Response<CountryDetailRoot> response) {
                if (response.code() == 200) {
                    if (response.body() != null && response.body().getCurrencies() != null && !response.body().getCurrencies().isEmpty()) {
                        CurrentCurrency = response.body().getCurrencies().get(0);

                        Log.d(TAG, "onResponse: currency code " + CurrentCurrency);
                        getPlanList();
                    } else {
                        getPlanList();
                    }
                } else {
                    getPlanList();
                }
            }

            @Override
            public void onFailure(Call<CountryDetailRoot> call, Throwable t) {
                Log.d(TAG, "onFailure: 110 " + t.getMessage());
                getPlanList();
            }
        });

    }

    private void getPlanList() {
        Call<PlanRoot> call = RetrofitBuilder_a.create().getPlanList(Const_a.DEVKEY);
        call.enqueue(new Callback<PlanRoot>() {


            @Override
            public void onResponse(Call<PlanRoot> call, Response<PlanRoot> response) {
                if (response.code() == 200 && response.body().isStatus() && !response.body().getData().isEmpty()) {

                    String currency = "INR";
                    String country = "IN";

                    MoreCoinAdapter_a moreCoinAdapter = new MoreCoinAdapter_a(response.body().getData(), country, this::onButClick);
                    bottomSheetPlansBinding.rvMoreCoins.setAdapter(moreCoinAdapter);
                    Log.d(TAG, "onResponse:plad " + response.body().getData().size());
                }
            }

            private void onButClick(PlanRoot.DataItem dataItem) {
                selectedPlanId = dataItem.getGoogleProductId();
            }

            @Override
            public void onFailure(Call<PlanRoot> call, Throwable t) {
//ll
            }
        });
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    public void openRechargeSheet() {
        bottomSheetDialog.show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        Log.d("TAG", "onActivityResult: ");
        super.onActivityResult(requestCode, resultCode, data);
    }


}
