package com.hi.live.activity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowInsets;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.Gson;
import com.hi.live.MyApp_g;
import com.hi.live.R;
import com.hi.live.SessionManager__a;
import com.hi.live.activity.callwork.CallScreenActivity_a;
import com.hi.live.models.RestResponse;
import com.hi.live.oflineModels.VideoCallDataRoot;
import com.hi.live.popus.CustomDialogClass;
import com.hi.live.retrofit.Const_a;
import com.hi.live.retrofit.RetrofitBuilder_a;
import com.hi.live.socket.CallHandler;
import com.hi.live.socket.MySocketManager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BaseActivity_a extends AppCompatActivity {

    public static boolean IN_BACKGROUND = false;
    private static final String TAG = "baseact";
    public static boolean isUserBusyLocal = false;
    public static boolean isActOpened = false;
    public static boolean HOST_ONLINE = false;
    protected static boolean isCallIncoming = false;
    public static boolean STATUS_VIDEO_CALL = false;
    public SessionManager__a sessionManager;
    public CustomDialogClass customDialogClass;

    CallHandler callHandler = new CallHandler() {
        @Override
        public void onCallRequest(Object[] args) {
            Log.d(TAG, "call: inforemed" + args[0].toString());
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (args[0].equals("")) {
                        Log.d(TAG, "run: call args is null");
                        return;
                    }
                    try {
                        VideoCallDataRoot videoCallDataRoot = new Gson().fromJson(args[0].toString(), VideoCallDataRoot.class);
                        if (videoCallDataRoot != null) {
                            SessionManager__a sessionManager = new SessionManager__a(BaseActivity_a.this);
                            if (sessionManager.getBooleanValue(Const_a.ISLOGIN)) {
                                String uId = sessionManager.getUser().getId();
                                Log.d(TAG, "run: is host call  " + videoCallDataRoot.isHostCall());
                                Log.d(TAG, "run: is client id   " + videoCallDataRoot.getClientId());
                                if (videoCallDataRoot.isHostCall() && uId.equals(videoCallDataRoot.getClientId())) {
                                    Log.d(TAG, "onCallRequest: HOST_ONLINE  === " + isUserBusyLocal);
                                    Log.d(TAG, "onCallRequest: isCallIncoming  == " + isCallIncoming);
                                    Log.d(TAG, "onCallRequest: isActOpened  == " + isActOpened);
                                    Log.d(TAG, "onCallRequest: IN_BACKGROUND  == " + IN_BACKGROUND);
                                    if (!IN_BACKGROUND && !isUserBusyLocal && !isCallIncoming) {
                                        isCallIncoming = true;
                                        if (!isActOpened) {
                                            isActOpened = true;
                                            startActivity(new Intent(BaseActivity_a.this, CallScreenActivity_a.class).putExtra("datastr", args[0].toString()));
                                        }
                                    }
                                }

                            } else {
                                Log.d(TAG, "run: not login yet");
                            }

                        } else {
                            Log.d(TAG, "run: object is null");
                        }
                    } catch (Exception o) {
                        Log.d(TAG, "run: err " + o.getMessage());
                    }
                }
            });
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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SECURE);
        View decorView = getWindow().getDecorView();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        MySocketManager.getInstance().addCallListener(callHandler);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT_WATCH) {
            decorView.setOnApplyWindowInsetsListener((v, insets) -> {
                WindowInsets defaultInsets = v.onApplyWindowInsets(insets);

                return defaultInsets.replaceSystemWindowInsets(
                        defaultInsets.getSystemWindowInsetLeft(),
                        0,
                        defaultInsets.getSystemWindowInsetRight(),
                        defaultInsets.getSystemWindowInsetBottom());

            });
        }
        ViewCompat.requestApplyInsets(decorView);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(ContextCompat.getColor(this, android.R.color.transparent));
        }
        sessionManager = new SessionManager__a(this);
        customDialogClass = new CustomDialogClass(this, R.style.customStyle);
        customDialogClass.setCanceledOnTouchOutside(false);
        customDialogClass.setCancelable(false);
        onLineUser();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MySocketManager.getInstance().removeCallListener(callHandler);
    }

    public MyApp_g getApp() {
        return ((MyApp_g) getApplication());
    }

    private void onLineUser() {
        SessionManager__a sessionManager = new SessionManager__a(getApplicationContext());
        if (sessionManager.getBooleanValue(Const_a.ISLOGIN)) {
            Call<RestResponse> call = RetrofitBuilder_a.create().onlineUser(Const_a.DEVKEY, sessionManager.getUser().getId());
            call.enqueue(new Callback<RestResponse>() {
                @Override
                public void onResponse(Call<RestResponse> call, Response<RestResponse> response) {

                }

                @Override
                public void onFailure(Call<RestResponse> call, Throwable t) {
//ll
                }
            });
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        FirebaseMessaging.getInstance().subscribeToTopic("ONLINE").addOnCompleteListener(task -> Log.d("ssuub", "subscribe: init msg"));
    }

    public void doTransition(int type) {
        if (type == Const_a.BOTTOM_TO_UP) {

            overridePendingTransition(R.anim.enter_from_bottom, R.anim.exit_none);
        } else if (type == Const_a.UP_TO_BOTTOM) {
            overridePendingTransition(R.anim.exit_none, R.anim.enter_from_up);

        }

    }
}

