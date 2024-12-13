package com.hi.live.adaptor;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.hi.live.R;
import com.hi.live.SessionManager__a;
import com.hi.live.databinding.ItemCommentBinding;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class CommentAdapterOriginal_a extends RecyclerView.Adapter<CommentAdapterOriginal_a.CommentViewHolder> {
    Random random = new Random();
    Context context;
    private List<JSONObject> data = new ArrayList<>();
    private SessionManager__a sessonManager;


    public CommentAdapterOriginal_a() {
//ll

    }

    @NonNull
    @Override
    public CommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        sessonManager = new SessionManager__a(context);
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_comment, parent, false);
        return new CommentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentViewHolder holder, int position) {

        JSONObject jsonObject = data.get(position);
        String name = null;
        try {
            name = jsonObject.get("name").toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (name != null && !name.equals("")) {
            holder.binding.tvName.setText(name);
        }
        String message = null;
        try {
            message = jsonObject.get("comment").toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (message != null && !message.equals("")) {
            holder.binding.tvcomment.setText(message);
        }
        try {
            if (jsonObject.get("name").toString().equals("User") || jsonObject.get("name").toString().equals(sessonManager.getUser().getName())) {
                holder.binding.tvName.setTextColor(ContextCompat.getColor(context, R.color.yellow));
            } else {
                holder.binding.tvName.setTextColor(ContextCompat.getColor(context, R.color.white));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


    @Override
    public int getItemCount() {
        return data.size();
    }


    public void addComment(JSONObject toString) {
        data.add(toString);
        notifyItemInserted(data.size() - 1);
    }

    public class CommentViewHolder extends RecyclerView.ViewHolder {
        ItemCommentBinding binding;

        public CommentViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = ItemCommentBinding.bind(itemView);
        }
    }
}
