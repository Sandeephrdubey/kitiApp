package com.hi.live.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.hi.live.R;
import com.hi.live.SessionManager__a;
import com.hi.live.databinding.ActivitySearchForNewFriendsBinding;
import com.hi.live.models.RandomMatchHostRoot;
import com.hi.live.retrofit.Const_a;
import com.hi.live.retrofit.RetrofitBuilder_a;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchForNewFriendsActivity_a extends AppCompatActivity {

    private static final String TAG = "SearchForNewFriendsActivity_a";
    ActivitySearchForNewFriendsBinding binding;
    Animation zoomin;

    Handler handler;
    Runnable runnable;
    SessionManager__a sessionManager;
    private String countryId = "GLOBAL";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_search_for_new_friends);


        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        sessionManager = new SessionManager__a(SearchForNewFriendsActivity_a.this);

        binding.back.setOnClickListener(v -> {
            finish();
            handler.removeCallbacks(runnable);
        });

        initmain();
    }

    private void initmain() {
        Glide.with(this.getApplicationContext())
                .load(R.drawable.ic_match_beauty_light)
                .circleCrop()
                .into(binding.ivUser);

        zoomin = AnimationUtils.loadAnimation(this, R.anim.zoomin);
        Animation animZoomin = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.zoomin);
        binding.ivUser.startAnimation(animZoomin);

        handler = new Handler();
        runnable = () -> {
            Call<RandomMatchHostRoot> call = RetrofitBuilder_a.create().getRandomHost(Const_a.DEVKEY);
            call.enqueue(new Callback<RandomMatchHostRoot>() {
                @Override
                public void onResponse(Call<RandomMatchHostRoot> call, Response<RandomMatchHostRoot> response) {
                    if (response.code() == 200 && response.body().isStatus()) {
                        if (response.body().getHost() != null) {
                            Intent i = new Intent(SearchForNewFriendsActivity_a.this, SearchNewFriendsDoneActivityG_a.class);
                            i.putExtra(Const_a.DATA, new Gson().toJson(response.body().getHost()));
                            if (!isFinishing()) {
                                startActivity(i);
                                finish();
                            }
                        }
                    } else {
                        if (!isFinishing()) {
                            Toast.makeText(SearchForNewFriendsActivity_a.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                            onBackPressed();
                        }
                    }
                    handler.removeCallbacks(runnable);
                    handler.removeCallbacksAndMessages(null);
                }

                @Override
                public void onFailure(Call<RandomMatchHostRoot> call, Throwable t) {
                    handler.removeCallbacks(runnable);
                    handler.removeCallbacksAndMessages(null);
                }
            });
        };
        handler.postDelayed(runnable, 4000);
    }


    @Override
    public void onBackPressed() {
        handler.removeCallbacks(runnable);
        super.onBackPressed();
    }

}