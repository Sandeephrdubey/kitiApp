package com.hi.live.adaptor;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.hi.live.R;
import com.hi.live.databinding.ItemHistoryBinding;
import com.hi.live.models.CoinHistoryRoot;
import com.hi.live.models.RechargeHistoryRoot;

import java.util.ArrayList;
import java.util.List;

public class RechargeAdapter_a extends RecyclerView.Adapter<RechargeAdapter_a.HistoryViewHolder> {
    private List<CoinHistoryRoot.DataItem> data = new ArrayList<>();
    private int type;

    private List<RechargeHistoryRoot.DataItem> recharges = new ArrayList<>();

    public RechargeAdapter_a(int type) {

        this.type = type;
    }


    @NonNull
    @Override
    public HistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        return new HistoryViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_history, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull HistoryViewHolder holder, int position) {

        Log.d("TAG", "onBindViewHolder: " + recharges.get(position).getDate());
        holder.binding.tvcoin.setText(String.valueOf(recharges.get(position).getCoin()));
        holder.binding.tvdate.setText(recharges.get(position).getDate());
        holder.binding.tvrupees.setText(String.valueOf(recharges.get(position).getRupee()).concat(" INR"));
    }

    @Override
    public int getItemCount() {

        return recharges.size();


    }

    public void clear() {
        recharges.clear();
        notifyDataSetChanged();
    }

    public void addRecharges(List<RechargeHistoryRoot.DataItem> dataItems) {

        Log.d("TAG", "addRecharges: " + dataItems.size());
        this.recharges.addAll(dataItems);
        notifyItemRangeInserted(recharges.size(), dataItems.size());
    }

    public void setType(int i) {
        this.type = i;
    }

    public class HistoryViewHolder extends RecyclerView.ViewHolder {
        ItemHistoryBinding binding;

        public HistoryViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = ItemHistoryBinding.bind(itemView);
        }
    }
}
