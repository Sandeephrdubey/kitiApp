package com.hi.live.fregment;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.gms.ads.AdView;
import com.hi.live.R;
import com.hi.live.SessionManager__a;
import com.hi.live.activity.MainActivityG_a;
import com.hi.live.adaptor.AdapterVideos_a;
import com.hi.live.adaptor.VipImageAdaptor;
import com.hi.live.databinding.FragmentLiveListBinding;
import com.hi.live.models.GirlThumbListRoot;
import com.hi.live.retrofit.Const_a;
import com.hi.live.retrofit.RetrofitBuilder_a;
import com.hi.live.retrofit.RetrofitService_a;

import io.socket.client.Socket;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class LiveListFragment_a extends Fragment {
    private static final String TAG = "livelist";


    // Required empty public constructor

    FragmentLiveListBinding binding;
    AdView adView;
    ProgressBar pd;
    SwipeRefreshLayout swipeRefreshLayout;

    int count = 2;
    AdapterVideos_a adapterVideosG = new AdapterVideos_a();
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
    private SessionManager__a sessionManager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_live_list, container, false);
        return binding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        sessionManager = new SessionManager__a(getActivity());
        binding.tvRefresh.setOnClickListener(v -> {
            binding.tvRefresh.setVisibility(View.INVISIBLE);
            initMain();
            getBanner();
        });
    }

    @Override
    public void onResume() {
        super.onResume();


        initMain();
        getBanner();

        MainActivityG_a.isHostLive = false;
    }

    private void initMain() {

        binding.shimmer.setVisibility(View.VISIBLE);
        binding.shimmer.startShimmer();

        adapterVideosG = new AdapterVideos_a();
        adapterVideosG.clearAll();
        start = 0;
        binding.rvvideos.setAdapter(adapterVideosG);
        getData(false);

        binding.swipeRefresh.setOnRefreshListener(refreshLayout -> {
            getData(false);
        });
        binding.swipeRefresh.setOnLoadMoreListener(refreshLayout -> {
            getData(true);
        });

        binding.lytCountry.setOnClickListener(v -> {
            if (binding.frameCountry.getVisibility() == View.VISIBLE) {
                binding.frameCountry.setVisibility(View.GONE);
            } else {
                binding.frameCountry.setVisibility(View.VISIBLE);
                binding.rvvideos.setEnabled(false);
                CountryFragment_a countryFragment = new CountryFragment_a(selectedCountryName);
                getChildFragmentManager().beginTransaction().addToBackStack(null).add(R.id.frameCountry, countryFragment).commit();
                countryFragment.setOnCountryClickListner(countryItem -> {
                    selectedCountryName = countryItem.getName();
                    binding.tvCountryName.setText(countryItem.getName());
                    Log.d(TAG, "onActivityCreated: cl     " + countryItem.getName());
                    getChildFragmentManager().beginTransaction().remove(countryFragment).commit();
                    countryId = countryItem.getId();
                    adapterVideosG = new AdapterVideos_a();
                    adapterVideosG.clearAll();
                    start = 0;
                    binding.frameCountry.setVisibility(View.GONE);
                    binding.rvvideos.setEnabled(true);
                    getData(false);
                });
            }
        });

        binding.btnrefesh.setOnClickListener(v -> onResume());
    }

    private void getData(boolean isLoadMore) {
        if (isLoadMore) {
            start += Const_a.LIMIT;
            binding.lyt404.setVisibility(View.GONE);
        } else {
            start = 0;
            adapterVideosG.clearAll();
            binding.shimmer.setVisibility(View.VISIBLE);
            binding.lyt404.setVisibility(View.GONE);
        }

        Call<GirlThumbListRoot> call = RetrofitBuilder_a.create().getThumbs(Const_a.DEVKEY, countryId, start, Const_a.LIMIT);
        call.enqueue(new Callback<GirlThumbListRoot>() {
            @Override
            public void onResponse(Call<GirlThumbListRoot> call, Response<GirlThumbListRoot> response) {
                if (response.code() == 200 && response.body().isStatus()) {
                    if (!response.body().getData().isEmpty()) {
                        adapterVideosG.addData(response.body().getData());
                        isLoding = false;
                    } else if (start == 0 && response.body().getData().isEmpty()) {
                        binding.lyt404.setVisibility(View.VISIBLE);
                    }
                }
                binding.shimmer.setVisibility(View.GONE);
                binding.swipeRefresh.finishRefresh();
                binding.swipeRefresh.finishLoadMore();
            }

            @Override
            public void onFailure(Call<GirlThumbListRoot> call, Throwable t) {
                Log.d(TAG, "onFailure: " + t.getMessage());
                binding.shimmer.setVisibility(View.GONE);
                binding.lyt404.setVisibility(View.VISIBLE);
                binding.swipeRefresh.finishRefresh();
                binding.swipeRefresh.finishLoadMore();
            }
        });
    }

    private void getBanner() {

//
//        if (sessionManager.getBooleanValue(Const_a.DownloadBanner)) {
//            binding.imageSlidernew.setVisibility(View.VISIBLE);
//            binding.imageSlidernew.setSliderAdapter(new VipImageAdaptor(sessionManager.getBanner().getBanner()));
//            binding.imageSlidernew.setIndicatorAnimation(IndicatorAnimationType.WORM); //set indicator animation by using IndicatorAnimationType. :WORM or THIN_WORM or COLOR or DROP or FILL or NONE or SCALE or SCALE_DOWN or SLIDE and SWAP!!
//            binding.imageSlidernew.setSliderTransformAnimation(SliderAnimations.SIMPLETRANSFORMATION);
//            binding.imageSlidernew.setAutoCycleDirection(SliderView.AUTO_CYCLE_DIRECTION_BACK_AND_FORTH);
//            binding.imageSlidernew.setIndicatorSelectedColor(Color.WHITE);
//            binding.imageSlidernew.setIndicatorUnselectedColor(Color.GRAY);
//            binding.imageSlidernew.setScrollTimeInSec(4); //set scroll delay in seconds :
//            binding.imageSlidernew.startAutoCycle();
//        } else {
//            binding.imageSlidernew.setVisibility(View.GONE);
//        }

    }

}