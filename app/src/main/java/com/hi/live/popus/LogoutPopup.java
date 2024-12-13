package com.hi.live.popus;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;

import androidx.databinding.DataBindingUtil;

import com.hi.live.R;
import com.hi.live.databinding.PopupLogoutBinding;

public class LogoutPopup {

    private final Dialog dialog;

    public LogoutPopup(Context context, onLogoutPopupListener onLogoutPopupListener) {
        dialog = new Dialog(context, R.style.customStyle);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        PopupLogoutBinding binding = DataBindingUtil.inflate(inflater, R.layout.popup_logout, null, false);

        dialog.setContentView(binding.getRoot());
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        binding.btncountinue.setOnClickListener(view -> {
            onLogoutPopupListener.onLogoutClick();
            dialog.dismiss();
        });

        binding.btncancel.setOnClickListener(view -> {
            dialog.dismiss();
        });

        dialog.show();

    }

    public interface onLogoutPopupListener {
        void onLogoutClick();
    }
}
