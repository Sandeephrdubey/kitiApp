package com.hi.live.activity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowInsets;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;
import androidx.databinding.DataBindingUtil;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.hi.live.BuildConfig;
import com.hi.live.R;
import com.hi.live.databinding.ActivityTickitDetailBinding;
import com.hi.live.models.ComplainRoot;


public class TickitDetailActivity_a extends AppCompatActivity {
    ActivityTickitDetailBinding binding;
    private ComplainRoot.DataItem tickit;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View decorView = getWindow().getDecorView();
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
        binding = DataBindingUtil.setContentView(this, R.layout.activity_tickit_detail);

        Intent intent = getIntent();
        String datastr = intent.getStringExtra("tickit");
        if (datastr != null) {
            tickit = new Gson().fromJson(datastr, ComplainRoot.DataItem.class);
            if (tickit != null) {
                setData();
            }
        }
    }

    private void setData() {
        binding.tvTitle.setText(tickit.getContact());
        binding.tvDescription.setText(tickit.getMessage());

        binding.tvtime.setText(tickit.getCreatedAt());
        if (tickit.isSolved()) {
            binding.status.setText("SOLVED");
        } else {
            binding.status.setText("OPEN");
            binding.status.setBackgroundColor(ContextCompat.getColor(this, R.color.cgreen2));
        }
        if (tickit.getImage().equals("null")) {
            binding.imageview.setVisibility(View.GONE);
            binding.tvImage.setVisibility(View.GONE);
        } else {

            Glide.with(this).load(BuildConfig.BASE_URL + tickit.getImage())
                    .into(binding.imageview);
        }
    }

    public void onClickBack(View view) {
        finish();
    }
}