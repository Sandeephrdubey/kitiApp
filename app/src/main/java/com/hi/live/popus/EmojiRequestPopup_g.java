package com.hi.live.popus;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;

import androidx.databinding.DataBindingUtil;

import com.bumptech.glide.Glide;
import com.hi.live.BuildConfig;
import com.hi.live.R;
import com.hi.live.databinding.ItemEmojirequestpopupBinding;
import com.hi.live.models.EmojiIconRoot;

/**
 * Created by on 22-May-21.
 */
public class EmojiRequestPopup_g {

    private final Dialog dialog;
    ItemEmojirequestpopupBinding popupbinding;
    private Context context;

    public EmojiRequestPopup_g(Context context) {
        dialog = new Dialog(context, R.style.customStyle);
        this.context = context;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(LAYOUT_INFLATER_SERVICE);
        popupbinding = DataBindingUtil.inflate(inflater, R.layout.item_emojirequestpopup, null, false);

        dialog.setContentView(popupbinding.getRoot());


    }

    public void openPopup(OnGiftRequestListner onGiftRequestListner, EmojiIconRoot.Datum requestedGift, String clientName) {
        if (context != null) {
            Glide.with(context.getApplicationContext()).load(BuildConfig.BASE_URL + requestedGift.getIcon()).centerCrop().into(popupbinding.imgGift);

            String text = clientName + " is requested to you send this gift";
            popupbinding.tvTitle.setText(text);
            popupbinding.btnSend.setOnClickListener(v -> {
                onGiftRequestListner.onSendClick(requestedGift);
                dialog.dismiss();
            });
            popupbinding.btnCancel.setOnClickListener(v -> dialog.dismiss());
            if (dialog != null) {
                if (!((Activity) context).isFinishing()) {
                    dialog.show();
                }
            }
        }


    }

    public void dismiss() {
        if (dialog != null) {
            dialog.dismiss();
        }
    }

    public interface OnGiftRequestListner {
        void onSendClick(EmojiIconRoot.Datum requestedGift);
    }
}
