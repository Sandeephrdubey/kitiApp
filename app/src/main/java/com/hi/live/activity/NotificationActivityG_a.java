package com.hi.live.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.hi.live.R;
import com.hi.live.SessionManager__a;
import com.hi.live.adaptor.NotificationAdapter_a;
import com.hi.live.databinding.ActivityNotificationBinding;
import com.hi.live.models.NotificationRoot;
import com.hi.live.retrofit.Const_a;
import com.hi.live.retrofit.RetrofitBuilder_a;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NotificationActivityG_a extends BaseActivity_a {
    ActivityNotificationBinding binding;
    SessionManager__a sessionManager;
    NotificationAdapter_a notificationAdapter = new NotificationAdapter_a();
    private boolean isLoding = false;
    private int start = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_notification);
        sessionManager = new SessionManager__a(this);
        binding.rvNotifications.setAdapter(notificationAdapter);
        binding.shimmer.startShimmer();
        getNotifications();
        MainActivityG_a.isHostLive = false;

        binding.rvNotifications.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (!binding.rvNotifications.canScrollVertically(1)) {
                    LinearLayoutManager manager = (LinearLayoutManager) binding.rvNotifications.getLayoutManager();
                    Log.d("TAG", "onScrollStateChanged: ");
                    int visibleItemcount = manager.getChildCount();
                    int totalitem = manager.getItemCount();
                    int firstvisibleitempos = manager.findFirstCompletelyVisibleItemPosition();
                    Log.d("TAG", "onScrollStateChanged:187   " + visibleItemcount);
                    Log.d("TAG", "onScrollStateChanged:188 " + totalitem);
                    if (!isLoding && (visibleItemcount + firstvisibleitempos >= totalitem) && firstvisibleitempos >= 0) {

                        start = start + Const_a.LIMIT;
                        getNotifications();

                    }
                }
            }
        });

    }

    private void getNotifications() {
        Call<NotificationRoot> call = RetrofitBuilder_a.create().getNotifications(sessionManager.getUser().getId(), start, Const_a.LIMIT);
        call.enqueue(new Callback<NotificationRoot>() {
            @Override
            public void onResponse(Call<NotificationRoot> call, Response<NotificationRoot> response) {
                if (response.code() == 200) {
                    if (response.body().isStatus() && !response.body().getData().isEmpty()) {
                        notificationAdapter.addData(response.body().getData());
                        isLoding = false;
                    } else if (start == 0 && response.body().getData().isEmpty()) {
                        binding.lyt404.setVisibility(View.VISIBLE);
                    }
                    binding.shimmer.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(Call<NotificationRoot> call, Throwable t) {
                binding.shimmer.setVisibility(View.GONE);
                binding.lyt404.setVisibility(View.VISIBLE);
            }
        });
    }

    public void onClickBack(View view) {
        onBackPressed();
    }
}