package com.hi.live.activity;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.databinding.DataBindingUtil;

import com.hi.live.R;
import com.hi.live.databinding.ActivityNewMobileLoginGaBinding;

public class NewMobileLoginActivityG_a extends AppCompatActivity {

    ActivityNewMobileLoginGaBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        //setContentView(R.layout.activity_new_mobile_login_ga);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_new_mobile_login_ga);



        binding.loginbtn.setOnClickListener(view -> {
            startActivity(new Intent(NewMobileLoginActivityG_a.this, NewMainActivity.class));
        });

    }
}