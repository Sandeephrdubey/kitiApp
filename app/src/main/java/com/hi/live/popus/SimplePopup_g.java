package com.hi.live.popus;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;

import androidx.databinding.DataBindingUtil;

import com.hi.live.R;
import com.hi.live.databinding.ItemSimplepopupBinding;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;

/**
 * Created by  on 22-May-21.
 */
public class SimplePopup_g {

    private final Dialog dialog;

    public SimplePopup_g(Context context) {
        dialog = new Dialog(context, R.style.customStyle);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(LAYOUT_INFLATER_SERVICE);
        ItemSimplepopupBinding popupbinding = DataBindingUtil.inflate(inflater, R.layout.item_simplepopup, null, false);

        dialog.setContentView(popupbinding.getRoot());
        dialog.show();
        popupbinding.btncountinue.setOnClickListener(v -> dialog.dismiss());

    }
}
