package com.hi.live.activity;

import android.os.Bundle;
import android.view.View;

import androidx.databinding.DataBindingUtil;

import com.hi.live.R;
import com.hi.live.SessionManager__a;
import com.hi.live.adaptor.TikitAdaptor_a;
import com.hi.live.databinding.ActivityComplainListBinding;
import com.hi.live.models.ComplainRoot;
import com.hi.live.retrofit.Const_a;
import com.hi.live.retrofit.RetrofitBuilder_a;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ComplainListActivityG_a extends BaseActivity_a {
    ActivityComplainListBinding binding;
    private SessionManager__a sessonManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_complain_list);
        sessonManager = new SessionManager__a(this);
        getComplains();


    }

    private void getComplains() {
        binding.shimmer.setVisibility(View.VISIBLE);
        Call<ComplainRoot> call = RetrofitBuilder_a.create().getComplains(Const_a.DEVKEY, sessonManager.getUser().getId());
        call.enqueue(new Callback<ComplainRoot>() {
            @Override
            public void onResponse(Call<ComplainRoot> call, Response<ComplainRoot> response) {
                if (response.code() == 200 && response.body().isStatus() && !response.body().getData().isEmpty()) {
                    binding.rvTikit.setAdapter(new TikitAdaptor_a(response.body().getData()));

                } else {
                    binding.lyt404.setVisibility(View.VISIBLE);
                }
                binding.shimmer.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call<ComplainRoot> call, Throwable t) {

            }
        });
    }

    public void onClickBack(View view) {
        onBackPressed();
    }
}