package com.hi.live.adaptor;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.hi.live.fregment.HistoryFragment_a;

public class ViewPagerAdapter_a extends FragmentPagerAdapter {

    private boolean isHost;

    public ViewPagerAdapter_a(@NonNull FragmentManager fm, boolean isHost) {
        super(fm);

        this.isHost = isHost;
    }


    @Override
    public int getCount() {
        if (isHost) {
            return 1;
        } else {
            return 3;
        }
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {


        return new HistoryFragment_a(position);

    }
}
