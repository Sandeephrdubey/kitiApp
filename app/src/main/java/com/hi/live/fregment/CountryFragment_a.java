package com.hi.live.fregment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.hi.live.R;
import com.hi.live.adaptor.CountryAdapter_a;
import com.hi.live.databinding.FragmentCountryBinding;
import com.hi.live.models.CountryRoot;
import com.hi.live.retrofit.Const_a;
import com.hi.live.retrofit.RetrofitBuilder_a;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CountryFragment_a extends Fragment {
    FragmentCountryBinding binding;
    OnCountryClickListner onCountryClickListner;
    private List<CountryRoot.CountryItem> countries = new ArrayList<>();
    private String selectedCountryName;

    public CountryFragment_a() {
    }

    public CountryFragment_a(String selectedCountryName) {

        this.selectedCountryName = selectedCountryName;
    }

    public OnCountryClickListner getOnCountryClickListner() {
        return onCountryClickListner;
    }

    public void setOnCountryClickListner(OnCountryClickListner onCountryClickListner) {
        this.onCountryClickListner = onCountryClickListner;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_country, container, false);
        return binding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.d("TAG", "onActivityCreated: countretyfrag ");

        getCountries();


    }

    private void getCountries() {
        countries.clear();
        countries.add(0, new CountryRoot.CountryItem("GLOBAL"));

        Call<CountryRoot> call = RetrofitBuilder_a.create().getCountries(Const_a.DEVKEY);
        call.enqueue(new Callback<CountryRoot>() {
            @Override
            public void onResponse(Call<CountryRoot> call, Response<CountryRoot> response) {
                if (response.code() == 200 && response.body().isStatus() && !response.body().getCountry().isEmpty()) {

                    countries.addAll(response.body().getCountry());
                    initView();
                }
                binding.shimmer.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call<CountryRoot> call, Throwable t) {
//ll
            }
        });
    }

    private void initView() {
        CountryAdapter_a countryAdapter = new CountryAdapter_a(selectedCountryName);
        countryAdapter.addCountry(countries);
        binding.rvCountry.setAdapter(countryAdapter);
        countryAdapter.setOnCountryClickListner(countryItem -> onCountryClickListner.onCountryClick(countryItem));
    }

    public interface OnCountryClickListner {
        void onCountryClick(CountryRoot.CountryItem countryItem);
    }
}