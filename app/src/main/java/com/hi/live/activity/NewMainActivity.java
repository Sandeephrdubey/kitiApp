package com.hi.live.activity;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.hi.live.R;
import com.hi.live.adaptor.ScreenNewSlidePagerAdapter;
import com.hi.live.adaptor.ScreenSlidePagerAdapter;
import com.hi.live.databinding.ActivityNewMainBinding;

public class NewMainActivity extends AppCompatActivity {
    private ScreenSlidePagerAdapter screenSlidePagerAdapter;

    ActivityNewMainBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        //setContentView(R.layout.activity_new_main);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_new_main);
        initView();
    }

    private void initView() {
        screenSlidePagerAdapter = new ScreenSlidePagerAdapter(NewMainActivity.this);
        binding.viewpagerMain.setAdapter(screenSlidePagerAdapter);
        binding.viewpagerMain.setUserInputEnabled(false);


        binding.bottomNavigationView.setSelectedItemId(R.id.navHome);
        binding.bottomNavigationView.setItemIconTintList(null);
        binding.bottomNavigationView.setBackgroundColor(getResources().getColor(R.color.themepurple));

        setUpFragment(2);
        binding.bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.navHome) {
                setUpFragment(0);
                return true;
            } else if (itemId == R.id.navMatch) {
                setUpFragment(1);
                return true;
            } else if (itemId == R.id.navHomee) {
                setUpFragment(2);
                return true;
            } else if (itemId == R.id.navMessage) {
                setUpFragment(3);
                return true;
            } else if (itemId == R.id.navProfile) {
                setUpFragment(4);
                return true;
            }
            return false;
        });
    }

    private void setUpFragment(int position) {
        binding.viewpagerMain.setCurrentItem(position);
    }


}