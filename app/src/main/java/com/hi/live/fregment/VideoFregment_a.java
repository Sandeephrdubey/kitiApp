package com.hi.live.fregment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.material.tabs.TabLayout;
import com.hi.live.R;
import com.hi.live.activity.NotificationActivityG_a;
import com.hi.live.adaptor.ViewPagerHomeAdapter_a;
import com.hi.live.databinding.FragmentVideoFregmentBinding;
import com.hi.live.retrofit.RetrofitBuilder_a;
import com.hi.live.retrofit.RetrofitService_a;

import java.util.ArrayList;
import java.util.List;

import io.socket.client.Socket;


public class VideoFregment_a extends Fragment {
    private static final String TAG = "custumview";
    FragmentVideoFregmentBinding binding;

    AdView adView;
    ProgressBar pd;
    SwipeRefreshLayout swipeRefreshLayout;

    int count = 2;

    View view;

    FragmentActivity context;
    String ownAdBannerUrl;
    com.facebook.ads.AdView adViewfb;
    ImageView imgOwnAd;
    String adid;
    String ownWebUrl;
    RetrofitService_a service;
    String selectedCountryName = "";

    Socket socket;
    int start = 0;
    String countryId = "GLOBAL";
    boolean isLoding = true;


    public VideoFregment_a() {
//ll
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_video_fregment, container, false);
        context = getActivity();
        service = RetrofitBuilder_a.create();


        return binding.getRoot();
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
//        MobileAds.initialize(getActivity(), initializationStatus -> {
//        });

        Log.d(TAG, "onActivityCreated: ");


        binding.imgnotification.setOnClickListener(v -> startActivity(new Intent(getActivity(), NotificationActivityG_a.class)));

        setTabLyt();
        if (getActivity() != null) {
            List<String> contry = new ArrayList<>();
            contry.add("Video Call");
            contry.add("Live Streaming");
            settab(contry);
        }
    }

    private void setTabLyt() {
        if (getActivity() != null) {
            binding.viewPager.setAdapter(new ViewPagerHomeAdapter_a(getChildFragmentManager()));
            binding.tablayout1.setupWithViewPager(binding.viewPager);
            binding.tablayout1.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
                @Override
                public void onTabSelected(TabLayout.Tab tab) {
                    //ll
                }

                @Override
                public void onTabUnselected(TabLayout.Tab tab) {
                    //ll
                }

                @Override
                public void onTabReselected(TabLayout.Tab tab) {
//ll
                }
            });
        }
    }

    private void settab(List<String> contry) {
        binding.tablayout1.setTabGravity(TabLayout.GRAVITY_FILL);
        binding.tablayout1.removeAllTabs();
        for (int i = 0; i < contry.size(); i++) {
            binding.tablayout1.addTab(binding.tablayout1.newTab().setCustomView(createCustomView(contry.get(i))));
        }

    }

    private View createCustomView(String s) {

        View v = LayoutInflater.from(getActivity()).inflate(R.layout.custom_tabhorizontol, null);
        TextView tv = (TextView) v.findViewById(R.id.tvTab);
        tv.setText(s);
        tv.setTextColor(ContextCompat.getColor(getActivity(), R.color.me_pink));

        return v;

    }

}





