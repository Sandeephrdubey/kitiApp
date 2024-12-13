package com.hi.live.popus;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.widget.Toast;

import androidx.databinding.DataBindingUtil;

import com.hi.live.R;
import com.hi.live.SessionManager__a;
import com.hi.live.databinding.ItemPrivacypopupBinding;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;

public class PrivacyPopup_g {

    OnSubmitClickListnear onSubmitClickListnear;
    SessionManager__a sessionManager;
    Dialog dialog;
    private boolean loadingFinished;
    private boolean redirect;

    public PrivacyPopup_g(Context context, OnSubmitClickListnear onSubmitClickListnear) {
        sessionManager = new SessionManager__a(context);
        dialog = new Dialog(context, R.style.customStyle);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(LAYOUT_INFLATER_SERVICE);
        ItemPrivacypopupBinding popupbinding = DataBindingUtil.inflate(inflater, R.layout.item_privacypopup, null, false);
        dialog.setCancelable(false);
        dialog.setContentView(popupbinding.getRoot());


        popupbinding.tvCountinue.setOnClickListener(v -> {
            if (popupbinding.checkbox.isChecked()) {
                dialog.dismiss();
                onSubmitClickListnear.onAccept();
            } else {
                Toast.makeText(context, "Please Accept PrivacyPolicy", Toast.LENGTH_SHORT).show();
            }


        });
        popupbinding.tvCencel.setOnClickListener(v -> {
            dialog.dismiss();
            onSubmitClickListnear.onDeny();

        });

        dialog.show();

    }


    public interface OnSubmitClickListnear {
        void onAccept();

        void onDeny();

    }
}
