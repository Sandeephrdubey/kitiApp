package com.hi.live.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.databinding.DataBindingUtil;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.hi.live.LivexUtils_a;
import com.hi.live.R;
import com.hi.live.SessionManager__a;
import com.hi.live.activity.callwork.CallRequestActivity_a;
import com.hi.live.databinding.ActivityGuestProfileBinding;
import com.hi.live.models.CallCreateRoot;
import com.hi.live.models.GuestUserRoot;
import com.hi.live.retrofit.ApiCalling_a;
import com.hi.live.retrofit.Const_a;
import com.hi.live.retrofit.RetrofitBuilder_a;
import com.hi.live.socket.CallHandler;
import com.hi.live.socket.MySocketManager;
import com.hi.live.socket.SocketConst;

import org.json.JSONException;
import org.json.JSONObject;

import io.socket.client.Socket;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GuestProfileActivityG_a extends BaseActivity_a {
    private static final String TAG = "GuestProfileActivityG_a";
    ActivityGuestProfileBinding binding;
    SessionManager__a sessionManager;
    private GuestUserRoot.Data guestUser;
    private Socket globalSoket;
    private boolean isGoneForIntent = false;
    private String guestId;
    private boolean isFollow = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_guest_profile);
        MainActivityG_a.isHostLive = false;
        sessionManager = new SessionManager__a(this);
        binding.shimmer.setVisibility(View.VISIBLE);
        binding.shimmer.startShimmer();
        globalSoket = MySocketManager.getInstance().getSocet();
        globalSoket.emit("call", "jems bro geusrt");
        getIntentData();
    }

    private void getIntentData() {
        MySocketManager.getInstance().addCallListener(callHandler);
        Intent intent = getIntent();
        guestId = intent.getStringExtra("guestId");
        Log.d("TAG", "getIntentData: " + guestId);
        if (guestId != null && !guestId.equals("")) {
            getData(guestId);
        }
    }


    private void getData(String guestId) {
        Call<GuestUserRoot> call = RetrofitBuilder_a.create().getGuestUserProfile(Const_a.DEVKEY, "user", sessionManager.getUser().getId(), guestId);
        call.enqueue(new Callback<GuestUserRoot>() {
            @Override
            public void onResponse(Call<GuestUserRoot> call, Response<GuestUserRoot> response) {
                if (response.code() == 200 && response.body().isStatus()) {
                    if (response.body().getData() != null) {
                        isFollow = response.body().isIsFollow();
                        Log.d(TAG, "onResponse: user follow or not" + isFollow);
                        guestUser = response.body().getData();
                        if (guestUser != null) {
                            binding.shimmer.setVisibility(View.GONE);
                            setUserData();
                            initListner();
                            LivexUtils_a.setProfileVisit(guestUser.getId(), sessionManager.getUser().getId());
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<GuestUserRoot> call, Throwable t) {
                if (!isFinishing()) {
                    Toast.makeText(GuestProfileActivityG_a.this, "User Not Found.", Toast.LENGTH_SHORT).show();
                }
                finish();
            }
        });
    }

    private void initListner() {
        binding.lytfollow.setOnClickListener(v -> followUser());
        binding.lytunfollow.setOnClickListener(v -> unFOllowUser());
        binding.lytchat.setOnClickListener(v -> startActivity(new Intent(this, ChatListActivityGOriginal_a.class).putExtra("name", guestUser.getName()).putExtra("image", guestUser.getImage()).putExtra("hostid", guestUser.getId())));

        binding.lytCall.setOnClickListener(v -> {
            if (sessionManager.getSetting().getUserCallCharge() <= sessionManager.getUser().getCoin()) {
                customDialogClass.show();
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("user_id", sessionManager.getUser().getId());
                    jsonObject.put("host_id", guestId);
                    jsonObject.put("type", "user");
                    MySocketManager.getInstance().getSocet().emit(SocketConst.EVENT_MAKE_CALL, jsonObject);
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }

                MySocketManager.getInstance().getSocet().once(SocketConst.EVENT_MAKE_CALL,args -> {
                    if (args[0] != null) {
                        runOnUiThread(() -> {
                            if (!isGoneForIntent) {
                                isGoneForIntent = true;
                                CallCreateRoot.CallId call = new Gson().fromJson(args[0].toString(), CallCreateRoot.CallId.class);
                                Log.d("CALLLLLL", "onMakeCall: GuestProfileActivityG_a ");
                                startActivity(new Intent(GuestProfileActivityG_a.this, CallRequestActivity_a.class).putExtra(Const_a.IS_FROM_LIST, true).putExtra(Const_a.CALL_DATA, new Gson().toJson(call)));
                                customDialogClass.dismiss();
                            }
                        });

                    }
                    if (args[1] != null) {
                        if (!isFinishing()) {
                            runOnUiThread(() -> {
                                customDialogClass.dismiss();
                                Toast.makeText(GuestProfileActivityG_a.this, guestUser.getName() + " is not able receive call.", Toast.LENGTH_SHORT).show();
                            });
                        }
                    }
                });
            } else
                Toast.makeText(this, "You require " + sessionManager.getSetting().getUserCallCharge() + " coin to make call.", Toast.LENGTH_SHORT).show();


        });
    }

    private void followUser() {
        binding.lytfollow.setVisibility(View.GONE);
        binding.followProgress.setVisibility(View.VISIBLE);
        ApiCalling_a apiCalling = new ApiCalling_a(this);
        apiCalling.followUser(this, new SessionManager__a(this).getUser().getId(), guestUser.getId());
        apiCalling.setResponseListnear(new ApiCalling_a.ResponseListnear() {
            @Override
            public void responseSuccess() {

                binding.followProgress.setVisibility(View.GONE);
                binding.lytunfollow.setVisibility(View.VISIBLE);
            }

            @Override
            public void responseFail() {
//ll
            }
        });
    }

    private void unFOllowUser() {
        binding.lytunfollow.setVisibility(View.GONE);
        binding.followProgress.setVisibility(View.VISIBLE);
        ApiCalling_a apiCalling = new ApiCalling_a(this);
        apiCalling.unfollowUser(this, new SessionManager__a(this).getUser().getId(), guestUser.getId());
        apiCalling.setResponseListnear(new ApiCalling_a.ResponseListnear() {
            @Override
            public void responseSuccess() {
                binding.followProgress.setVisibility(View.GONE);
                binding.lytfollow.setVisibility(View.VISIBLE);
            }

            @Override
            public void responseFail() {
//ll
            }
        });

    }

    private void setUserData() {
        if (!isFinishing()) {
            Glide.with(this).load(guestUser.getImage()).centerCrop().into(binding.imggirl);
            Glide.with(this).load(guestUser.getImage()).circleCrop().into(binding.imguser);
        }
        binding.tvcoin.setText(String.valueOf(guestUser.getCoin()));
        binding.tvfollowing.setText(String.valueOf(guestUser.getFollowingCount()));
        binding.tvfollowrs.setText(String.valueOf(guestUser.getFollowersCount()));
        binding.tvlocation.setText(guestUser.getCountry());
        binding.tvName.setText(guestUser.getName());
        binding.tvusername.setText(guestUser.getUniqueId());
        if (guestUser.isIsOnline() || guestUser.isOnlineOfPenal()) {
            binding.lytCall.setVisibility(View.VISIBLE);
        }
        if (!isFollow) { // notFollowing
            binding.lytunfollow.setVisibility(View.GONE);
            binding.lytfollow.setVisibility(View.VISIBLE);
        } else {
            binding.lytfollow.setVisibility(View.GONE);
            binding.lytunfollow.setVisibility(View.VISIBLE);
        }
    }

    public void onClickBack(View view) {
        finish();
    }


    CallHandler callHandler = new CallHandler() {
        @Override
        public void onCallRequest(Object[] args) {

        }

        @Override
        public void onCallReceive(Object[] args) {

        }

        @Override
        public void onCallConfirm(Object[] args) {

        }

        @Override
        public void onCallAnswer(Object[] args) {

        }

        @Override
        public void onCallCancel(Object[] args) {

        }

        @Override
        public void onCallDisconnect(Object[] args) {

        }

        @Override
        public void onGiftRequest(Object[] args) {

        }

        @Override
        public void onVgift(Object[] args) {

        }

        @Override
        public void onComment(Object[] args) {

        }

        @Override
        public void onMakeCall(Object[] args) {

        }

        @Override
        public void onIsBusy(Object[] args) {
        }

        @Override
        public void onRefresh(Object[] args) {

        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        isGoneForIntent = false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MySocketManager.getInstance().removeCallListener(callHandler);
    }
}