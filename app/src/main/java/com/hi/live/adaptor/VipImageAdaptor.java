package com.hi.live.adaptor;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.hi.live.BuildConfig;
import com.hi.live.R;
import com.hi.live.SessionManager__a;
import com.hi.live.activity.WebActivity_a;
import com.hi.live.databinding.ItemImageBinding;
import com.hi.live.models.BannerRoot;

import java.util.List;

public class VipImageAdaptor  {

    SessionManager__a sessionManager;
    List<BannerRoot.BannerItem> data;
    private Context context;


    public VipImageAdaptor(List<BannerRoot.BannerItem> customAd) {
        this.data = customAd;
    }



    public SliderAdapterVH onCreateViewHolder(ViewGroup parent) {
        context = parent.getContext();

        View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_image, null);
        return new SliderAdapterVH(inflate);
    }


    public void onBindViewHolder(SliderAdapterVH viewHolder, final int position) {

        sessionManager = new SessionManager__a(context);

        Glide.with(context)
                .load(BuildConfig.BASE_URL + data.get(position).getImage())
                .transform(new CenterCrop(), new RoundedCorners(20))
                .into(viewHolder.binding.ivPhoto);

//        viewHolder.itemView.setOnClickListener(v -> {
//            if (data.get(position).getLink() != null && !data.get(position).getLink().isEmpty())
//                context.startActivity(new Intent(context, WebActivity_a.class).putExtra("website", data.get(position).getLink()).putExtra("title", R.string.app_name));
//        });

    }



    public int getCount() {
        return data.size();
    }

    class SliderAdapterVH   {
        ItemImageBinding binding;

        public SliderAdapterVH(View itemView) {
            super();
            binding = ItemImageBinding.bind(itemView);

        }
    }

}


