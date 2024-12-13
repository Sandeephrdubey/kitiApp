package com.hi.live.fregment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.hi.live.R;
import com.hi.live.SessionManager__a;
import com.hi.live.activity.SearchActivity;
import com.hi.live.adaptor.ChatUsersAdapterOriginal_a;
import com.hi.live.databinding.FragmentMessageFregmentBinding;
import com.hi.live.models.ChatUserListRoot;
import com.hi.live.retrofit.Const_a;
import com.hi.live.retrofit.RetrofitBuilder_a;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class MessageFregment_a extends Fragment {
    private static final String TAG = "chatfrag";
    SessionManager__a sessionManager;
    FragmentMessageFregmentBinding binding;
    ChatUsersAdapterOriginal_a chatUsersAdapterOriginalG = new ChatUsersAdapterOriginal_a();
    Call<ChatUserListRoot> call;
    private String userid;
    public int start = 0;

    public MessageFregment_a() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_message_fregment, container, false);
        return binding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();
        initMain();
    }

    private void initMain() {
        sessionManager = new SessionManager__a(getActivity());
        userid = sessionManager.getUser().getId();
        initView();
        MessageFregment_a.this.start = 0;
        binding.shimmer.startShimmer();
        binding.lyt404.setVisibility(View.GONE);
        binding.shimmer.setVisibility(View.VISIBLE);
        chatUsersAdapterOriginalG = new ChatUsersAdapterOriginal_a();
        binding.rvuserlist.setAdapter(chatUsersAdapterOriginalG);
        getChatUserList(73, false);
        initListnear();
    }

    private void initListnear() {
        binding.swipeRefresh.setOnRefreshListener(refreshLayout -> {
            getChatUserList(79, false);
        });
        binding.swipeRefresh.setOnLoadMoreListener(refreshLayout -> {
            getChatUserList(82, true);
        });

        binding.imgsearch.setOnClickListener(v -> {
            startActivity(new Intent(getActivity(), SearchActivity.class));
            doTransition(Const_a.BOTTOM_TO_UP);
        });
    }

    private void getChatUserList(int line, boolean isLoadMore) {
        Log.d(TAG, "getChatUserList: line === " + line);
        if (isLoadMore) {
            start += Const_a.LIMIT;
        } else {
            chatUsersAdapterOriginalG.clear();
            start = 0;
            binding.shimmer.setVisibility(View.VISIBLE);
            binding.lyt404.setVisibility(View.GONE);
        }
        Call<ChatUserListRoot> call = RetrofitBuilder_a.create().getChatUserList(Const_a.DEVKEY, userid, start, Const_a.LIMIT);
        call.enqueue(new Callback<ChatUserListRoot>() {
            @Override
            public void onResponse(Call<ChatUserListRoot> call, Response<ChatUserListRoot> response) {
                if (response.code() == 200 && response.body().isStatus()) {
                    if (!response.body().getData().isEmpty()) {
                        chatUsersAdapterOriginalG.addData(response.body().getData());
                        sessionManager.saveStringValue(Const_a.CHAT_COUNT, String.valueOf(response.body().getData().size()));
                    } else if (start == 0 && response.body().getData().isEmpty()) {
                        binding.lyt404.setVisibility(View.VISIBLE);
                    }
                }
                binding.swipeRefresh.finishLoadMore();
                binding.swipeRefresh.finishRefresh();
                binding.shimmer.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call<ChatUserListRoot> call, Throwable t) {
                Log.d(TAG, "onFailure: " + t.getMessage());
                binding.lyt404.setVisibility(View.VISIBLE);
                binding.shimmer.setVisibility(View.GONE);
                binding.swipeRefresh.finishLoadMore();
                binding.swipeRefresh.finishRefresh();
            }
        });
    }

    private void initView() {
        binding.rvuserlist.setAdapter(chatUsersAdapterOriginalG);
        binding.tvtitle.setVisibility(View.VISIBLE);
        binding.imgsearch.setImageDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.ic_loupe));
    }

    public void doTransition(int type) {
        if (getActivity() != null) {
            if (type == Const_a.BOTTOM_TO_UP) {
                getActivity().overridePendingTransition(R.anim.enter_from_bottom, R.anim.exit_none);
            } else if (type == Const_a.UP_TO_BOTTOM) {
                getActivity().overridePendingTransition(R.anim.exit_none, R.anim.enter_from_up);
            }
        }
    }


}