package com.hi.live.fregment;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.google.gson.Gson;
import com.hi.live.R;
import com.hi.live.SessionManager__a;
import com.hi.live.activity.MainActivityG_a;
import com.hi.live.activity.callwork.CallRequestActivity_a;
import com.hi.live.adaptor.AdapterHosts_a;
import com.hi.live.adaptor.VipImageAdaptor;
import com.hi.live.databinding.FragmentOnlineHostListBinding;
import com.hi.live.models.CallCreateRoot;
import com.hi.live.models.GirlThumbListRoot;
import com.hi.live.popus.CustomDialogClass;
import com.hi.live.retrofit.Const_a;
import com.hi.live.retrofit.RetrofitBuilder_a;
import com.hi.live.socket.CallHandler;
import com.hi.live.socket.MySocketManager;
import com.hi.live.socket.SocketConst;

import org.json.JSONException;
import org.json.JSONObject;

import io.socket.client.Socket;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class OnlineHostListFragment_a extends Fragment {
    private static final String TAG = "onlinehostfrag";
    FragmentOnlineHostListBinding binding;
    private boolean isGoneForIntent = false;
    AdapterHosts_a adapterVideos = new AdapterHosts_a();
    String selectedCountryName = "";
    int start = 0;
    String countryId = "GLOBAL";
    boolean isLoding = true;
    SessionManager__a sessionManager;
    Socket globalSoket;
    private CustomDialogClass customDialogClass;
    private Handler makeCallHandler = new Handler();
    private Runnable makeCallRunnable = new Runnable() {
        @Override
        public void run() {
            if (!requireActivity().isFinishing()) {
                if (customDialogClass != null) {
                    customDialogClass.dismiss();
                }
                Toast.makeText(requireActivity(), "Host is Currently busy.", Toast.LENGTH_SHORT).show();
            }
        }
    };


    public OnlineHostListFragment_a() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_online_host_list, container, false);
        return binding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        sessionManager = new SessionManager__a(getActivity());

        binding.tvRefresh.setOnClickListener(v -> {
            binding.tvRefresh.setVisibility(View.INVISIBLE);
            initMain(80);
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        initMain(90);
        getBanner();
        MainActivityG_a.isHostLive = false;
    }

    private void initMain(int line) {
        Log.d(TAG, "initMain: line ==== " + line);
        globalSoket = MySocketManager.getInstance().getSocet();
        MySocketManager.getInstance().addCallListener(callHandler);
        binding.shimmer.setVisibility(View.VISIBLE);
        binding.shimmer.startShimmer();
        customDialogClass = new CustomDialogClass(requireActivity(), R.style.customStyle);
        customDialogClass.setCanceledOnTouchOutside(false);
        customDialogClass.setCancelable(false);
        adapterVideos = new AdapterHosts_a();
        adapterVideos.clearAll();
        start = 0;
        isGoneForIntent = false;
        binding.rvvideos.setAdapter(adapterVideos);
        getData(false);


        binding.lytCountry.setOnClickListener(v -> {
            binding.rvvideos.setEnabled(false);
            CountryFragment_a countryFragment = new CountryFragment_a(selectedCountryName);
            getChildFragmentManager().beginTransaction().addToBackStack(null).add(R.id.frameCountry, countryFragment).commit();
            countryFragment.setOnCountryClickListner(countryItem -> {
                selectedCountryName = countryItem.getName();
                binding.tvCountryName.setText(countryItem.getName());
                Log.d(TAG, "onActivityCreated: cl" + countryItem.getName());
                getChildFragmentManager().beginTransaction().remove(countryFragment).commit();
                countryId = countryItem.getId();
                adapterVideos = new AdapterHosts_a();
                adapterVideos.clearAll();
                start = 0;
                getData(false);
                binding.rvvideos.setEnabled(true);
            });
        });
        binding.swipeRefresh.setOnRefreshListener(refreshLayout -> {
            getData(true);
        });
        binding.swipeRefresh.setOnLoadMoreListener(refreshLayout -> {
            getData(true);
        });

        binding.btnrefesh.setOnClickListener(v -> onResume());

        adapterVideos.setOnHostThumbClickListner((thumb, binding) -> {


            if (thumb.isOnline()) {
                Log.d(TAG, "initMain: setOnHostThumbClickListner  = isGoneForIntent ====== " + isGoneForIntent);
                OnlineHostListFragment_a.this.binding.rvvideos.setEnabled(false);
                customDialogClass.show();
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("user_id", sessionManager.getUser().getId());
                    jsonObject.put("host_id", thumb.get_id());
                    jsonObject.put("type", "user");
                    MySocketManager.getInstance().getSocet().emit(SocketConst.EVENT_MAKE_CALL, jsonObject);
                    Log.d(TAG, "initMain: emit = EVENT_MAKE_CALL =");
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }
            makeCallHandler.postDelayed(makeCallRunnable, 4000);
            MySocketManager.getInstance().getSocet().once(SocketConst.EVENT_MAKE_CALL, args1 -> {
                if (args1[0] != null) {
                    if (!requireActivity().isFinishing() && getActivity() != null && isAdded()) {
                        requireActivity().runOnUiThread(() -> {
                            if (!isGoneForIntent) {
                                isGoneForIntent = true;
                                Log.d(TAG, "onMakeCall: isGoneForIntent   ===== " + isGoneForIntent);
                                CallCreateRoot.CallId call = new Gson().fromJson(args1[0].toString(), CallCreateRoot.CallId.class);
                                requireActivity().startActivity(new Intent(requireActivity(), CallRequestActivity_a.class).putExtra(Const_a.IS_FROM_LIST, true).putExtra(Const_a.CALL_DATA, new Gson().toJson(call)));
                                Log.d("CALLLLLL", "onMakeCall: Online Fragment");
                                OnlineHostListFragment_a.this.binding.rvvideos.setEnabled(true);
                                makeCallHandler.removeCallbacks(makeCallRunnable);
                                customDialogClass.dismiss();
                            }

                        });
                    }
                }

                if (args1[1] != null) {
                    if (!requireActivity().isFinishing() && getActivity() != null && isAdded()) {
                        requireActivity().runOnUiThread(() -> {
                            Log.d(TAG, "onMakeCall:  args[1].toString() === " + args1[1].toString());
                            if (!requireActivity().isFinishing()) {
                                if (customDialogClass != null) {
                                    customDialogClass.dismiss();
                                }
                                Toast.makeText(requireActivity(), "Host is Currently busy.", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
            });
        });
    }


    private void getBanner() {
//        if (sessionManager.getBooleanValue(Const_a.DownloadBanner)) {
//            binding.imageSlider.setVisibility(View.VISIBLE);
//            binding.imageSlider.setSliderAdapter(new VipImageAdaptor(sessionManager.getBanner().getBanner()));
//            binding.imageSlider.setIndicatorAnimation(IndicatorAnimationType.WORM); //set indicator animation by using IndicatorAnimationType. :WORM or THIN_WORM or COLOR or DROP or FILL or NONE or SCALE or SCALE_DOWN or SLIDE and SWAP!!
//            binding.imageSlider.setSliderTransformAnimation(SliderAnimations.SIMPLETRANSFORMATION);
//            binding.imageSlider.setAutoCycleDirection(SliderView.AUTO_CYCLE_DIRECTION_BACK_AND_FORTH);
//            binding.imageSlider.setIndicatorSelectedColor(Color.WHITE);
//            binding.imageSlider.setIndicatorUnselectedColor(Color.GRAY);
//            binding.imageSlider.setScrollTimeInSec(4); //set scroll delay in seconds :
//            binding.imageSlider.startAutoCycle();
//        } else {
//            binding.imageSlider.setVisibility(View.GONE);
//        }

    }

    private void getData(boolean isLoadMore) {
        if (isLoadMore) {
            start += Const_a.LIMIT;
        } else {
            start = 0;
            adapterVideos.clearAll();
            binding.shimmer.setVisibility(View.VISIBLE);
        }
        binding.lyt404.setVisibility(View.GONE);
        Call<GirlThumbListRoot> call = RetrofitBuilder_a.create().getOnlineHosts(Const_a.DEVKEY, countryId, start, Const_a.LIMIT);
        call.enqueue(new Callback<GirlThumbListRoot>() {
            @Override
            public void onResponse(Call<GirlThumbListRoot> call, Response<GirlThumbListRoot> response) {
                if (response.code() == 200 && response.body().isStatus()) {
                    if (!response.body().getData().isEmpty()) {
                        Log.d(TAG, "onResponse: responce done");
                        adapterVideos.addData(response.body().getData());
                        isLoding = false;
                    } else if (start == 0 && response.body().getData().isEmpty()) {
                        Log.d(TAG, "onResponse: data empty condition");
                        binding.lyt404.setVisibility(View.VISIBLE);
                    }
                }

                Log.d(TAG, "onResponse: responce come"+response.body().isStatus());
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


    private CallHandler callHandler = new CallHandler() {
        @Override
        public void onCallRequest(Object[] args) {

        }

        @Override
        public void onCallReceive(Object[] args) {

        }

        @Override
        public void onCallConfirm(Object[] args) {

        }

        @Override
        public void onCallAnswer(Object[] args) {

        }

        @Override
        public void onCallCancel(Object[] args) {

        }

        @Override
        public void onCallDisconnect(Object[] args) {

        }

        @Override
        public void onGiftRequest(Object[] args) {

        }

        @Override
        public void onVgift(Object[] args) {

        }

        @Override
        public void onComment(Object[] args) {

        }

        @Override
        public void onMakeCall(Object[] args) {

        }

        @Override
        public void onIsBusy(Object[] args) {

        }

        @Override
        public void onRefresh(Object[] args) {

        }
    };

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("CALLLLLL", "onDestroy: ONline ");
        MySocketManager.getInstance().removeCallListener(callHandler);
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d("CALLLLLL", "onStop: ONline ");
        MySocketManager.getInstance().removeCallListener(callHandler);
    }

}