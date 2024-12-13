package com.hi.live.activity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;

import androidx.databinding.DataBindingUtil;

import com.hi.live.R;
import com.hi.live.adaptor.ChatUsersAdapterOriginal_a;
import com.hi.live.databinding.ActivitySearchBinding;
import com.hi.live.models.ChatUserListRoot;
import com.hi.live.retrofit.Const_a;
import com.hi.live.retrofit.RetrofitBuilder_a;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchActivity extends BaseActivity_a {
    private static final String TAG = "SearchActivity";
    private ActivitySearchBinding binding;
    private String keyword = "@#";
    private ChatUsersAdapterOriginal_a chatUsersAdapterOriginalG = new ChatUsersAdapterOriginal_a();

    private int start = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_search);
        initMain();
    }

    private void initMain() {
        initView();
        initListener();
    }

    private void initListener() {
        binding.backBtn.setOnClickListener(view -> onBackPressed());
        binding.swipeRefresh.setOnRefreshListener(refreshLayout -> {
            searchUserList(false);
        });
        binding.swipeRefresh.setOnLoadMoreListener(refreshLayout -> {
            searchUserList(true);
        });
        binding.etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//ll
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().equals("")) {
                    keyword = s.toString();
                } else {
                    keyword = "@#";
//                    keyword = "";
                }
//                searchUserList(false);
            }

            @Override
            public void afterTextChanged(Editable s) {
//ll
            }
        });

        binding.etSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    searchUserList(false);
                    return true;
                }
                return false;
            }
        });
    }

    private void initView() {
        binding.rvMessage.setAdapter(chatUsersAdapterOriginalG);
        searchUserList(false);
    }

    private void searchUserList(boolean isLoadMore) {
        if (isLoadMore) {
            start += Const_a.LIMIT;
        } else {
            chatUsersAdapterOriginalG.clear();
            start = 0;
            binding.shimmer.setVisibility(View.VISIBLE);
        }
        binding.lyt404.setVisibility(View.GONE);
        Call<ChatUserListRoot> call = RetrofitBuilder_a.create().getSearchList(Const_a.DEVKEY, keyword, sessionManager.getUser().getId(), start, Const_a.LIMIT);
        call.enqueue(new Callback<ChatUserListRoot>() {
            @Override
            public void onResponse(Call<ChatUserListRoot> call, Response<ChatUserListRoot> response) {
                if (response.code() == 200 && response.body().isStatus()) {
                    if (!response.body().getData().isEmpty()) {
                        chatUsersAdapterOriginalG.addData(response.body().getData());
                    } else if (start == 0 && response.body().getData().isEmpty()) {
                        binding.lyt404.setVisibility(View.VISIBLE);
                    }
                }
                binding.shimmer.setVisibility(View.GONE);
                binding.swipeRefresh.finishLoadMore();
                binding.swipeRefresh.finishRefresh();
            }

            @Override
            public void onFailure(Call<ChatUserListRoot> call, Throwable t) {
                Log.d(TAG, "onFailure: msg  " + t.getMessage());
                binding.shimmer.setVisibility(View.GONE);
                binding.lyt404.setVisibility(View.VISIBLE);
                binding.swipeRefresh.finishLoadMore();
                binding.swipeRefresh.finishRefresh();
            }
        });

    }

    public void onBackPressed() {
        super.onBackPressed();
        doTransition(Const_a.UP_TO_BOTTOM);
    }
}