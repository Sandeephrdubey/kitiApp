package com.hi.live.fregment;

import android.content.Intent;
import android.os.Bundle;
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
import com.hi.live.activity.callwork.CallApiWork_a;
import com.hi.live.activity.callwork.CallRequestActivity_a;
import com.hi.live.adaptor.CallHistoryAdapter_a;
import com.hi.live.adaptor.HistoryAdapter_a;
import com.hi.live.adaptor.RechargeAdapter_a;
import com.hi.live.databinding.FragmentHistoryBinding;
import com.hi.live.models.CallCreateRoot;
import com.hi.live.models.CallHistoryListRoot;
import com.hi.live.models.CoinHistoryRoot;
import com.hi.live.models.RechargeHistoryRoot;
import com.hi.live.popus.CustomDialogClass;
import com.hi.live.retrofit.Const_a;
import com.hi.live.retrofit.RetrofitBuilder_a;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class HistoryFragment_a extends Fragment {

    FragmentHistoryBinding binding;
    SessionManager__a sessionManager;
    HistoryAdapter_a historyAdapter = new HistoryAdapter_a(1);

    CallHistoryAdapter_a callHistoryAdapterG = new CallHistoryAdapter_a();
    CustomDialogClass customDialogClass;
    private int position;
    private boolean isLoding = false;
    private int start = 0;
    private String uid;
    ;
    private RechargeAdapter_a rechargeAdapter = new RechargeAdapter_a(2);

    public HistoryFragment_a(int position) {

        this.position = position;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_history, container, false);
        return binding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        MainActivityG_a.isHostLive = false;
        sessionManager = new SessionManager__a(getActivity());
        customDialogClass = new CustomDialogClass(requireActivity(), R.style.customStyle);
        customDialogClass.setCancelable(false);
        customDialogClass.setCanceledOnTouchOutside(false);
        if (sessionManager.getBooleanValue(Const_a.ISLOGIN)) {
            uid = sessionManager.getUser().getId();

            if (position == 0) {
                binding.tv3.setText("Money");

                binding.rvHistory.setAdapter(rechargeAdapter);
                getrechargeData(false);
            } else if (position == 1) {
                binding.tv3.setText("User");
                binding.rvHistory.setAdapter(callHistoryAdapterG);
                getCallHistory(false);
                binding.lyttop.setVisibility(View.GONE);
            } else {
                binding.tv3.setText("User");

                binding.rvHistory.setAdapter(historyAdapter);
                getCoinOutflowData(false);
            }

        }

        if (position == 0) {
            binding.swipeRefresh.setOnRefreshListener(refreshLayout -> {
                getrechargeData(false);
            });
            binding.swipeRefresh.setOnLoadMoreListener(refreshLayout -> {
                getrechargeData(true);
            });
            binding.tv3.setText("Money");
        } else if (position == 1) {
            binding.tv3.setText("User");
            binding.swipeRefresh.setOnRefreshListener(refreshLayout -> {
                getCallHistory(false);
            });
            binding.swipeRefresh.setOnLoadMoreListener(refreshLayout -> {
                getCallHistory(true);
            });
        } else {
            binding.tv3.setText("User");
            binding.swipeRefresh.setOnRefreshListener(refreshLayout -> {
                getCoinOutflowData(false);
            });
            binding.swipeRefresh.setOnLoadMoreListener(refreshLayout -> {
                getCoinOutflowData(true);
            });
        }


        callHistoryAdapterG.setOnCallClickListner(new CallHistoryAdapter_a.OnCallClickListner() {
            @Override
            public void onCallClick(CallHistoryListRoot.CallHistoryItem data) {
                if (sessionManager.getUser().getCoin() >= sessionManager.getSetting().getUserCallCharge()) {

                    customDialogClass.show();
                    CallApiWork_a apiCallingA = new CallApiWork_a(requireActivity());
                    apiCallingA.createCallRequest(data.getHostId(), new CallApiWork_a.CallApiListner() {
                        @Override
                        public void onSuccess(CallCreateRoot.CallId callId) {
                        /*String chenal = data.getHostId();
                        String clientId = sessionManager.getUser().getId();
                        try {
                            String tkn = RtcTokenBuilderSample.main(sessionManager.getSetting().getAgoraId(), sessionManager.getSetting().getAgoraCertificate(), chenal);

                            VideoCallDataRoot videoCallDataRoot = new VideoCallDataRoot();
                            videoCallDataRoot.setHostName(data.getName());
                            videoCallDataRoot.setChannel(chenal);
                            videoCallDataRoot.setClientId(clientId);
                            videoCallDataRoot.setHostId(data.getHostId());
                            videoCallDataRoot.setToken(tkn);
                            videoCallDataRoot.setClientImage(sessionManager.getUser().getImage());
                            videoCallDataRoot.setHostImage(data.getImage());
                            videoCallDataRoot.setClientName(sessionManager.getUser().getName());
                            startActivity(new Intent(getActivity(), CallRequestActivity_a.class).putExtra(Const_a.CALL_DATA, new Gson().toJson(videoCallDataRoot)));

                        } catch (Exception e) {
                            e.printStackTrace();
                        }*/
                            requireActivity().startActivity(new Intent(requireActivity(), CallRequestActivity_a.class).putExtra(Const_a.IS_PRIVATE, true).putExtra(Const_a.IS_FROM_LIST, true).putExtra(Const_a.CALL_DATA, new Gson().toJson(callId)));
                            Log.d("CALLLLLL", "onMakeCall: History FRagment");
                            customDialogClass.dismiss();
                        }

                        @Override
                        public void onFailure(String error) {
                            customDialogClass.dismiss();
                            if (isAdded()) {
                                Toast.makeText(requireActivity(), data.getName() + " is not able receive call.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }, "user");
                } else {
                    Toast.makeText(requireActivity(), "Insufficient Coins!", Toast.LENGTH_SHORT).show();
                }


            }

            @Override
            public void onUserClick() {

            }
        });
    }

    private void getCallHistory(boolean isLoadMore) {
        if (isLoadMore) {
            start += Const_a.LIMIT;
        } else {
            start = 0;
            callHistoryAdapterG.clear();
            binding.pd.setVisibility(View.VISIBLE);
        }
        Call<CallHistoryListRoot> call = RetrofitBuilder_a.create().getCallHistoryList(Const_a.DEVKEY, sessionManager.getUser().getId(), start, Const_a.LIMIT);
        call.enqueue(new Callback<CallHistoryListRoot>() {
            @Override
            public void onResponse(Call<CallHistoryListRoot> call, Response<CallHistoryListRoot> response) {
                if (response.code() == 200) {
                    if (response.body().isStatus() && !response.body().getCallHistory().isEmpty()) {
                        callHistoryAdapterG.addData(response.body().getCallHistory());
                        binding.lyt404.setVisibility(View.GONE);
                    } else if (start == 0 && response.body().getCallHistory().isEmpty()) {
                        binding.lyt404.setVisibility(View.VISIBLE);
                    }
                }
                binding.swipeRefresh.finishRefresh();
                binding.swipeRefresh.finishLoadMore();
                binding.pd.setVisibility(View.GONE);
                isLoding = true;
            }

            @Override
            public void onFailure(Call<CallHistoryListRoot> call, Throwable t) {
                Log.d("TAG", "onFailure: " + t.getMessage());
                binding.lyt404.setVisibility(View.VISIBLE);
                binding.pd.setVisibility(View.GONE);
                binding.swipeRefresh.finishRefresh();
                binding.swipeRefresh.finishLoadMore();
            }
        });
    }


    private void getrechargeData(boolean isLoadMore) {
        if (isLoadMore) {
            start += Const_a.LIMIT;
        } else {
            start = 0;
            rechargeAdapter.clear();
            binding.pd.setVisibility(View.VISIBLE);
        }
        Call<RechargeHistoryRoot> call = RetrofitBuilder_a.create().getRechargeHistory(Const_a.DEVKEY, uid, start, Const_a.LIMIT);
        call.enqueue(new Callback<RechargeHistoryRoot>() {
            @Override
            public void onResponse(Call<RechargeHistoryRoot> call, Response<RechargeHistoryRoot> response) {

                if (response.code() == 200 && response.body().isStatus() && !response.body().getData().isEmpty()) {
                    rechargeAdapter.addRecharges(response.body().getData());
                    isLoding = false;
                } else if (start == 0 && response.body().getData().isEmpty()) {
                    binding.lyt404.setVisibility(View.VISIBLE);
                }
                binding.swipeRefresh.finishRefresh();
                binding.swipeRefresh.finishLoadMore();
                binding.pd.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call<RechargeHistoryRoot> call, Throwable t) {
                binding.lyt404.setVisibility(View.VISIBLE);
                binding.pd.setVisibility(View.GONE);
                binding.swipeRefresh.finishRefresh();
                binding.swipeRefresh.finishLoadMore();
                Log.d("TAG", "onFailure: " + t.getMessage());
            }
        });
    }

    private void getCoinOutflowData(boolean isLoadMore) {
        if (isLoadMore) {
            start += Const_a.LIMIT;
        } else {
            start = 0;
            historyAdapter.clear();
            binding.pd.setVisibility(View.VISIBLE);
        }
        Call<CoinHistoryRoot> call = RetrofitBuilder_a.create().getcoinoutflowHistory(Const_a.DEVKEY, uid, start, Const_a.LIMIT);
        call.enqueue(new Callback<CoinHistoryRoot>() {
            @Override
            public void onResponse(Call<CoinHistoryRoot> call, Response<CoinHistoryRoot> response) {
                if (response.code() == 200 && response.body().isStatus() && !response.body().getData().isEmpty()) {
                    binding.lyt404.setVisibility(View.GONE);
                    historyAdapter.setType(1);
                    historyAdapter.addData(response.body().getData());
                    isLoding = false;
                } else if (start == 0 && response.body().getData().isEmpty()) {
                    binding.lyt404.setVisibility(View.VISIBLE);
                }
                binding.swipeRefresh.finishRefresh();
                binding.swipeRefresh.finishLoadMore();
                binding.pd.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call<CoinHistoryRoot> call, Throwable t) {
                Log.d("TAG", "onFailure: " + t.getMessage());
                binding.lyt404.setVisibility(View.VISIBLE);
                binding.swipeRefresh.finishRefresh();
                binding.swipeRefresh.finishLoadMore();
            }
        });
    }
}