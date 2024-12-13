package com.hi.live.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.databinding.DataBindingUtil;

import com.hi.live.R;
import com.hi.live.SessionManager__a;
import com.hi.live.adaptor.AdapterVideos_a;
import com.hi.live.databinding.ActivityFavouriteBinding;
import com.hi.live.models.GirlThumbListRoot;
import com.hi.live.retrofit.Const_a;
import com.hi.live.retrofit.RetrofitBuilder_a;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FavouriteActivityG_a extends BaseActivity_a {
    private static final String TAG = "favact";
    ActivityFavouriteBinding binding;
    SessionManager__a sessionManager;
    private String userId;
    private AdapterVideos_a adapterVideosG = new AdapterVideos_a();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_favourite);
        sessionManager = new SessionManager__a(this);
        userId = sessionManager.getUser().getId();

        MainActivityG_a.isHostLive = false;
        getData();


    }

    private void getData() {
        binding.lyt404.setVisibility(View.GONE);
        binding.shimmer.setVisibility(View.VISIBLE);
        binding.shimmer.startShimmer();
        Call<GirlThumbListRoot> call = RetrofitBuilder_a.create().getFaviourites(Const_a.DEVKEY, userId);
        call.enqueue(new Callback<GirlThumbListRoot>() {
            @Override
            public void onResponse(Call<GirlThumbListRoot> call, Response<GirlThumbListRoot> response) {
                if (response.code() == 200) {
                    if (response.body().isStatus() && !response.body().getData().isEmpty()) {
                        adapterVideosG.addData(response.body().getData());
                        binding.rvvideos.setAdapter(adapterVideosG);

                    } else {
                        binding.lyt404.setVisibility(View.VISIBLE);
                    }
                } else {
                    binding.lyt404.setVisibility(View.VISIBLE);
                }
                binding.shimmer.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call<GirlThumbListRoot> call, Throwable t) {
                Log.d(TAG, "onFailure: " + t.getMessage());
                binding.lyt404.setVisibility(View.VISIBLE);
            }
        });
    }

    public void onClickBack(View view) {
        onBackPressed();
    }
}