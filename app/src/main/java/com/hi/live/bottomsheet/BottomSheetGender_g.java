package com.hi.live.bottomsheet;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.hi.live.R;
import com.hi.live.activity.LoginActivityG_a;
import com.hi.live.databinding.BottomSheetGenderBinding;

public class BottomSheetGender_g {

    private final BottomSheetDialog bottomSheetDialog;
    private String selected = "";
    private boolean submitButtonEnable = false;

    public BottomSheetGender_g(LoginActivityG_a.LoginType loginType, Context context, OnGenderSelectListner onReportedListner) {
        bottomSheetDialog = new BottomSheetDialog(context, R.style.CustomBottomSheetDialogTheme);
        bottomSheetDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
//        bottomSheetDialog.setOnShowListener(dialog -> {
//            BottomSheetDialog d = (BottomSheetDialog) dialog;
//            FrameLayout bottomSheet = (FrameLayout) d.findViewById(R.id.design_bottom_sheet);
//            BottomSheetBehavior.from(bottomSheet)
//                    .setState(BottomSheetBehavior.STATE_EXPANDED);
//        });


        BottomSheetGenderBinding sheetDilogBinding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.bottom_sheet_gender, null, false);
        bottomSheetDialog.setContentView(sheetDilogBinding.getRoot());
        bottomSheetDialog.show();
        sheetDilogBinding.male.setBackground(ContextCompat.getDrawable(context, R.drawable.bg_grayring));
        sheetDilogBinding.female.setBackground(ContextCompat.getDrawable(context, R.drawable.bg_grayring));
        sheetDilogBinding.male.setOnClickListener(v -> {
            selected = "MALE";
            sheetDilogBinding.male.setBackground(ContextCompat.getDrawable(context, R.drawable.bg_greadentring));
            sheetDilogBinding.female.setBackground(ContextCompat.getDrawable(context, R.drawable.bg_grayring));
        });
        sheetDilogBinding.female.setOnClickListener(v -> {
            selected = "FEMALE";
            sheetDilogBinding.female.setBackground(ContextCompat.getDrawable(context, R.drawable.bg_greadentring));
            sheetDilogBinding.male.setBackground(ContextCompat.getDrawable(context, R.drawable.bg_grayring));
        });
        if (loginType == LoginActivityG_a.LoginType.quick) {
            sheetDilogBinding.lytname.setVisibility(View.VISIBLE);
        } else {
            sheetDilogBinding.lytname.setVisibility(View.GONE);
        }

        sheetDilogBinding.btnSubmit.setOnClickListener(v -> {
            String name = sheetDilogBinding.etName.getText().toString();
            if (loginType == LoginActivityG_a.LoginType.quick) {
                if (name.isEmpty()) {
                    Toast.makeText(context, "Enter Name First", Toast.LENGTH_SHORT).show();
                    return;
                }
            }
            if (selected.isEmpty()) {
                Toast.makeText(context, "Select Gender First", Toast.LENGTH_SHORT).show();
                return;
            }
            onReportedListner.onSelect(selected, name);
            bottomSheetDialog.dismiss();
        });

    }

    public interface OnGenderSelectListner {
        void onSelect(String g, String name);
        // void onCancel();
    }
}
