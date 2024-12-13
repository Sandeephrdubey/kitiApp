package com.hi.live;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.DefaultLifecycleObserver;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ProcessLifecycleOwner;

import com.google.firebase.messaging.FirebaseMessaging;
import com.hi.live.activity.BaseActivity_a;
import com.hi.live.models.RestResponse;
import com.hi.live.retrofit.Const_a;
import com.hi.live.retrofit.RetrofitBuilder_a;
import com.hi.live.socket.CallHandler;
import com.hi.live.socket.MySocketManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyApp_g extends Application {
    public static final String NIGHT_MODE = "NIGHT_MODE";
    private static MyApp_g singleton = null;
    public boolean isNightModeEnabled = false;

    public static boolean isAppOpen = false;
    public SessionManager__a sessionManager;
    public String TAG = "SocketManager";


    public static MyApp_g getInstance() {

        if (singleton == null) {
            singleton = new MyApp_g();
        }
        return singleton;
    }

    String userId;

    CallHandler callHandler = new CallHandler() {
        @Override
        public void onCallRequest(Object[] args1) {

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
    AppLifecycleObserver appLifecycleObserver = new AppLifecycleObserver();


    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);
        Log.d("TAG", "onTrimMemory: " + level);
        if (level <= 25) {
            FirebaseMessaging.getInstance().unsubscribeFromTopic("ONLINE").addOnCompleteListener(task -> Log.d("ssuub", "Unsubscribe: init msg"));
            SessionManager__a sessionManager = new SessionManager__a(getApplicationContext());
            if (sessionManager.getBooleanValue(Const_a.ISLOGIN)) {
                Call<RestResponse> call = RetrofitBuilder_a.create().offlineUser(Const_a.DEVKEY, sessionManager.getUser().getId());
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

    }

    private boolean checkForground() {
        ActivityManager activityManager = (ActivityManager) getApplicationContext().getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> appProcesses = activityManager.getRunningAppProcesses();
        if (appProcesses == null) {
            return false;
        }
        final String packageName = getApplicationContext().getPackageName();
        for (ActivityManager.RunningAppProcessInfo appProcess : appProcesses) {
            if (appProcess.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND && appProcess.processName.equals(packageName)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        singleton = this;
        sessionManager = new SessionManager__a(getApplicationContext());
        Lifecycle lifecycle = ProcessLifecycleOwner.get().getLifecycle();
        lifecycle.removeObserver(appLifecycleObserver);
        lifecycle.addObserver(appLifecycleObserver);
    }

    public void initGlobalSocket() {
        Log.d(TAG, "initGlobalSocket: 106");

        if (MySocketManager.getInstance().globalConnecting) {
            Log.d(TAG, "initGlobalSocket: already connecting... global socket .........");
            return;
        }
        MySocketManager.getInstance().createGlobal(getApplicationContext());
    }

    public class AppLifecycleObserver implements DefaultLifecycleObserver {
        @Override
        public void onResume(@NonNull LifecycleOwner owner) {
            BaseActivity_a.IN_BACKGROUND = false;
            try {
                if (sessionManager.getUser() == null) {
                    Log.d(TAG, "onResume: not logged yet");
                    return;
                }
                if (MySocketManager.getInstance().getSocet() == null || !MySocketManager.getInstance().getSocet().connected()) {
                    Log.d(TAG, "onResume: ma socket connect thay che ==");
                    MySocketManager.getInstance().createGlobal(getApplicationContext());
                    MySocketManager.getInstance().addCallListener(callHandler);
                    MySocketManager.getInstance().globalConnecting = true;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        @Override
        public void onPause(@NonNull LifecycleOwner owner) {
            Log.e(TAG, "onPause: ");
            BaseActivity_a.IN_BACKGROUND = true;
        }

        @Override
        public void onStop(@NonNull LifecycleOwner owner) {
            Log.e(TAG, "onStop: ");
        }

        @Override
        public void onDestroy(@NonNull LifecycleOwner owner) {
            DefaultLifecycleObserver.super.onDestroy(owner);
            if (MySocketManager.getInstance().getSocet() != null) {
                Log.d(TAG, "onDestroy: ");

                if (sessionManager.getUser() != null) {
                    userId = sessionManager.getUser().getId();
                }

                try {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("userId", userId);
                    jsonObject.put("type", "user");
                    Log.d(TAG, "onPause: mannual1");
                    MySocketManager.getInstance().getSocet().emit("manualDisconnect", jsonObject);

                    Log.d(TAG, "onPause: mannual ");
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }

                MySocketManager.getInstance().getSocet().disconnect();
                MySocketManager.getInstance().removeCallListener(callHandler);
            }

        }
    }

}
