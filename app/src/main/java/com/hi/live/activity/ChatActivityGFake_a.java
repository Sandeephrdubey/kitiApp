package com.hi.live.activity;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.hi.live.R;
import com.hi.live.adaptor.ChatAdapterFake_a;
import com.hi.live.databinding.ActivityChatFakeBinding;
import com.hi.live.oflineModels.ChatRootFake;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ChatActivityGFake_a extends BaseActivity_a {
    ActivityChatFakeBinding binding;
    List<ChatRootFake> list = new ArrayList<>();
    ChatAdapterFake_a chatAdapterFakeG = new ChatAdapterFake_a();
    private String profileImage;
    private String name;
    private String lastChat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_chat_fake);

        Intent intent = getIntent();
        MainActivityG_a.isHostLive = false;

        profileImage = intent.getStringExtra("image");
        Log.d("TAG", "onCreate: image " + profileImage);
        name = intent.getStringExtra("name");
        lastChat = intent.getStringExtra("lastchat");
        if (name != null && !name.equals("")) {
            binding.tvName.setText(name);
            list = ChatRootFake.setFakeChates();

            chatAdapterFakeG.addSingleMessage(new ChatRootFake(2, lastChat));
            binding.rvchats.setAdapter(chatAdapterFakeG);
            initListner();

        }


        Glide.with(this).load(profileImage).circleCrop().addListener(new RequestListener<Drawable>() {
            @Override
            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                return true;
            }

            @Override
            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                Log.d("TAG", "onLoadFailed: ");
                binding.imgProfile.setBackground(ContextCompat.getDrawable(ChatActivityGFake_a.this, R.drawable.bg_whitebtnround_a));
                binding.imgProfile.setImageDrawable(resource);
                return true;
            }
        }).into(binding.imgProfile);
    }

    private void initListner() {
        binding.btnsend.setOnClickListener(v -> {
            String msg = binding.etChat.getText().toString();
            if (msg.equals("")) {
                Toast.makeText(this, "Type message first", Toast.LENGTH_SHORT).show();
                return;
            }
            binding.etChat.setText("");
            chatAdapterFakeG.addSingleMessage(new ChatRootFake(1, msg));
            binding.btnsend.setEnabled(false);
            new Handler().postDelayed(() -> {
                Collections.shuffle(list);
                chatAdapterFakeG.addSingleMessage(list.get(0));
                binding.btnsend.setEnabled(true);
            }, 1500);
        });
    }

    public void onClickBack(View view) {
        onBackPressed();
    }

    public void onclicProfile(View view) {

    }
}