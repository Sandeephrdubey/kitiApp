package com.hi.live.activity.callwork;

import android.content.Context;

import com.google.gson.JsonObject;
import com.hi.live.SessionManager__a;
import com.hi.live.models.CallCreateRoot;
import com.hi.live.models.UserRoot;
import com.hi.live.retrofit.Const_a;
import com.hi.live.retrofit.RetrofitBuilder_a;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CallApiWork_a {
    SessionManager__a sessionManager;
    private Context context;

    public CallApiWork_a(Context context) {
        this.context = context;
        sessionManager = new SessionManager__a(context);
    }

    public void createCallRequest(String hostId, CallApiListner callApiListner, String type) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("user_id", sessionManager.getUser().getId());
        jsonObject.addProperty("host_id", hostId);
        jsonObject.addProperty("type", type);

        Call<CallCreateRoot> call = RetrofitBuilder_a.create().createCall(Const_a.DEVKEY, jsonObject);
        call.enqueue(new Callback<CallCreateRoot>() {
            @Override
            public void onResponse(Call<CallCreateRoot> call, Response<CallCreateRoot> response) {
                if (response.code() == 200 && response.body().isStatus()) {
                    if (response.body().getCallId() != null) {
                        callApiListner.onSuccess(response.body().getCallId());
                    }
                } else {
                    callApiListner.onFailure(response.body().getMessage());
                }
            }
            @Override
            public void onFailure(Call<CallCreateRoot> call, Throwable t) {
                callApiListner.onFailure(t.getMessage());
            }
        });
    }

    public void callAccepted(String callId, String userCallCharge, int seconds, CallCoinCutApiListener callCoinCutApiListener) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("callId", callId);
        jsonObject.addProperty("coin", userCallCharge);
        jsonObject.addProperty("time", seconds);
        Call<UserRoot> call = RetrofitBuilder_a.create().callRecive(Const_a.DEVKEY, jsonObject);
        call.enqueue(new Callback<UserRoot>() {
            @Override
            public void onResponse(Call<UserRoot> call, Response<UserRoot> response) {
                if (response.code() == 200) {
                    if (response.body().isStatus() && response.body().getUser() != null) {
                        sessionManager.saveUser(response.body().getUser());
                        callCoinCutApiListener.onSuccess("");
                    } else {
                        callCoinCutApiListener.onFailure(null);
                    }
                } else {
                    callCoinCutApiListener.onFailure(null);
                }
            }

            @Override
            public void onFailure(Call<UserRoot> call, Throwable t) {
                callCoinCutApiListener.onFailure(t.getMessage());
            }
        });

    }

    public interface CallApiListner {
        void onSuccess(CallCreateRoot.CallId callId);

        void onFailure(String error);
    }

    public interface CallCoinCutApiListener {
        void onSuccess(String callId);

        void onFailure(String error);
    }
}
