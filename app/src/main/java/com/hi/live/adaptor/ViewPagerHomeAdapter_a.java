package com.hi.live.adaptor;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.hi.live.fregment.LiveListFragment_a;
import com.hi.live.fregment.OnlineHostListFragment_a;


public class ViewPagerHomeAdapter_a extends FragmentPagerAdapter {


    public ViewPagerHomeAdapter_a(@NonNull FragmentManager fm) {
        super(fm);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        if (position == 0) {
            return new OnlineHostListFragment_a();
        } else {
            return new LiveListFragment_a();
        }
    }

    @Override
    public int getCount() {
        return 2;
    }
}
