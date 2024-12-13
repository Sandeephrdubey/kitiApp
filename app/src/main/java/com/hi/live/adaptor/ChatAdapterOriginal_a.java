package com.hi.live.adaptor;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.hi.live.R;
import com.hi.live.databinding.ItemChatBinding;
import com.hi.live.models.OriginalMessageRoot;

import java.util.ArrayList;
import java.util.List;

public class ChatAdapterOriginal_a extends RecyclerView.Adapter<ChatAdapterOriginal_a.ChatViewholder> {


    private static final String TAG = "ChatAdapterOriginal_a";
    private List<OriginalMessageRoot.DataItem> list = new ArrayList<>();

    @NonNull
    @Override
    public ChatAdapterOriginal_a.ChatViewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ChatViewholder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ChatViewholder holder, int position) {


        OriginalMessageRoot.DataItem messageRoot = list.get(position);
        holder.binding.tvrobot.setVisibility(View.GONE);
        holder.binding.tvuser.setVisibility(View.GONE);
        holder.binding.lytimagerobot.setVisibility(View.GONE);
        holder.binding.lytimageuser.setVisibility(View.GONE);
        Log.d("TAG", "onBindViewHolder: sender " + messageRoot.getSender());
        if (messageRoot.getSender().equals("user")) {
            if (!messageRoot.getMessage().equals("")) {
                holder.binding.tvuser.setText(messageRoot.getMessage());
                holder.binding.tvuser.setVisibility(View.VISIBLE);
            }


        } else {
            if (!messageRoot.getMessage().equals("")) {
                holder.binding.tvrobot.setText(messageRoot.getMessage());
                holder.binding.tvrobot.setVisibility(View.VISIBLE);
            }
        }
    }


    @Override
    public int getItemCount() {
        return list.size();
    }

    public void clear() {
        list.clear();
        notifyDataSetChanged();
    }

    public void addData(List<OriginalMessageRoot.DataItem> data) {
        for (int i = 0; i < data.size(); i++) {
            list.add(data.get(i));
            notifyItemInserted(list.size() - 1);
        }
    }

    public void addSingleMessage(OriginalMessageRoot.DataItem dataItem) {
        list.add(0, dataItem);
        notifyItemInserted(0);
    }


    public class ChatViewholder extends RecyclerView.ViewHolder {
        ItemChatBinding binding;

        public ChatViewholder(@NonNull View itemView) {
            super(itemView);
            binding = ItemChatBinding.bind(itemView);
        }
    }
}
