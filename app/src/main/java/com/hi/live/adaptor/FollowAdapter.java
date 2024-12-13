package com.hi.live.fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.hi.live.R;

import java.util.List;

public class FollowAdapter extends RecyclerView.Adapter<FollowAdapter.FollowViewHolder> {

    private final List<String> userList;
    private final List<Integer> imageList;

    public FollowAdapter(List<String> userList, List<Integer> imageList) {
        this.userList = userList;
        this.imageList = imageList;
    }

    @NonNull
    @Override
    public FollowViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_follow, parent, false);
        return new FollowViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FollowViewHolder holder, int position) {
        // Set user name
        holder.userName.setText(userList.get(position));

        // Set corresponding image
        holder.userImage.setImageResource(imageList.get(position));
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    static class FollowViewHolder extends RecyclerView.ViewHolder {
        ImageView userImage, userProfileIcon;
        TextView userName;

        public FollowViewHolder(@NonNull View itemView) {
            super(itemView);
            userImage = itemView.findViewById(R.id.userImage);
            userProfileIcon = itemView.findViewById(R.id.userProfileIcon);
            userName = itemView.findViewById(R.id.userName);
        }
    }
}
