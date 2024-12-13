package com.hi.live.adaptor;

import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.PagerAdapter;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.hi.live.R;
import com.hi.live.models.EmojiIconRoot;
import com.hi.live.models.EmojicategoryRoot;
import com.hi.live.retrofit.Const_a;
import com.hi.live.retrofit.RetrofitBuilder_a;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BottomViewPagerAdapter_a extends PagerAdapter {
    private static final String TAG = "BottomViewPagerAdapter_a";
    EmojiListnerViewPager emojiListnerViewPager;
    private List<EmojicategoryRoot.Datum> categories = new ArrayList<>();

    public BottomViewPagerAdapter_a(List<EmojicategoryRoot.Datum> categories) {

        this.categories = categories;
    }

    public EmojiListnerViewPager getEmojiListnerViewPager() {
        return emojiListnerViewPager;
    }

    public void setEmojiListnerViewPager(EmojiListnerViewPager emojiListnerViewPager) {
        this.emojiListnerViewPager = emojiListnerViewPager;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        View view = LayoutInflater.from(container.getContext()).inflate(R.layout.item_emojiviewpager, container, false);
        Log.d("TAG", "instantiateItem: " + categories.get(position).getName());
        RecyclerView recyclerView = view.findViewById(R.id.rvEmojiSheet);
        ShimmerFrameLayout animationView = view.findViewById(R.id.shimmer);
        animationView.setVisibility(View.VISIBLE);
        Call<EmojiIconRoot> call = RetrofitBuilder_a.create().getEmojiByCategory(Const_a.DEVKEY, categories.get(position).get_id());
        call.enqueue(new Callback<EmojiIconRoot>() {
            @Override
            public void onResponse(Call<EmojiIconRoot> call, Response<EmojiIconRoot> response) {
                if (response.code() == 200 && response.body().getStatus() && !response.body().getData().isEmpty()) {
                    Log.d(TAG, "onResponse: getStatus === " + response.body().getStatus());
                    EmojiAdapter_a emojiAdapter = new EmojiAdapter_a(response.body().getData());
                    recyclerView.setAdapter(emojiAdapter);
                    animationView.setVisibility(View.GONE);
                    emojiAdapter.setOnEmojiClickListnear((bitmap, coin, emoji) -> {
                        Log.e("TAG", "onResponse: click >>>>>>>>>>  bottomgift  ");
                        emojiListnerViewPager.emojilistnerViewpager(bitmap, coin, emoji);
                    });
                } else if (response.body().getData().isEmpty()) {
                    TextView textView = view.findViewById(R.id.noDataFound);
                    animationView.setVisibility(View.GONE);
                    textView.setVisibility(View.VISIBLE);
                } else {
                    animationView.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(Call<EmojiIconRoot> call, Throwable t) {
                Log.d("TAG", "onFailure: emoji adapter" + t.getMessage());
//ll
            }
        });
        container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((RelativeLayout) object);
    }

    @Override
    public int getCount() {
        return categories.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    public interface EmojiListnerViewPager {
        void emojilistnerViewpager(Bitmap bitmap, Long coin, EmojiIconRoot.Datum emoji);
    }
}
