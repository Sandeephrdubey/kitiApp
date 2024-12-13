package com.hi.live.bottomsheet;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.widget.FrameLayout;

import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.gson.JsonObject;
import com.hi.live.R;
import com.hi.live.SessionManager__a;
import com.hi.live.databinding.BottomSheetReportBinding;
import com.hi.live.models.RestResponse;
import com.hi.live.retrofit.Const_a;
import com.hi.live.retrofit.RetrofitBuilder_a;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BottomSheetReport_g {

    private final BottomSheetDialog bottomSheetDialog;
    private boolean submitButtonEnable = false;

    public BottomSheetReport_g(Context context, String agencyId, String onotherId, OnReportedListner onReportedListner) {
        bottomSheetDialog = new BottomSheetDialog(context, R.style.CustomBottomSheetDialogTheme);
        bottomSheetDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
//        bottomSheetDialog.setOnShowListener(dialog -> {
//            BottomSheetDialog d = (BottomSheetDialog) dialog;
//            FrameLayout bottomSheet = (FrameLayout) d.findViewById(R.id.design_bottom_sheet);
//            BottomSheetBehavior.from(bottomSheet)
//                    .setState(BottomSheetBehavior.STATE_EXPANDED);
//        });


        BottomSheetReportBinding sheetDilogBinding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.bottom_sheet_report, null, false);
        bottomSheetDialog.setContentView(sheetDilogBinding.getRoot());
        bottomSheetDialog.show();

        sheetDilogBinding.btnclose.setOnClickListener(v -> bottomSheetDialog.dismiss());
        sheetDilogBinding.etDes.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//ll
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().equals("")) {
                    sheetDilogBinding.btnSubmit.setBackground(ContextCompat.getDrawable(context, R.drawable.bg_etblack_a));
                    sheetDilogBinding.btnSubmit.setTextColor(ContextCompat.getColor(context, R.color.black));
                    submitButtonEnable = false;
                } else {
                    sheetDilogBinding.btnSubmit.setBackground(ContextCompat.getDrawable(context, R.drawable.bg_greadentround_a));
                    sheetDilogBinding.btnSubmit.setTextColor(ContextCompat.getColor(context, R.color.white));
                    submitButtonEnable = true;

                }
            }

            @Override
            public void afterTextChanged(Editable s) {
//ll
            }
        });
        sheetDilogBinding.btnSubmit.setOnClickListener(v -> {
            if (submitButtonEnable) {

                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("user_id", new SessionManager__a(context).getUser().getId());

                jsonObject.addProperty("host_id", onotherId);
                jsonObject.addProperty("agency_id", agencyId);
                jsonObject.addProperty("description", sheetDilogBinding.etDes.getText().toString().trim());
                Call<RestResponse> call = RetrofitBuilder_a.create().reportThisUser(Const_a.DEVKEY, jsonObject);
                call.enqueue(new Callback<RestResponse>() {
                    @Override
                    public void onResponse(Call<RestResponse> call, Response<RestResponse> response) {
                        if (response.code() == 200 && response.body().isStatus()) {

                            onReportedListner.onReported();
                            bottomSheetDialog.dismiss();
                        }
                    }

                    @Override
                    public void onFailure(Call<RestResponse> call, Throwable t) {
//ll
                    }
                });
            }
        });

    }

    public interface OnReportedListner {
        void onReported();
    }
}
