package com.hi.live.retrofit;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.hi.live.SessionManager__a;
import com.hi.live.models.ChatTopicRoot;
import com.hi.live.models.HostRoot;
import com.hi.live.models.IpAddressDataRoot;
import com.hi.live.models.RestResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ApiCalling_a {

    ResponseListnear responseListnear;
    OnHostProfileGetListnear onHostProfileGetListnear;
    SessionManager__a sessionManager;
    OnRoomGenereteListnear onRoomGenereteListnear;

    public ApiCalling_a(Context context) {
        sessionManager = new SessionManager__a(context);

    }

    public void updateUserFollowerCount() {
        Call<RestResponse> call = RetrofitBuilder_a.create().updateUserFollowerCount(Const_a.DEVKEY, sessionManager.getUser().getId());
        call.enqueue(new Callback<RestResponse>() {
            @Override
            public void onResponse(Call<RestResponse> call, Response<RestResponse> response) {
                if (response.code() == 200) {

                }
            }

            @Override
            public void onFailure(Call<RestResponse> call, Throwable t) {
                Log.d("TAG", "onFailure: followroort" + t.getMessage());
            }
        });
    }

    public void followUser(Context context, String myId, String guestId) {
        Call<RestResponse> call = RetrofitBuilder_a.create().follow(Const_a.DEVKEY, myId, guestId);
        call.enqueue(new Callback<RestResponse>() {
            @Override
            public void onResponse(Call<RestResponse> call, Response<RestResponse> response) {
                if (response.code() == 200) {
                    if (response.body().isStatus()) {
                        Toast.makeText(context, "Follow Successfully", Toast.LENGTH_SHORT).show();
                        responseListnear.responseSuccess();
                    } else {
                        responseListnear.responseFail();
                    }
                }
            }

            @Override
            public void onFailure(Call<RestResponse> call, Throwable t) {
                Log.d("TAG", "onFailure: followroort" + t.getMessage());
                responseListnear.responseFail();
            }
        });
    }


    public void setResponseListnear(ResponseListnear responseListnear) {
        this.responseListnear = responseListnear;
    }

    public void unfollowUser(Context context, String myId, String guestId) {
        Call<RestResponse> call = RetrofitBuilder_a.create().unfollow(Const_a.DEVKEY, myId, guestId);
        call.enqueue(new Callback<RestResponse>() {
            @Override
            public void onResponse(Call<RestResponse> call, Response<RestResponse> response) {
                if (response.code() == 200) {
                    if (response.body().isStatus()) {
                        Toast.makeText(context, "Unfollow Successfully", Toast.LENGTH_SHORT).show();
                        responseListnear.responseSuccess();
                    } else {
                        responseListnear.responseFail();
                    }
                }
            }

            @Override
            public void onFailure(Call<RestResponse> call, Throwable t) {
                Log.d("TAG", "onFailure: followroort" + t.getMessage());
                responseListnear.responseFail();
            }
        });
    }

    public void getHostProfile(String hostId, OnHostProfileGetListnear onHostProfileGetListnear) {
        this.onHostProfileGetListnear = onHostProfileGetListnear;
        Log.d("checkinggg ", "getData: apicalling 87");
        Call<HostRoot> call = RetrofitBuilder_a.create().getHostProfile(Const_a.DEVKEY, hostId);
        call.enqueue(new Callback<HostRoot>() {
            @Override
            public void onResponse(Call<HostRoot> call, Response<HostRoot> response) {
                if (response.code() == 200) {
                    if (response.body().isStatus() && response.body().getHost() != null) {
                        onHostProfileGetListnear.onHostGet(response.body().getHost());
                    } else {
                        onHostProfileGetListnear.onFail();
                    }
                }
            }

            @Override
            public void onFailure(Call<HostRoot> call, Throwable t) {
                onHostProfileGetListnear.onFail();
            }
        });
    }

    public void createChatRoom(String userId, String hostId, OnRoomGenereteListnear onRoomGenereteListnear) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("user_id", userId);
        jsonObject.addProperty("host_id", hostId);
        Call<ChatTopicRoot> call = RetrofitBuilder_a.create().createChatTopic(Const_a.DEVKEY, jsonObject);
        call.enqueue(new Callback<ChatTopicRoot>() {
            @Override
            public void onResponse(Call<ChatTopicRoot> call, Response<ChatTopicRoot> response) {
                if (response.code() == 200) {
                    if (response.body().isStatus() && response.body().getData() != null) {

                        onRoomGenereteListnear.onRoomGenereted(response.body().getData().getId());
                    } else {
                        onRoomGenereteListnear.onFail();
                    }
                } else {
                    onRoomGenereteListnear.onFail();
                }
            }

            @Override
            public void onFailure(Call<ChatTopicRoot> call, Throwable t) {
                onRoomGenereteListnear.onFail();
            }
        });
    }




    public void getCountryByIp(String ipAddress, IpResponseListnear ipResponseListnear) {
        Call<IpAddressDataRoot> call = RetrofitBuilder_a.getCountryByIp().getCountryByIp();
        call.enqueue(new Callback<IpAddressDataRoot>() {
            @Override
            public void onResponse(Call<IpAddressDataRoot> call, Response<IpAddressDataRoot> response) {
                if (response.code() == 200 && response.body() != null) {
                    if (response.body().getCountry() != null) {
                        ipResponseListnear.responseSuccess(response.body().getCountry());
                    } else {
                        ipResponseListnear.responseFail();
                    }
                } else {
                    ipResponseListnear.responseFail();
                }
            }

            @Override
            public void onFailure(Call<IpAddressDataRoot> call, Throwable t) {
                ipResponseListnear.responseFail();
            }
        });
    }

    public interface IpResponseListnear {
        void responseSuccess(String country);

        void responseFail();
    }

    public interface ResponseListnear {
        void responseSuccess();

        void responseFail();
    }

    public interface OnHostProfileGetListnear {
        void onHostGet(HostRoot.Host user);

        void onFail();
    }

    public interface OnRoomGenereteListnear {
        void onRoomGenereted(String roomId);

        void onFail();
    }
}
