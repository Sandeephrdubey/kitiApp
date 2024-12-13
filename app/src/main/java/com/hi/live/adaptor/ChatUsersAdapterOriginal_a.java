package com.hi.live.adaptor;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.hi.live.R;
import com.hi.live.SessionManager__a;
import com.hi.live.activity.ChatActivityGFake_a;
import com.hi.live.activity.ChatListActivityGOriginal_a;
import com.hi.live.databinding.ItemChatusersBinding;
import com.hi.live.models.ChatUserListRoot;

import java.util.ArrayList;
import java.util.List;

public class ChatUsersAdapterOriginal_a extends RecyclerView.Adapter<ChatUsersAdapterOriginal_a.ChatViewholder> {
    private List<ChatUserListRoot.DataItem> data = new ArrayList<>();
    private Context context;
    private SessionManager__a sessionManager;

    @NonNull
    @Override
    public ChatViewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        sessionManager = new SessionManager__a(context);
        return new ChatViewholder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chatusers, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ChatViewholder holder, int position) {
        ChatUserListRoot.DataItem user = data.get(position);
        if (user.getImage() != null) {
            Glide.with(context).load(user.getImage()).circleCrop().into(holder.binding.imguser);
        }
        if (user.getName() != null) {
            holder.binding.tvusername.setText(user.getName());
        }
        if (user.getMessage() != null) {
            holder.binding.tvlastchet.setText(user.getMessage());
        }
        if (user.getTime() != null) {
            holder.binding.tvtime.setText(user.getTime());
        }
        holder.binding.tvcountry.setText(user.getCountryName());
        Log.d("TAG", "onBindViewHolder: image " + user.getImage());

        holder.itemView.setOnClickListener(v -> {
            if (!user.isFake()) {
                context.startActivity(new Intent(context, ChatListActivityGOriginal_a.class).putExtra("name", user.getName())
                        .putExtra("image", user.getImage())
                        .putExtra("isFake", user.isFake())
                        .putExtra("chatTopic", user.getTopic())
                        .putExtra("hostid", user.getId()));
            } else if (user.getId().equals(sessionManager.getUser().getId())) {
                Toast.makeText(context, "You cannot text yourself.", Toast.LENGTH_SHORT).show();
            } else {
                context.startActivity(new Intent(context, ChatActivityGFake_a.class).putExtra("name", user.getName())
                        .putExtra("image", user.getImage())
                        .putExtra("isFake", user.isFake())
                        .putExtra("hostid", user.getId()));
            }

        });
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public void addData(List<ChatUserListRoot.DataItem> data) {
        this.data.addAll(data);
        notifyItemRangeInserted(this.data.size(), data.size());
    }

    public void clear() {
        this.data.clear();
        notifyDataSetChanged();
    }

    public class ChatViewholder extends RecyclerView.ViewHolder {
        ItemChatusersBinding binding;

        public ChatViewholder(@NonNull View itemView) {
            super(itemView);
            binding = ItemChatusersBinding.bind(itemView);
        }
    }

}
