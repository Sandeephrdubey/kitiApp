package com.hi.live.activity;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.hi.live.R;
import com.hi.live.SessionManager__a;
import com.hi.live.activity.callwork.CallRequestActivity_a;
import com.hi.live.adaptor.ChatAdapterOriginal_a;
import com.hi.live.databinding.ActivityChatListOriginalBinding;
import com.hi.live.models.CallCreateRoot;
import com.hi.live.models.ChatTopicRoot;
import com.hi.live.models.OriginalMessageRoot;
import com.hi.live.models.User;
import com.hi.live.retrofit.Const_a;
import com.hi.live.retrofit.RetrofitBuilder_a;
import com.hi.live.socket.CallHandler;
import com.hi.live.socket.ChatHandler;
import com.hi.live.socket.MySocketManager;
import com.hi.live.socket.SocketConst;

import org.json.JSONException;
import org.json.JSONObject;

import io.socket.client.Socket;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChatListActivityGOriginal_a extends BaseActivity_a {
    private static final String STR_USERID = "user_id";
    private static final String STR_TOPIC = "topic";
    private static final String STR_MSG = "message";
    private static final String TAG = "ChatListActivity";
    ActivityChatListOriginalBinding binding;
    private boolean isGoneForIntent = false;


    private boolean isLoadMore = false;
    ChatAdapterOriginal_a chatAdapterOriginal = new ChatAdapterOriginal_a();
    SessionManager__a sessionManager;
    String name;
    String profileImage;
    private int start = 0;
    private boolean isInsufficentBalance = false;
    private String secondUserId, userId, chatRoomId;
    private boolean isFake = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_chat_list_original);
        sessionManager = new SessionManager__a(this);
        MySocketManager.getInstance().addChatListener(chatHandler);
        MySocketManager.getInstance().addCallListener(callHandler);
        if (sessionManager.getBooleanValue(Const_a.ISLOGIN)) {
            userId = sessionManager.getUser().getId();
        }

        Intent intent = getIntent();
        MainActivityG_a.isHostLive = false;
        secondUserId = intent.getStringExtra("hostid");
        profileImage = intent.getStringExtra("image");
        chatRoomId = intent.getStringExtra("chatTopic");
        isFake = intent.getBooleanExtra("isFake", false);
        Log.d("TAG", "onCreate: image" + profileImage);
        name = intent.getStringExtra("name");
        if (name != null && !name.equals("")) {
            binding.tvName.setText(name);
        }
        initView();
        Glide.with(this).load(profileImage).circleCrop().addListener(new RequestListener<Drawable>() {
            @Override
            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                return true;
            }

            @Override
            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                Log.d("TAG", "onLoadFailed: ");
                binding.imgProfile.setBackground(ContextCompat.getDrawable(ChatListActivityGOriginal_a.this, R.drawable.bg_whitebtnround_a));
                binding.imgProfile.setImageDrawable(resource);
                return true;
            }
        }).into(binding.imgProfile);


        binding.btnCall.setOnClickListener(v -> {

            if (sessionManager.getSetting().getUserCallCharge() <= sessionManager.getUser().getCoin()) {
                customDialogClass.show();
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("user_id", sessionManager.getUser().getId());
                    jsonObject.put("host_id", secondUserId);
                    jsonObject.put("type", "user");
                    MySocketManager.getInstance().getSocet().emit(SocketConst.EVENT_MAKE_CALL, jsonObject);
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }

                MySocketManager.getInstance().getSocet().once(SocketConst.EVENT_MAKE_CALL, args -> {
                    if (args[0] != null) {
                        runOnUiThread(() -> {
                            if (!isGoneForIntent) {
                                isGoneForIntent = true;
                                CallCreateRoot.CallId call = new Gson().fromJson(args[0].toString(), CallCreateRoot.CallId.class);
                                startActivity(new Intent(ChatListActivityGOriginal_a.this, CallRequestActivity_a.class).putExtra(Const_a.IS_FROM_LIST, true).putExtra(Const_a.CALL_DATA, new Gson().toJson(call)));
                                Log.d("CALLLLLL", "onMakeCall: CHAT Actviity");
                                customDialogClass.dismiss();
                            }
                        });

                    }
                    if (args[1] != null) {
                        if (!isFinishing()) {
                            runOnUiThread(() -> {
                                customDialogClass.dismiss();
                                Toast.makeText(ChatListActivityGOriginal_a.this, name + " is not able receive call.", Toast.LENGTH_SHORT).show();
                            });
                        }
                    }
                });
            } else
                Toast.makeText(this, "You require " + sessionManager.getSetting().getUserCallCharge() + " coin to make call.", Toast.LENGTH_SHORT).show();
        });
        if (isFake) {
            binding.btnsend.setOnClickListener(v -> {
                if (isFake) {
                    Toast.makeText(this, "This is Dummy User this action can't perform", Toast.LENGTH_SHORT).show();
                    return;
                }
            });
        }
    }

    private void checkBalance() {
        if (sessionManager.getUser().getCoin() <= sessionManager.getSetting().getChatCharge()) {
            isInsufficentBalance = true;
        }
    }


    private void initListnear() {
        binding.etChat.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEND) {
                    binding.btnsend.performClick();
                    return true;
                }
                return false;
            }
        });
        binding.swipeRefresh.setOnRefreshListener(refreshLayout -> {
            getOldMessages(true);
        });
        binding.btnsend.setOnClickListener(v -> {
            if (isInsufficentBalance) {
                binding.btnsend.setEnabled(false);
                Toast.makeText(this, "Please recharge your wallet", Toast.LENGTH_SHORT).show();
                MyWalletActivity_a.openMe(ChatListActivityGOriginal_a.this);
                return;

            }
            if (isFake) {
                Toast.makeText(this, "This is Dummy User this action can't perform", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!binding.etChat.getText().toString().equals("")) {
                JSONObject object = new JSONObject();
                try {
                    object.put("user_id", userId);
                    object.put("host_id", secondUserId);
                    object.put("sender", "user");
                    object.put("userChatCharge", sessionManager.getSetting().getChatCharge());
                    object.put(STR_TOPIC, chatRoomId);
                    object.put(STR_MSG, binding.etChat.getText().toString().trim());
                    MySocketManager.getInstance().getSocet().emit("chat", object);
                    binding.etChat.setText("");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

        });
    }

    private void getOldMessages(boolean isLoadMore) {
        if (isLoadMore) {
            start = start + Const_a.LIMIT;
        } else {
            start = 0;
            chatAdapterOriginal.clear();
        }
        Call<OriginalMessageRoot> call = RetrofitBuilder_a.create().getOldMessage(Const_a.DEVKEY, userId, "user", chatRoomId, start, Const_a.LIMIT);
        call.enqueue(new Callback<OriginalMessageRoot>() {
            @Override
            public void onResponse(Call<OriginalMessageRoot> call, Response<OriginalMessageRoot> response) {
                if (response.code() == 200 && response.body().isStatus() && !response.body().getData().isEmpty()) {
                    chatAdapterOriginal.addData(response.body().getData());
                }
                binding.swipeRefresh.finishRefresh();
                binding.swipeRefresh.finishLoadMore();
                binding.loader.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call<OriginalMessageRoot> call, Throwable t) {
                Log.d("TAG", "onFailure: " + t.getMessage());
                binding.loader.setVisibility(View.GONE);
                binding.swipeRefresh.finishRefresh();
                binding.swipeRefresh.finishLoadMore();
            }
        });
    }

    private void initView() {
        binding.rvChat.setAdapter(chatAdapterOriginal);
        checkBalance();
        getOldChat();
    }

    public void onClickBack(View view) {
        finish();
    }

    private void getOldChat() {
        if (chatRoomId == null || chatRoomId.isEmpty()) {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("user_id", userId);
            jsonObject.addProperty("host_id", secondUserId);
            Call<ChatTopicRoot> call = RetrofitBuilder_a.create().createChatTopic(Const_a.DEVKEY, jsonObject);
            call.enqueue(new Callback<ChatTopicRoot>() {
                @Override
                public void onResponse(Call<ChatTopicRoot> call, Response<ChatTopicRoot> response) {
                    if (response.code() == 200 && response.body().isStatus() && response.body().getData() != null) {
                        chatRoomId = response.body().getData().getId();
                        getOldMessages(false);
                        initListnear();
                    }
                }

                @Override
                public void onFailure(Call<ChatTopicRoot> call, Throwable t) {
                    Log.d(TAG, "onFailure: ChatTopicRoot === " + t.getMessage());
                }
            });
        } else {
            getOldMessages(false);
            initListnear();
        }
    }

    ChatHandler chatHandler = new ChatHandler() {
        @Override
        public void onChat(Object[] args) {
            if (args[0] != null) {
                runOnUiThread(() -> {
                    try {
                        JSONObject response = (JSONObject) args[0];
                        String userId = response.get(STR_USERID).toString();
                        String topic = response.get(STR_TOPIC).toString();
                        String message = response.get(STR_MSG).toString();
                        Log.d(TAG, "onChat: response.get(sender).toString()+1 ==   " + response.get("sender").toString());
                        OriginalMessageRoot.DataItem dataItem = new OriginalMessageRoot.DataItem();
                        dataItem.setSender(response.get("sender").toString());
                        dataItem.setTopic(topic);
                        dataItem.setMessage(message);
                        Log.d(TAG, "onChat: response.get(sender).toString()+2 ==   " + response.get("sender").toString());
                        Log.d(TAG, "onChat: dataItem ==  " + dataItem.toString());
                        chatAdapterOriginal.addSingleMessage(dataItem);
                        binding.rvChat.postDelayed(() -> {
                            binding.rvChat.smoothScrollToPosition(0);
                        }, 100);

                    } catch (Exception o) {
                        Log.d(TAG, "run: eooros" + o.getMessage());
                    }

                });
            }

            if (args[1] != null) {
                User localUser = new Gson().fromJson(args[1].toString(), User.class);
                Log.d(TAG, "onChat: localUser == " + localUser.toString());
                sessionManager.saveUser(localUser);
                checkBalance();
            }
        }
    };

    public void onclicProfile(View view) {
        if (isFake) {
            Toast.makeText(this, "This is Dummy User this action can't perform", Toast.LENGTH_SHORT).show();
            return;
        }
        Log.d("TAG", "onclicProfile: secondusser id " + secondUserId);
        startActivity(new Intent(this, GuestProfileActivityG_a.class).putExtra("guestId", secondUserId));
    }

    private CallHandler callHandler = new CallHandler() {
        @Override
        public void onCallRequest(Object[] args) {

        }

        @Override
        public void onCallReceive(Object[] args) {

        }

        @Override
        public void onCallConfirm(Object[] args) {

        }

        @Override
        public void onCallAnswer(Object[] args) {

        }

        @Override
        public void onCallCancel(Object[] args) {

        }

        @Override
        public void onCallDisconnect(Object[] args) {

        }

        @Override
        public void onGiftRequest(Object[] args) {

        }

        @Override
        public void onVgift(Object[] args) {

        }

        @Override
        public void onComment(Object[] args) {

        }

        @Override
        public void onMakeCall(Object[] args) {

        }

        @Override
        public void onIsBusy(Object[] args) {
        }

        @Override
        public void onRefresh(Object[] args) {

        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        isGoneForIntent = false;
        binding.btnsend.setEnabled(true);
        checkBalance();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MySocketManager.getInstance().removeChatListener(chatHandler);
        MySocketManager.getInstance().removeCallListener(callHandler);
    }

    @Override
    protected void onStop() {
        super.onStop();
        MySocketManager.getInstance().removeCallListener(callHandler);
        Log.d(TAG, "CALLLLLL: onStop Chta Activity ");
    }
}