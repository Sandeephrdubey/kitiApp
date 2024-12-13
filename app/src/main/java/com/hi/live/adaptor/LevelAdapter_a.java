package com.hi.live.adaptor;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.hi.live.R;
import com.hi.live.databinding.ItemLevelBinding;
import com.hi.live.models.LevelRoot;
import com.hi.live.oflineModels.Filters.FilterRoot;

import java.util.List;

public class LevelAdapter_a extends RecyclerView.Adapter<LevelAdapter_a.LevelViewHolder> {

    private Context context;

    private List<LevelRoot.LevelsItem> levels;

    public LevelAdapter_a(List<LevelRoot.LevelsItem> levels) {

        this.levels = levels;
    }

    @NonNull
    @Override
    public LevelViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        return new LevelViewHolder(LayoutInflater.from(context).inflate(R.layout.item_level, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull LevelViewHolder holder, int position) {

        holder.binding.tvCoins.setText(String.valueOf(levels.get(position).getRupee()));
        holder.binding.tvLevel.setText(String.valueOf(levels.get(position).getName()));
    }

    @Override
    public int getItemCount() {
        return levels.size();
    }

    public interface OnFilterClickListnear {
        void onFilterClick(FilterRoot filterRoot);
    }

    public class LevelViewHolder extends RecyclerView.ViewHolder {
        ItemLevelBinding binding;

        public LevelViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = ItemLevelBinding.bind(itemView);
        }
    }
}
