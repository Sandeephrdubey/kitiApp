package com.hi.live.adaptor;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.hi.live.R;
import com.hi.live.activity.ChatListActivityGOriginal_a;
import com.hi.live.activity.GuestProfileActivityG_a;
import com.hi.live.databinding.ItemFollowrsBinding;
import com.hi.live.models.FollowListRoot;
import java.util.ArrayList;
import java.util.List;

public class FollowAdapter_a extends RecyclerView.Adapter<FollowAdapter_a.FollowViewHolder> {
    private Context context;
    private List<FollowListRoot.FollowersItem> list = new ArrayList<>();


    @NonNull
    @Override
    public FollowViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        return new FollowViewHolder(LayoutInflater.from(context).inflate(R.layout.item_followrs, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull FollowViewHolder holder, int position) {

        FollowListRoot.HostId user = list.get(position).getHostId();
        Log.d("TAG", "onBindViewHolder: " + user.getName());
        Glide.with(context).load(user.getImage()).placeholder(R.drawable.shimmer_circle).circleCrop().error(R.drawable.shimmer_circle).circleCrop().into(holder.binding.imguser);
        holder.binding.tvname.setText(user.getName());
        holder.binding.tvusername.setText(user.getUsername());

        holder.binding.lytchat.setOnClickListener(v -> context.startActivity(new Intent(context, ChatListActivityGOriginal_a.class).putExtra("name", user.getName())
                .putExtra("image", user.getImage())
                .putExtra("hostid", user.getId())));
        holder.itemView.setOnClickListener(v -> context.startActivity(new Intent(context, GuestProfileActivityG_a.class).putExtra("guestId", user.getId())));

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void addData(List<FollowListRoot.FollowersItem> data) {
        for (int i = 0; i < data.size(); i++) {
            list.add(data.get(i));
            notifyItemInserted(list.size() - 1);
        }
    }

    public void clear() {
        list.clear();
        notifyDataSetChanged();
    }

    public class FollowViewHolder extends RecyclerView.ViewHolder {
        ItemFollowrsBinding binding;

        public FollowViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = ItemFollowrsBinding.bind(itemView);
        }
    }
}
