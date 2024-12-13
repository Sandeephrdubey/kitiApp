package com.hi.live.adaptor;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.facebook.ads.NativeAd;
import com.google.android.gms.ads.formats.UnifiedNativeAd;
import com.google.gson.Gson;
import com.hi.live.R;
import com.hi.live.SessionManager__a;
import com.hi.live.activity.FakeActivity_a;
import com.hi.live.activity.callwork.FakeCallActivity_a;
import com.hi.live.ads.AdViewHolderFacebook_a;
import com.hi.live.ads.UnifiedNativeAdViewHolder;
import com.hi.live.databinding.ItemVideoBinding;
import com.hi.live.models.Thumb;

import java.util.ArrayList;
import java.util.List;

public class AdapterHosts_a extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int VIEWTYPE = 2;
    private static final int AD_TYPE = 1;
    private static final int AD_FB_TYPE = 3;
    private static final String TAG = "AdapterHosts_a";
    int adPosition = 2;
    int adSetPos = 0;
    List<Object> data = new ArrayList<>();
    String cid;
    private Context context;
    private SessionManager__a sessionManager;
    private OnHostThumbClickListner onHostThumbClickListner;

    @Override
    public int getItemViewType(int position) {
        if (data.get(position) instanceof UnifiedNativeAd) {
            return AD_TYPE;
        } else if (data.get(position) instanceof NativeAd) {
            Log.d(TAG, "getItemViewType: fb rtrt");

            return AD_FB_TYPE;
        } else {
            return VIEWTYPE;
        }


    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        sessionManager = new SessionManager__a(context);
//        if (viewType == AD_TYPE) {
//            Log.d("TAG", "onCreateViewHolder: google  type");
//            View unifiedNativeLayoutView = LayoutInflater.from(
//                    parent.getContext()).inflate(R.layout.item_google_ad,
//                    parent, false);
//            return new UnifiedNativeAdViewHolder(unifiedNativeLayoutView);
//
//        } else if (viewType == AD_FB_TYPE) {
//            Log.d("TAG", "onCreateViewHolder: fb type");
//            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_nativead, parent, false);
//            return new AdViewHolderFacebook_a(view);
//
//
//        } else {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_video, parent, false);
        return new VideoViewHolder(view);
//        }


    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof UnifiedNativeAdViewHolder) {
            UnifiedNativeAdViewHolder viewHolder = (UnifiedNativeAdViewHolder) holder;
            com.google.android.gms.ads.nativead.NativeAd nativeAd = (com.google.android.gms.ads.nativead.NativeAd) data.get(position);
            viewHolder.populateNativeAdView(nativeAd, ((UnifiedNativeAdViewHolder) holder).getAdView());
        } else if (holder instanceof AdViewHolderFacebook_a) {
            Log.d(TAG, "onBindViewHolder: fb viewholder");
            AdViewHolderFacebook_a adViewHolder = (AdViewHolderFacebook_a) holder;
            NativeAd ad = (NativeAd) data.get(position);
            adViewHolder.setData(ad);
        } else {
            VideoViewHolder videoViewHolder = (VideoViewHolder) holder;
            videoViewHolder.setData(position);
        }


    }


    @Override
    public int getItemCount() {
        return data.size();
    }

    public void addData(List<Thumb> data) {
        Log.d(TAG, "addData: " + data.size());
        this.data.addAll(data);
        notifyItemRangeInserted(this.data.size(), data.size());
    }

    public void clearAll() {
        this.data.clear();
        notifyDataSetChanged();
    }

    public List<Object> getList() {
        return data;
    }


    public OnHostThumbClickListner getOnHostThumbClickListner() {
        return onHostThumbClickListner;
    }

    public void setOnHostThumbClickListner(OnHostThumbClickListner onHostThumbClickListner) {
        this.onHostThumbClickListner = onHostThumbClickListner;
    }

    public interface OnHostThumbClickListner {
        void onClick(Thumb thumb, ItemVideoBinding binding);
    }

    public class VideoViewHolder extends RecyclerView.ViewHolder {
        ItemVideoBinding binding;

        public VideoViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = ItemVideoBinding.bind(itemView);
        }

        public void setData(int position) {
            if (data != null) {
                Thumb girl = (Thumb) data.get(position);
                binding.tvCountry.setText(girl.getCountryName());
                binding.tvName.setText(girl.getName());
                binding.tvCountry.setText(girl.getCountryName());


                Log.d("TAG", "setData: host iss busy " + girl.isIsBusy());
                Log.d("TAG", "setData: host iss busy " + girl.isBusy());

                binding.viewLayout.setVisibility(View.GONE);
                if (girl.getImage() != null && !girl.getImage().equals("")) {

                    String img;
                    img = girl.getImage();

                    Glide.with(context).load(img).centerCrop().addListener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                            binding.tvchar.setVisibility(View.VISIBLE);
                            binding.tvchar.setText(String.valueOf(girl.getName().charAt(0)).toUpperCase());

                            return true;
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                            binding.imgThumb.setImageDrawable(resource);
                            return true;
                        }
                    }).into(binding.imgThumb);
                } else {
                    binding.tvchar.setVisibility(View.VISIBLE);
                    binding.tvchar.setText(String.valueOf(girl.getName().charAt(0)).toUpperCase());

                }

                String status;
                if (girl.isIsBusy()) {
                    status = "Busy";
                    binding.onlineStatusImage.setBackgroundResource(R.drawable.bg_live_busy);
                } else if (girl.isOnline()) {
                    status = "Online";
                    binding.onlineStatusImage.setBackgroundResource(R.drawable.bg_live);
                } else {
                    status = "Offline";
                    binding.onlineStatusImage.setBackgroundResource(R.drawable.bg_live_offline);
                }
                binding.tvOnlineStatus.setText(status);
                if (girl.isFake()) {
                    Log.d(TAG, "setData111: 1111");

                    binding.imgThumb.setOnClickListener(v -> context.startActivity(new Intent(context, FakeCallActivity_a.class).putExtra("model", new Gson().toJson(girl))));

//                    binding.imgThumb.setOnClickListener(v -> onHostThumbClickListner.onClick(girl, binding));
                } else {
                    Log.d(TAG, "setData111: 2222");
                    binding.imgThumb.setOnClickListener(v -> {
                        if (context != null) {
                            if (sessionManager.getUser().getCoin() < sessionManager.getSetting().getUserCallCharge()) {
                                Toast.makeText(context, "Please recharge your wallet", Toast.LENGTH_SHORT).show();
//                                new Handler().postDelayed(() -> MyWalletActivity_a.openMe(context), 1500);
                            } else if (!girl.isOnline()) {
                                Toast.makeText(context, girl.getName() + " is Offline now.", Toast.LENGTH_SHORT).show();
                            } else if (girl.isBusy()) {
                                Toast.makeText(context, girl.getName() + " is Busy now.", Toast.LENGTH_SHORT).show();
                            } else {
                                onHostThumbClickListner.onClick(girl, binding);
                            }
                        }
                    });
                }
            }

        }
    }

}
