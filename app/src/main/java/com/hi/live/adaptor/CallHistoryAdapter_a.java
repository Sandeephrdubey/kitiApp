package com.hi.live.adaptor;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.hi.live.R;
import com.hi.live.activity.GuestProfileActivityG_a;
import com.hi.live.databinding.ItemCallHistoryBinding;
import com.hi.live.models.CallHistoryListRoot;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class CallHistoryAdapter_a extends RecyclerView.Adapter<CallHistoryAdapter_a.CallHistoryViewHolder> {
    private static final String TAG = "CallHistoryAdapter_a";
    OnCallClickListner onCallClickListner;
    private Context context;
    private List<CallHistoryListRoot.CallHistoryItem> callHistory = new ArrayList<>();

    public OnCallClickListner getOnCallClickListner() {
        return onCallClickListner;
    }

    public void setOnCallClickListner(OnCallClickListner onCallClickListner) {
        this.onCallClickListner = onCallClickListner;
    }

    @NonNull
    @NotNull
    @Override
    public CallHistoryViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        return new CallHistoryViewHolder(LayoutInflater.from(context).inflate(R.layout.item_call_history, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull CallHistoryViewHolder holder, int position) {

        holder.setData(position);
    }

    @Override
    public int getItemCount() {
        return callHistory.size();
    }

    public void addData(List<CallHistoryListRoot.CallHistoryItem> callHistory) {
        this.callHistory.addAll(callHistory);
        notifyItemRangeInserted(this.callHistory.size(), callHistory.size());
    }

    public void clear() {
        callHistory.clear();
        notifyDataSetChanged();
    }

    public interface OnCallClickListner {
        void onCallClick(CallHistoryListRoot.CallHistoryItem data);

        void onUserClick();
    }

    public class CallHistoryViewHolder extends RecyclerView.ViewHolder {
        ItemCallHistoryBinding binding;

        public CallHistoryViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            binding = ItemCallHistoryBinding.bind(itemView);
        }

        public void setData(int pos) {
            CallHistoryListRoot.CallHistoryItem data = callHistory.get(pos);
            Glide.with(context).load(data.getImage()).circleCrop().into(binding.ivUser);
            binding.name.setText(data.getName());
            binding.time.setText(data.getDate());
            binding.tvCoin.setText(String.valueOf(data.getCoin()));
            binding.tvCallTime.setText(data.getTime());
            binding.tvStatus.setText(data.getType());

            binding.getRoot().setOnClickListener(v -> {
                context.startActivity(new Intent(context, GuestProfileActivityG_a.class).putExtra("guestId", data.getHostId()));
            });
            binding.ivCall.setOnClickListener(v -> {
                onCallClickListner.onCallClick(data);
            });
            if (callHistory.get(pos).getType().equals("Outgoing")) {
                binding.tvStatus.setBackgroundTintList(ContextCompat.getColorStateList(context, R.color.cgreen2));
                binding.tvStatus.setTextColor(ContextCompat.getColor(context, R.color.white));
            } else if (callHistory.get(pos).getType().equals("Incoming")) {
                binding.tvStatus.setBackgroundTintList(ContextCompat.getColorStateList(context, R.color.yellow));
                binding.tvStatus.setTextColor(ContextCompat.getColor(context, R.color.white));
            } else if (callHistory.get(pos).getType().equals("MissedCall")) {
                binding.tvStatus.setBackgroundTintList(ContextCompat.getColorStateList(context, R.color.red));
                binding.tvStatus.setTextColor(ContextCompat.getColor(context, R.color.white));
            }

        }
    }
}
