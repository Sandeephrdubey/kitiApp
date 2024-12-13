package com.hi.live.popus;

import android.app.Dialog;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;

import androidx.databinding.DataBindingUtil;

import com.bumptech.glide.Glide;
import com.hi.live.R;
import com.hi.live.SessionManager__a;
import com.hi.live.databinding.ItemRatepopupBinding;
import com.hi.live.models.User;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;

public class ChangeRatePopup_g {


    private final Dialog dialog;
    OnSubmitClickListnear onSubmitClickListnear;
    SessionManager__a sessionManager;

    public ChangeRatePopup_g(Context context, User user) {
        sessionManager = new SessionManager__a(context);
        dialog = new Dialog(context, R.style.customStyle);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(LAYOUT_INFLATER_SERVICE);
        ItemRatepopupBinding popupbinding = DataBindingUtil.inflate(inflater, R.layout.item_ratepopup, null, false);

        dialog.setContentView(popupbinding.getRoot());

        Glide.with(context)
                .load(user.getImage()).error(R.drawable.bg_whitebtnround_a)
                .placeholder(R.drawable.bg_whitebtnround_a)
                .circleCrop()
                .into(popupbinding.imagepopup);

        popupbinding.tvName.setText(user.getName());
        popupbinding.tvusername.setText("@" + user.getUsername());
        popupbinding.tvdes.setText("Hello dear " + user.getName() + " you can change your rate/min here");


        popupbinding.btnSubmit.setOnClickListener(v -> {
            Log.d("TAG", "ChangeRatePopup: submit");

            if (popupbinding.etRate.getText().toString().length() > 6) {
                popupbinding.etRate.setError("Your Rate is Very High");
                return;
            }
            if (popupbinding.etRate.getText().toString().equals("")) {
                popupbinding.etRate.setError("Required!");

            } else {
                if (Integer.parseInt(popupbinding.etRate.getText().toString()) < sessionManager.getSetting().getStreamingMinValue()) {
                    popupbinding.etRate.setError("Minimum rate is " + sessionManager.getSetting().getStreamingMinValue());
                } else if (Integer.parseInt(popupbinding.etRate.getText().toString()) >= sessionManager.getSetting().getStreamingMaxValue()) {
                    popupbinding.etRate.setError("Maximum rate is " + sessionManager.getSetting().getStreamingMaxValue());
                } else {
                    onSubmitClickListnear.onSubmit(popupbinding.etRate.getText().toString());
                    dialog.dismiss();
                }
            }
        });
        dialog.show();
        popupbinding.tvCencel.setOnClickListener(v -> dialog.dismiss());

    }

    public OnSubmitClickListnear getOnSubmitClickListnear() {
        return onSubmitClickListnear;
    }

    public void setOnSubmitClickListnear(OnSubmitClickListnear onSubmitClickListnear) {
        this.onSubmitClickListnear = onSubmitClickListnear;
    }

    public void close() {
        if (dialog != null) {
            dialog.dismiss();
        }
    }

    public interface OnSubmitClickListnear {
        void onSubmit(String rate);
    }
}
