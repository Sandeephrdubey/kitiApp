package com.hi.live.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.databinding.DataBindingUtil;

import com.hi.live.R;
import com.hi.live.SessionManager__a;
import com.hi.live.databinding.ActivityWebBinding;

public class WebActivity_a extends BaseActivity_a {

    ActivityWebBinding binding;
    String website;
    String title;
    private boolean loadingFinished;
    private boolean redirect;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_web);
        binding.pd.setVisibility(View.VISIBLE);
        Intent intent = getIntent();
        if (intent != null) {
            website = intent.getStringExtra("website");
            title = intent.getStringExtra("title");
            binding.tvtitle.setText(title);
            loadUrl(website);
        }

        SessionManager__a sessionManager = new SessionManager__a(this);

        binding.imgback.setOnClickListener(view -> finish());

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
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

    public void onClickAgree(View view) {
        startActivity(new Intent(this, LoginActivityG_a.class));
    }

    public void onClickCencel(View view) {
        finishAffinity();
    }
}