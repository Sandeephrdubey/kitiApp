package com.hi.live.adaptor;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.hi.live.R;
import com.hi.live.databinding.ItemBuycoinsBinding;
import com.hi.live.models.PlanRoot;

import java.text.DecimalFormat;
import java.util.List;

public class MoreCoinAdapter_a extends RecyclerView.Adapter<MoreCoinAdapter_a.MoreCoinViewHolder> {
    private List<PlanRoot.DataItem> data;
    private String currentCurrency;
    private OnBuyCoinClickListnear onBuyCoinClickListnear;
    private Context context;
    private String country;

    public MoreCoinAdapter_a(List<PlanRoot.DataItem> data, String country, OnBuyCoinClickListnear onBuyCoinClickListnear) {

        this.data = data;
        this.country = country;
        this.onBuyCoinClickListnear = onBuyCoinClickListnear;
    }

    @NonNull
    @Override
    public MoreCoinViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        return new MoreCoinViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_buycoins, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MoreCoinViewHolder holder, int position) {


        PlanRoot.DataItem item = data.get(position);
        DecimalFormat df = new DecimalFormat("###.##");
        df.setMaximumFractionDigits(2);

        double amount;
//        if (country.equalsIgnoreCase("INDIA")) {
//            amount = item.getRupee();
//            currentCurrency = "INR";
//        } else {
            amount = item.getRupee();
        currentCurrency = "INR";
//        }
        item.setRupee(amount);
        holder.buycoinsBinding.tvamount.setText(String.valueOf(item.getRupee()));
        holder.buycoinsBinding.tvcoin.setText(String.valueOf(item.getCoin()));
        holder.buycoinsBinding.tvbuy.setOnClickListener(v -> onBuyCoinClickListnear.onButClick(item));
        holder.buycoinsBinding.tvamounttype.setText(currentCurrency);

    }


/*
    private void calculateCurrency(PlanRoot.DataItem dataItem, ItemBuycoinsBinding binding) {
        Call<CurrencyConveterRoot> call = RetrofitBuilder_a.getCurrencyAmount()
                .convertCurrency("INR", currentCurrency, String.valueOf(dataItem.getRupee()));
        call.enqueue(new Callback<CurrencyConveterRoot>() {
            @Override
            public void onResponse(Call<CurrencyConveterRoot> call, Response<CurrencyConveterRoot> response) {
                if (response.code() == 200 && response.body() != null) {
                    DecimalFormat df = new DecimalFormat("###.##");


                    binding.tvamount.setText(String.valueOf(df.format(response.body().getResult())));

                }
            }

            @Override
            public void onFailure(Call<CurrencyConveterRoot> call, Throwable t) {

            }
        });
    }
*/

    @Override
    public int getItemCount() {
        return data.size();
    }

    public interface OnBuyCoinClickListnear {
        void onButClick(PlanRoot.DataItem dataItem);
    }

    public class MoreCoinViewHolder extends RecyclerView.ViewHolder {
        ItemBuycoinsBinding buycoinsBinding;

        public MoreCoinViewHolder(@NonNull View itemView) {
            super(itemView);
            buycoinsBinding = ItemBuycoinsBinding.bind(itemView);
        }
    }
}
