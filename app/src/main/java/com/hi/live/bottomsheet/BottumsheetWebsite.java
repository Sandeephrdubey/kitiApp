package com.hi.live.bottomsheet;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.databinding.DataBindingUtil;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.hi.live.R;
import com.hi.live.databinding.WebsiteSheetBinding;

public class BottumsheetWebsite {

    Context context;
    BottomSheetDialog bottomSheetDialog;
    WebsiteSheetBinding binding;

    private boolean loadingFinished;
    private boolean redirect;

    public BottumsheetWebsite(Context context) {
        this.context = context;
        this.bottomSheetDialog = new BottomSheetDialog(context);
    }

    public void showWebSheet(String website) {

        binding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.website_sheet, null, false);
        bottomSheetDialog.setContentView(binding.getRoot());

        binding.imgback.setOnClickListener(view -> closeBottomSheet());

        binding.tvtitle.setText("Privacy Policy");

        loadUrl(website);


        bottomSheetDialog.show();


    }

    private void loadUrl(String url) {
        if (url != null) {
            binding.webview.loadUrl(url);

            binding.webview.setWebViewClient(new WebViewClient() {

                @Override
                public boolean shouldOverrideUrlLoading(WebView view, String urlNewString) {
                    if (!loadingFinished) {
                        redirect = true;
                    }

                    loadingFinished = false;
                    view.loadUrl(urlNewString);
                    return true;
                }

                @Override
                public void onPageStarted(WebView view, String url, Bitmap facIcon) {
                    loadingFinished = false;
                    //SHOW LOADING IF IT ISNT ALREADY VISIBLE
                    binding.pd.setVisibility(View.VISIBLE);
                }

                @Override
                public void onPageFinished(WebView view, String url) {
                    if (!redirect) {
                        loadingFinished = true;
                        binding.pd.setVisibility(View.GONE);
                    }

                    if (loadingFinished && !redirect) {
                        //HIDE LOADING IT HAS FINISHED
                        binding.pd.setVisibility(View.GONE);
                    } else {
                        redirect = false;
                        binding.pd.setVisibility(View.GONE);
                    }

                }
            });

        }

    }


    private void closeBottomSheet() {
        bottomSheetDialog.dismiss();
    }


}
