package com.hi.live.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.gson.JsonObject;
import com.hi.live.R;
import com.hi.live.SessionManager__a;
import com.hi.live.activity.purchase.CoinPlanActivity_a;
import com.hi.live.adaptor.DailyCoinAdaptor_a;
import com.hi.live.adaptor.ReedemGatewayAdapter_a;
import com.hi.live.ads.RewardAds;
import com.hi.live.databinding.ActivityMyWalletBinding;
import com.hi.live.databinding.BottomSheetReedemBinding;
import com.hi.live.databinding.ItemDailyCoinColorBinding;
import com.hi.live.models.DailyTaskRoot;
import com.hi.live.models.PlanRoot;
import com.hi.live.models.RestResponse;
import com.hi.live.models.User;
import com.hi.live.models.UserRoot;
import com.hi.live.popus.ChangeRatePopup_g;
import com.hi.live.retrofit.Const_a;
import com.hi.live.retrofit.RetrofitBuilder_a;

import java.util.HashMap;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyWalletActivity_a extends BaseActivity_a {
    private static final String STR_USERID = "user_id";
    ActivityMyWalletBinding binding;
    SessionManager__a sessionManager;
    RewardAds rewardAds;
    String planId;
    String selectedpaymentgateway = "";

    private String userId;
    private User user;
    private int currentTask;
    private String STR_24HR = "Try after 24 hours";
    private boolean submitButtonEnable = false;
    private String TAG = "MyWalletActivity_a";

    public static void openMe(Context context) {
        context.startActivity(new Intent(context, MyWalletActivity_a.class));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_my_wallet);
        rewardAds = new RewardAds(MyWalletActivity_a.this);
        binding.shimmer.startShimmer();
        MainActivityG_a.isHostLive = false;
        sessionManager = new SessionManager__a(this);
        userId = sessionManager.getUser().getId();


        setDefaultData();
        getUserData();
        checkDailyTask();


    }

    private void setDefaultData() {
        int minStreamingValue = sessionManager.getSetting().getStreamingMinValue();
        binding.tvrate.setText(String.valueOf(minStreamingValue));
        Log.d(TAG, "setDefaultData: getGoogle().isShow() == " + sessionManager.getAdsKeys().getGoogle().isShow());
        Log.d(TAG, "setDefaultData: getFacebook().isShow() == " + sessionManager.getAdsKeys().getFacebook().isShow());
        if (sessionManager.getAdsKeys().getGoogle().isShow()) {
            binding.rvDailyCoin.setVisibility(View.VISIBLE);
        } else binding.rvDailyCoin.setVisibility(View.GONE);
    }

    private void checkDailyTask() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty(STR_USERID, userId);
        Call<DailyTaskRoot> call = RetrofitBuilder_a.create().checkDailyTask(Const_a.DEVKEY, jsonObject);
        call.enqueue(new Callback<DailyTaskRoot>() {
            @Override
            public void onResponse(Call<DailyTaskRoot> call, Response<DailyTaskRoot> response) {
                if (response.code() == 200 && response.body().isStatus()) {
                    currentTask = response.body().getNumber();
                    setDailyTaskList();
                }
            }

            @Override
            public void onFailure(Call<DailyTaskRoot> call, Throwable t) {
//ll
            }
        });
    }

    private void setDailyTaskList() {

        DailyCoinAdaptor_a dailyCoinAdaptor = new DailyCoinAdaptor_a(currentTask, (position, randomInteger, coinColorBinding) -> {
            binding.pd.setVisibility(View.VISIBLE);
            Log.d(TAG, "setDailyTaskList: position  =====   " + position);
            Log.d(TAG, "setDailyTaskList: System.currentTimeMillis() ==== " + System.currentTimeMillis());
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("coin", randomInteger);
            jsonObject.addProperty(STR_USERID, new SessionManager__a(MyWalletActivity_a.this).getUser().getId());
            Call<RestResponse> call = RetrofitBuilder_a.create().updateDailyTask(Const_a.DEVKEY, jsonObject);
            call.enqueue(new Callback<RestResponse>() {
                @Override
                public void onResponse(Call<RestResponse> call, Response<RestResponse> response) {
                    if (response.code() == 200 && response.body().isStatus()) {
                        adFuntion(position, coinColorBinding, new onRewardAdListener() {
                            @Override
                            public void onEarned() {
                                if (currentTask == position) {
                                    coinColorBinding.imgcoin.setVisibility(View.GONE);
                                } else {
                                    Toast.makeText(MyWalletActivity_a.this, STR_24HR, Toast.LENGTH_SHORT).show();
                                }
                                checkDailyTask();
                                getUserData();
                                sessionManager.setDailyTaskCoinHistory(position, randomInteger);
                                Log.d(TAG, "onResponse: position =" + position + "Earned Coin == " + sessionManager.getDailyTaskHistoryNumber(position));
                            }

                            @Override
                            public void onFailure() {
                                Toast.makeText(MyWalletActivity_a.this, "Reward Fail", Toast.LENGTH_SHORT).show();
                            }
                        });

                    } else {
                        binding.pd.setVisibility(View.GONE);
                        coinColorBinding.imgcoin.setVisibility(View.VISIBLE);
                        Toast.makeText(MyWalletActivity_a.this, STR_24HR, Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<RestResponse> call, Throwable t) {
                    binding.pd.setVisibility(View.GONE);
                    coinColorBinding.imgcoin.setVisibility(View.VISIBLE);
                    Toast.makeText(MyWalletActivity_a.this, STR_24HR, Toast.LENGTH_SHORT).show();
                }
            });


        });
        binding.shimmer.setVisibility(View.GONE);
        binding.rvDailyCoin.setAdapter(dailyCoinAdaptor);
    }

    private void adFuntion(int position, ItemDailyCoinColorBinding coinColorBinding, onRewardAdListener rewardAdListener) {


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                binding.pd.setVisibility(View.GONE);
                rewardAds.showAd(MyWalletActivity_a.this);

            }
        }, 3000);


        rewardAds.setRewardAdListnear(new RewardAds.RewardAdListnear() {
            @Override
            public void onAdClosed() {
                rewardAdListener.onFailure();
            }

            @Override
            public void onEarned() {
                rewardAdListener.onEarned();
            }
        });
        Log.d("TAG", "onBindViewHolder:" + currentTask + " select ==" + position);
    }


    private void getUserData() {
        Log.d("checkinggg ", "getData:  my wallet 171");
        Call<UserRoot> call = RetrofitBuilder_a.create().getUserProfile(Const_a.DEVKEY, userId);
        call.enqueue(new Callback<UserRoot>() {
            @Override
            public void onResponse(Call<UserRoot> call, Response<UserRoot> response) {
                if (response.code() == 200 && response.body().isStatus() && response.body().getUser() != null) {
                    user = response.body().getUser();
                    sessionManager.saveUser(user);
                    binding.tvBalancde.setText(String.valueOf(user.getCoin()));


                    binding.lytrate.setOnClickListener(v -> {
                        ChangeRatePopup_g changeRatePopup = new ChangeRatePopup_g(MyWalletActivity_a.this, user);
                        changeRatePopup.setOnSubmitClickListnear(rate -> {
                            Log.d("TAG", "submit: ");
                            updateRate(rate);
                        });
                    });

                    binding.lytreedem.setOnClickListener(v -> {
                        int minCoin = sessionManager.getSetting().getMinPoints();
                        if (user.getCoin() >= minCoin) {

                            openReedemSheet();
                        } else {
                            Toast.makeText(MyWalletActivity_a.this, "You have atleast " + minCoin + " to redeem", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }

            @Override
            public void onFailure(Call<UserRoot> call, Throwable t) {
//ll
            }
        });
    }

    private void openReedemSheet() {
        long howManyCoinForOne = sessionManager.getSetting().getHowManyCoins();
        long myPrice = user.getCoin() / howManyCoinForOne;


        String stringReedemGateway = sessionManager.getSetting().getRedeemGateway();
        String[] gateways = stringReedemGateway.split(",");
        selectedpaymentgateway = gateways[0];


        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(MyWalletActivity_a.this, R.style.CustomBottomSheetDialogTheme);
        bottomSheetDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        BottomSheetReedemBinding bottomSheetReedemBinding = DataBindingUtil.inflate(LayoutInflater.from(this), R.layout.bottom_sheet_reedem, null, false);
        bottomSheetDialog.setContentView(bottomSheetReedemBinding.getRoot());

        BottomSheetDialog finalBottomSheetDialog = bottomSheetDialog;
        bottomSheetReedemBinding.btnclose.setOnClickListener(v -> finalBottomSheetDialog.dismiss());
        ReedemGatewayAdapter_a reedemGatewayAdapter = new ReedemGatewayAdapter_a(gateways, mathod -> {

            bottomSheetReedemBinding.etDes.setText("");
            selectedpaymentgateway = mathod;

        });
        bottomSheetReedemBinding.rvReedemGetway.setAdapter(reedemGatewayAdapter);

        bottomSheetReedemBinding.etcoin.setText(String.valueOf(user.getCoin()));


        bottomSheetReedemBinding.etCurrency.setText(myPrice + sessionManager.getSetting().getCurrency());


        bottomSheetReedemBinding.etDes.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
///ll
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().equals("")) {
                    bottomSheetReedemBinding.btnSubmit.setBackground(ContextCompat.getDrawable(MyWalletActivity_a.this, R.drawable.bg_etblack_a));
                    bottomSheetReedemBinding.btnSubmit.setTextColor(ContextCompat.getColor(MyWalletActivity_a.this, R.color.black));
                    submitButtonEnable = false;
                } else {
                    bottomSheetReedemBinding.btnSubmit.setBackground(ContextCompat.getDrawable(MyWalletActivity_a.this, R.drawable.bg_greadentround_a));
                    bottomSheetReedemBinding.btnSubmit.setTextColor(ContextCompat.getColor(MyWalletActivity_a.this, R.color.white));
                    submitButtonEnable = true;

                }
            }

            @Override
            public void afterTextChanged(Editable s) {
//ll
            }
        });


    }

    private void updateRate(String rate) {
        RequestBody coin = RequestBody.create(MediaType.parse("text/plain"), rate);
        RequestBody userid = RequestBody.create(MediaType.parse("text/plain"), userId);
        HashMap<String, RequestBody> map = new HashMap<>();
        map.put(STR_USERID, userid);
        map.put("rate", coin);

        Call<UserRoot> call = RetrofitBuilder_a.create().updateUser(Const_a.DEVKEY, map);
        call.enqueue(new Callback<UserRoot>() {
            @Override
            public void onResponse(Call<UserRoot> call, Response<UserRoot> response) {
                if (response.code() == 200) {
                    if (response.body().isStatus() && response.body().getUser() != null) {
                        Toast.makeText(MyWalletActivity_a.this, "Updated Successfully", Toast.LENGTH_SHORT).show();
                        sessionManager.saveUser(response.body().getUser());
                        getUserData();
                    } else {
                        Toast.makeText(MyWalletActivity_a.this, "Something went wrong!!", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<UserRoot> call, Throwable t) {
//ll
            }
        });
    }


    private void buyItem(PlanRoot.DataItem dataItem) {
        planId = dataItem.getId();

    }

    @Override
    protected void onResume() {
        setDefaultData();
        getUserData();
        checkDailyTask();
        super.onResume();
    }


    public void onClickBack(View view) {
        finish();
    }

    public void onClickBuyMoreCoin(View view) {
        startActivity(new Intent(this, CoinPlanActivity_a.class).putExtra("isVip", false));
    }

    public void onClickVip(View view) {
        startActivity(new Intent(this, CoinPlanActivity_a.class).putExtra("isVip", true));
    }

    interface onRewardAdListener {
        void onEarned();

        void onFailure();
    }
}