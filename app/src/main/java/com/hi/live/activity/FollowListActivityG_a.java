package com.hi.live.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.databinding.DataBindingUtil;

import com.hi.live.R;
import com.hi.live.SessionManager__a;
import com.hi.live.adaptor.FollowAdapter_a;
import com.hi.live.databinding.ActivityFollowListBinding;
import com.hi.live.models.FollowListRoot;
import com.hi.live.retrofit.Const_a;
import com.hi.live.retrofit.RetrofitBuilder_a;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FollowListActivityG_a extends BaseActivity_a {
    private static final String TAG = "followlistact";
    ActivityFollowListBinding binding;
    SessionManager__a sessionManager;
    FollowAdapter_a followAdapter = new FollowAdapter_a();
    private String userId;
    private boolean isLoding = false;
    private int start = 0;
    private String title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_follow_list);
        MainActivityG_a.isHostLive = false;
        sessionManager = new SessionManager__a(this);
        userId = sessionManager.getUser().getId();
        Intent intent = getIntent();
        if (intent != null) {
            title = intent.getStringExtra("title");
            if (title != null) {
                binding.tvtitle.setText(title);
                binding.shimmerstatus.startShimmerAnimation();
                if (title.equals("Followers")) {
                    getFollowrsList(false);
                } else {
                    getFollowingList(false);
                }
            }
        }
        binding.rvList.setAdapter(followAdapter);
        binding.swipeRefresh.setOnRefreshListener(refreshLayout -> {
            if (title.equals("Followers")) {
                getFollowrsList(false);
            } else {
                getFollowingList(false);
            }
        });
        binding.swipeRefresh.setOnLoadMoreListener(refreshLayout -> {
            if (title.equals("Followers")) {
                getFollowrsList(true);
            } else {
                getFollowingList(true);
            }
        });

    }

    private void getFollowingList(boolean isLoadMore) {
        if (isLoadMore) {
            start += Const_a.LIMIT;
        } else {
            followAdapter.clear();
            start = 0;
            binding.shimmerstatus.setVisibility(View.VISIBLE);
        }
        Call<FollowListRoot> call = RetrofitBuilder_a.create().followingList(Const_a.DEVKEY, userId, start, Const_a.LIMIT);
        call.enqueue(new Callback<FollowListRoot>() {
            @Override
            public void onResponse(Call<FollowListRoot> call, Response<FollowListRoot> response) {
                if (response.code() == 200 && response.body().isStatus() && !response.body().getFollowers().isEmpty()) {
                    followAdapter.addData(response.body().getFollowers());
                } else if (start == 0 && response.body().getFollowers().isEmpty()) {
                    binding.lyt404.setVisibility(View.GONE);
                }

                binding.shimmerstatus.setVisibility(View.GONE);
                binding.swipeRefresh.finishRefresh();
                binding.swipeRefresh.finishLoadMore();
            }

            @Override
            public void onFailure(Call<FollowListRoot> call, Throwable t) {
                Log.d(TAG, "onFailure: following err" + t.getMessage());
                binding.shimmerstatus.setVisibility(View.GONE);
                binding.swipeRefresh.finishRefresh();
                binding.swipeRefresh.finishLoadMore();
            }
        });
    }


    private void getFollowrsList(boolean isLoadMore) {
        if (isLoadMore) {
            start += Const_a.LIMIT;
        } else {
            followAdapter.clear();
            start = 0;
            binding.shimmerstatus.setVisibility(View.VISIBLE);
        }
        Call<FollowListRoot> call = RetrofitBuilder_a.create().followrsList(Const_a.DEVKEY, userId, start, Const_a.LIMIT);
        call.enqueue(new Callback<FollowListRoot>() {
            @Override
            public void onResponse(Call<FollowListRoot> call, Response<FollowListRoot> response) {
                if (response.code() == 200 && response.body().isStatus() && !response.body().getFollowers().isEmpty()) {
                    followAdapter.addData(response.body().getFollowers());
                } else if (start == 0 && response.body().getFollowers().isEmpty()) {
                    binding.lyt404.setVisibility(View.VISIBLE);
                }
                binding.shimmerstatus.setVisibility(View.GONE);
                binding.swipeRefresh.finishRefresh();
                binding.swipeRefresh.finishLoadMore();
            }

            @Override
            public void onFailure(Call<FollowListRoot> call, Throwable t) {
                binding.shimmerstatus.setVisibility(View.GONE);
                Log.d(TAG, "onFailure: followrs err" + t.getMessage());
                binding.swipeRefresh.finishRefresh();
                binding.swipeRefresh.finishLoadMore();
            }
        });
    }

    public void onClickBack(View view) {
        finish();
    }
}