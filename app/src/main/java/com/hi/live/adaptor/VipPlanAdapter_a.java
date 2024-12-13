package com.hi.live.adaptor;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.hi.live.R;
import com.hi.live.databinding.ItemVipplansBinding;
import com.hi.live.models.VipPlanRoot;

import java.text.DecimalFormat;
import java.util.List;

public class VipPlanAdapter_a extends RecyclerView.Adapter<VipPlanAdapter_a.VipViewHolder> {
    private List<VipPlanRoot.DataItem> data;
    private String country;
    private OnBuyCoinClickListnear onBuyCoinClickListnear;
    private Context context;


    public VipPlanAdapter_a(List<VipPlanRoot.DataItem> data, String country, OnBuyCoinClickListnear onBuyCoinClickListnear) {

        this.data = data;
        this.country = country;
        this.onBuyCoinClickListnear = onBuyCoinClickListnear;
    }

    @NonNull
    @Override
    public VipViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        return new VipViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_vipplans, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull VipViewHolder holder, int position) {
        VipPlanRoot.DataItem item = data.get(position);
        double amount;
        String currentCurrency;

        DecimalFormat df = new DecimalFormat("###.##");
        df.setMaximumFractionDigits(2);
        amount = item.getPrice();
        currentCurrency = "INR";

        item.setPrice(amount);
        holder.binding.imgBestOffer.setVisibility(View.GONE);
        holder.binding.tvMonth1.setText(item.getTime());
        holder.binding.txtPrice.setText(String.valueOf(item.getPrice()));
        holder.binding.txtPriceWithMonth.setText(currentCurrency + " " + item.getPrice() + "/" + item.getTime().split(" ")[1]);

        holder.itemView.setOnClickListener(v -> onBuyCoinClickListnear.onButClick(data.get(position)));

        holder.binding.tvcurrency.setText(currentCurrency);
        if (position == 0) {
            holder.binding.imgBestOffer.setVisibility(View.VISIBLE);
        }


    }

   /* private void calculateCurrency(VipPlanRoot.DataItem item, ItemVipplansBinding binding) {
        Call<CurrencyConveterRoot> call = RetrofitBuilder_a.getCurrencyAmount()
                .convertCurrency("INR", currentCurrency, String.valueOf(item.getPrice()));
        call.enqueue(new Callback<CurrencyConveterRoot>() {
            @Override
            public void onResponse(Call<CurrencyConveterRoot> call, Response<CurrencyConveterRoot> response) {
                if (response.code() == 200 && response.body() != null) {
                    Log.d("TAG", "onResponse:  successyyy");
                    DecimalFormat df = new DecimalFormat("###.##");
                    double amount = response.body().getResult();
                    binding.txtPrice.setText(String.valueOf(df.format(amount)));
                    binding.txtPriceWithMonth.setText(currentCurrency + " " + df.format(amount) + "/" + item.getTime().split(" ")[1]);

                }
            }

            @Override
            public void onFailure(Call<CurrencyConveterRoot> call, Throwable t) {
                Log.d("TAG", "onFailure: err 82 " + t.getMessage());
            }
        });
    }*/


    @Override
    public int getItemCount() {
        return data.size();
    }

    public interface OnBuyCoinClickListnear {
        void onButClick(VipPlanRoot.DataItem dataItem);
    }

    public class VipViewHolder extends RecyclerView.ViewHolder {
        ItemVipplansBinding binding;

        public VipViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = ItemVipplansBinding.bind(itemView);
        }
    }
}
