package com.hi.live.adaptor;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.hi.live.fregment.MessageFregment_a;
import com.hi.live.fregment.NewHomeFragment;
import com.hi.live.fregment.ProfileFregment_g;
import com.hi.live.fregment.VideoOfflineFragment_a;


public class ScreenNewSlidePagerAdapter extends FragmentStateAdapter {
    public ScreenNewSlidePagerAdapter(FragmentActivity fa) {
        super(fa);
    }

    @Override
    public Fragment createFragment(int position) {
        if (position == 0) {
            return new NewHomeFragment();
        } else if (position == 1) {
            return new VideoOfflineFragment_a();
        }

        else if (position == 2) {
            return new MessageFregment_a();
        }
        else if (position == 3) {
            return new com.hi.live.fragment.FeedListFragment();
        }

        else if (position == 4) {
            return new ProfileFregment_g();
        }

        else {
            return new ProfileFregment_g();
        }
    }

    @Override
    public int getItemCount() {
        return 5;
    }
}