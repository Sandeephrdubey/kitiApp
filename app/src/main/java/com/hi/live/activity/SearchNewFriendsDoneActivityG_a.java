package com.hi.live.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.databinding.DataBindingUtil;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.hi.live.R;
import com.hi.live.SessionManager__a;
import com.hi.live.activity.callwork.CallApiWork_a;
import com.hi.live.activity.callwork.CallRequestActivity_a;
import com.hi.live.databinding.ActivitySearchNewFriendsDoneBinding;
import com.hi.live.models.CallCreateRoot;
import com.hi.live.models.RandomMatchHostRoot;
import com.hi.live.oflineModels.VideoCallDataRoot;
import com.hi.live.retrofit.Const_a;
import com.hi.live.socket.MySocketManager;

import io.socket.client.Socket;


public class SearchNewFriendsDoneActivityG_a extends BaseActivity_a {
    private static final String TAG = "SearchNewFriendsDone";
    ActivitySearchNewFriendsDoneBinding binding;
    RandomMatchHostRoot.Host host;
    VideoCallDataRoot videoCallDataRoot = null;
    private SessionManager__a sessionManager;
    private Socket globalSoket;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_search_new_friends_done);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        videoCallDataRoot = new VideoCallDataRoot();
        sessionManager = new SessionManager__a(SearchNewFriendsDoneActivityG_a.this);

        globalSoket = MySocketManager.getInstance().getSocet();
        Intent intent = getIntent();
        if (intent != null) {
            String onjStr = intent.getStringExtra(Const_a.DATA);
            Log.d("TAG", "onCreate: intent " + onjStr);
            if (onjStr != null && !onjStr.equals("")) {
                host = new Gson().fromJson(onjStr, RandomMatchHostRoot.Host.class);
                Log.d(TAG, "onCreate:  thumb.getHostId() === " + host.getHostId());
                initmain();
            }
        }

        binding.imgBack.setOnClickListener(view -> {
            finish();
        });
    }

    private void initmain() {

        Glide.with(this).load(host.getImage()).into(binding.hostProfileImage);
        Glide.with(this).load(host.getImage()).circleCrop().into(binding.hostRequestImage);
        binding.hostName.setText(host.getName());
        binding.hostCountry.setText(host.getCountry());
        binding.tvCoin.setText(String.valueOf(host.getCoin()));
        binding.tvAccept.setOnClickListener(v -> {


            if (sessionManager.getUser().getCoin() >= sessionManager.getSetting().getUserCallCharge()) {
                binding.tvAccept.setEnabled(false);
                customDialogClass.show();
                CallApiWork_a callApiWorkA = new CallApiWork_a(this);
                callApiWorkA.createCallRequest(host.getId(), new CallApiWork_a.CallApiListner() {
                    @Override
                    public void onSuccess(CallCreateRoot.CallId callId) {
                        try {
                            startActivity(new Intent(SearchNewFriendsDoneActivityG_a.this, CallRequestActivity_a.class).putExtra(Const_a.IS_PRIVATE,true).putExtra(Const_a.IS_FROM_LIST, true).putExtra(Const_a.CALL_DATA, new Gson().toJson(callId)));
                            customDialogClass.dismiss();
                            binding.tvAccept.setEnabled(true);
                            finish();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(String error) {


                    }
                }, "user");


            }else {

                Toast.makeText(this, "Insufficient Coins!", Toast.LENGTH_SHORT).show();
            }
            //todo listt



        });

        binding.tvSkip.setOnClickListener(v -> {
            startActivity(new Intent(SearchNewFriendsDoneActivityG_a.this, SearchForNewFriendsActivity_a.class));
            finish();
        });
    }
}

