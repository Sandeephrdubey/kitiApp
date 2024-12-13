package com.hi.live.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.databinding.DataBindingUtil;

import com.google.gson.Gson;
import com.hi.live.R;
import com.hi.live.adaptor.LevelAdapter_a;
import com.hi.live.databinding.ActivityLevelListBinding;
import com.hi.live.models.LevelRoot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class LevelListActivityG_a extends BaseActivity_a {
    ActivityLevelListBinding binding;
    private List<LevelRoot.LevelsItem> list = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_level_list);


        Intent intent = getIntent();
        String dataStr = intent.getStringExtra("data");
        if (dataStr != null && !dataStr.isEmpty()) {
            LevelRoot data = new Gson().fromJson(dataStr, LevelRoot.class);
            if (data != null) {
                list = data.getLevels();
                Collections.reverse(list);
                binding.rvList.setAdapter(new LevelAdapter_a(list));
            }
        }
    }

    public void onClickBack(View view) {
        onBackPressed();
    }
}