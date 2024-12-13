package com.hi.live.activity;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;

import com.google.android.material.tabs.TabLayout;
import com.hi.live.R;
import com.hi.live.SessionManager__a;
import com.hi.live.adaptor.ViewPagerAdapter_a;
import com.hi.live.databinding.ActivityHistoryBinding;

public class HistoryActivityG_a extends BaseActivity_a {
    ActivityHistoryBinding binding;
    SessionManager__a sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_history);
        sessionManager = new SessionManager__a(this);


        ViewPagerAdapter_a viewPagerAdapter = new ViewPagerAdapter_a(getSupportFragmentManager(), false);
        binding.viewPager.setAdapter(viewPagerAdapter);
        binding.tablayout1.setupWithViewPager(binding.viewPager);

        settabLayout();


    }


    private void settabLayout() {
        binding.tablayout1.setTabGravity(TabLayout.GRAVITY_FILL);
        binding.tablayout1.removeAllTabs();
        binding.tablayout1.addTab(binding.tablayout1.newTab().setCustomView(createCustomView("Recharge")));
        binding.tablayout1.addTab(binding.tablayout1.newTab().setCustomView(createCustomView("Call")));
        binding.tablayout1.addTab(binding.tablayout1.newTab().setCustomView(createCustomView("Debit")));

    }

    private View createCustomView(String name) {


        View v = LayoutInflater.from(this).inflate(R.layout.custom_tabhorizontol, null);
        TextView tv = (TextView) v.findViewById(R.id.tvTab);
        tv.setText(name);
        tv.setTextColor(ContextCompat.getColor(this, R.color.me_pink));


        return v;

    }

    public void onClickBack(View view) {
        finish();
    }
}