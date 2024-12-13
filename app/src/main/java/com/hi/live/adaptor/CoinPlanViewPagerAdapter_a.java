package com.hi.live.adaptor;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.hi.live.activity.purchase.PlanListFragment_a;
import com.hi.live.activity.purchase.VipPlanListFragment_a;

import java.util.List;

public class CoinPlanViewPagerAdapter_a extends FragmentPagerAdapter {
    private List<String> paymentGateways;
    private boolean isVip;

    public CoinPlanViewPagerAdapter_a(@NonNull FragmentManager fm, List<String> paymentGateways, boolean isVip) {
        super(fm);
        this.paymentGateways = paymentGateways;
        this.isVip = isVip;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        Log.d("TAG", "getItem: " + position + paymentGateways.get(position));
        if (isVip) {
            return new VipPlanListFragment_a();
        } else {
            return new PlanListFragment_a();
        }
    }

    @Override
    public int getCount() {
        return paymentGateways.size();
    }
}
