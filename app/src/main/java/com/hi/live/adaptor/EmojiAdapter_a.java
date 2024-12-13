package com.hi.live.adaptor;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.hi.live.BuildConfig;
import com.hi.live.R;
import com.hi.live.databinding.ItemEmojiBinding;
import com.hi.live.models.EmojiIconRoot;

import java.util.List;

public class EmojiAdapter_a extends RecyclerView.Adapter<EmojiAdapter_a.EmojiViewHolder> {

    private static final String TAG = "EmojiAdapter_a";
    OnEmojiClickListnear onEmojiClickListnear;
    private List<EmojiIconRoot.Datum> data;
    private Context contect;

    public EmojiAdapter_a(List<EmojiIconRoot.Datum> data) {

        this.data = data;
    }

    public OnEmojiClickListnear getOnEmojiClickListnear() {
        return onEmojiClickListnear;
    }

    public void setOnEmojiClickListnear(OnEmojiClickListnear onEmojiClickListnear) {
        this.onEmojiClickListnear = onEmojiClickListnear;
    }

    @Override
    public void onBindViewHolder(@NonNull EmojiViewHolder holder, int position) {
        EmojiIconRoot.Datum emoji = data.get(position);
        holder.binding.tvCoin.setText(String.valueOf(emoji.getCoin()));
        Log.d(TAG, "onBindViewHolder: emoji.getIcon() ========   "+BuildConfig.BASE_URL + emoji.getIcon());
        Glide.with(contect.getApplicationContext())
                .load(BuildConfig.BASE_URL + emoji.getIcon())
                .placeholder(R.drawable.ic_gift)
                .into(holder.binding.imgEmoji);
        holder.binding.imgEmoji.setOnClickListener(v -> {
            Log.e("TAG", "onBindViewHolder:   gift click"  +emoji );
            View content = holder.binding.imgEmoji;
            content.setDrawingCacheEnabled(true);
            Bitmap bitmap = content.getDrawingCache();
            onEmojiClickListnear.onEmojiClick(bitmap, emoji.getCoin(), emoji);
        });
    }

    @NonNull
    @Override
    public EmojiViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        contect = parent.getContext();
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_emoji, parent, false);
        return new EmojiViewHolder(view);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public interface OnEmojiClickListnear {
        void onEmojiClick(Bitmap bitmap, Long coin, EmojiIconRoot.Datum emojiurl);
    }

    public class EmojiViewHolder extends RecyclerView.ViewHolder {
        ItemEmojiBinding binding;

        public EmojiViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = ItemEmojiBinding.bind(itemView);
        }
    }
}
