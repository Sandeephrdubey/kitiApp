package com.hi.live.retrofit;

import com.google.gson.JsonObject;
import com.hi.live.models.User;
import com.hi.live.models.UserRoot;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CoinWork_a {
    OnCoinWorkLIstner onCoinWorkLIstner;



    public void setOnCoinWorkLIstner(OnCoinWorkLIstner onCoinWorkLIstner) {
        this.onCoinWorkLIstner = onCoinWorkLIstner;
    }

    public void transferCoin(String fromid, String toId, String coin) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("user_id", fromid);
        jsonObject.addProperty("host_id", toId);
        jsonObject.addProperty("coin", coin);
        Call<UserRoot> call = RetrofitBuilder_a.create().transferCoin(Const_a.DEVKEY, jsonObject);
        call.enqueue(new Callback<UserRoot>() {
            @Override
            public void onResponse(Call<UserRoot> call, Response<UserRoot> response) {
                if (response.code() == 200) {

                    if (response.body().isStatus() && response.body().getUser() != null) {
                        onCoinWorkLIstner.onSuccess(response.body().getUser());

                    } else if (!response.body().isStatus()) {
                        onCoinWorkLIstner.onInsufficientBalance();
                    }
                } else {
                    onCoinWorkLIstner.onFailure();
                }
            }

            @Override
            public void onFailure(Call<UserRoot> call, Throwable t) {
                onCoinWorkLIstner.onFailure();
            }
        });
    }

    public interface OnCoinWorkLIstner {
        void onSuccess(User user);

        void onFailure();

        void onInsufficientBalance();
    }
}
