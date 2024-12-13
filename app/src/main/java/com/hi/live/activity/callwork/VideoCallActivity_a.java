package com.hi.live.activity.callwork;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetFileDescriptor;
import android.graphics.Rect;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;

import com.bumptech.glide.Glide;
import com.google.android.material.tabs.TabLayout;
import com.google.gson.Gson;
import com.hi.live.BuildConfig;
import com.hi.live.R;
import com.hi.live.SessionManager__a;
import com.hi.live.activity.BaseActivity_a;
import com.hi.live.activity.VideoBaseActivity_a;
import com.hi.live.activity.purchase.CoinPlanActivity_a;
import com.hi.live.adaptor.BottomViewPagerAdapter_a;
import com.hi.live.adaptor.CommentAdapterOriginal_a;
import com.hi.live.bottomsheet.BottomSheetReport_g;
import com.hi.live.databinding.ActivityVideoCallBinding;
import com.hi.live.databinding.ItemNotificationLowBalanceBinding;
import com.hi.live.models.EmojiIconRoot;
import com.hi.live.models.EmojicategoryRoot;
import com.hi.live.models.HostRoot;
import com.hi.live.models.User;
import com.hi.live.models.UserRoot;
import com.hi.live.oflineModels.VideoCallDataRoot;
import com.hi.live.oflineModels.gift.GiftRoot;
import com.hi.live.popus.EmojiRequestPopup_g;
import com.hi.live.retrofit.ApiCalling_a;
import com.hi.live.retrofit.Const_a;
import com.hi.live.retrofit.RetrofitBuilder_a;
import com.hi.live.socket.CallHandler;
import com.hi.live.socket.MySocketManager;
import com.hi.live.socket.SocketConnectHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import io.agora.rtc.Constants;
import io.agora.rtc.IRtcEngineEventHandler;
import io.agora.rtc.RtcEngine;
import io.agora.rtc.video.VideoCanvas;
import io.agora.rtc.video.VideoEncoderConfiguration;
import io.socket.client.Socket;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class VideoCallActivity_a extends VideoBaseActivity_a {
    private static final String TAG = "VideoCallActivity_a";
    private static final int SHEET_OPEN = 1;
    private static final int SHEET_CLOSE = 2;
    SessionManager__a sessionManager;
    Handler timerHandler = new Handler();
    ActivityVideoCallBinding binding;
    Handler handler = new Handler();
    EmojiRequestPopup_g emojiRequestPopup;
    private RtcEngine mRtcEngine;
    private boolean mCallEnd;
    private boolean mMuted;
    private FrameLayout mLocalContainer;
    private RelativeLayout mRemoteContainer;
    private VideoCanvas mLocalVideo;
    private VideoCanvas mRemoteVideo;
    private int seconds = 0;
    private ImageView mMuteBtn;
    private String token;
    private VideoCallDataRoot videoCallDataRoot;
    private boolean istost = false;
    private String callId;
    private Socket socket;
    private List<EmojicategoryRoot.Datum> finelCategories = new ArrayList<>();
    private String hostId;
    private CallApiWork_a callApiWorkG;
    private static final int PERMISSION_REQ_ID = 22;
    private static final int REQ_ID = 1;
    private static final String[] REQUESTED_PERMISSIONS = {Manifest.permission.RECORD_AUDIO, Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};
    public static String[] storge_permissions_33 = {Manifest.permission.READ_MEDIA_IMAGES, Manifest.permission.READ_MEDIA_AUDIO, Manifest.permission.READ_MEDIA_VIDEO, Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO
    };
    Runnable timerRunnable = new Runnable() {
        @Override
        public void run() {
            seconds++;
            if (seconds % 60 == 0) {
                reduseCoin(sessionManager.getSetting().getUserCallCharge());
            }
            int p1 = seconds % 60;
            int p2 = seconds / 60;
            int p3 = p2 % 60;
            p2 = p2 / 60;

            String sec;
            String hour;
            String min;
            if (p1 < 10) {
                sec = "0" + p1;
            } else {
                sec = String.valueOf(p1);
            }
            if (p2 < 10) {
                hour = "0" + p2;
            } else {
                hour = String.valueOf(p2);
            }
            if (p3 < 10) {
                min = "0" + p3;
            } else {
                min = String.valueOf(p3);
            }
            binding.tvtimer.setText(hour + ":" + min + ":" + sec);


            timerHandler.postDelayed(this, 1000);
        }
    };
    Runnable runnable = () -> endCall(158);
    private boolean isVideoDecode = false;
    private final IRtcEngineEventHandler mRtcEventHandler = new IRtcEngineEventHandler() {

        @Override
        public void onJoinChannelSuccess(String channel, final int uid, int elapsed) {
            runOnUiThread(() -> {
                Log.d(TAG, "run: join chennel success");

            });
        }

        @Deprecated
        @Override
        public void onFirstRemoteVideoDecoded(final int uid, int width, int height, int elapsed) {
            runOnUiThread(() -> {
                isVideoDecode = true;
                handler.removeCallbacks(runnable);
                Log.d(TAG, "run: first vid ddecode");
                setupRemoteVideo(uid);
                binding.animationView.setVisibility(View.GONE);
                timerHandler.postDelayed(timerRunnable, 1000);
                reduseCoin(sessionManager.getSetting().getUserCallCharge());
            });
        }

        @Override
        public void onUserOffline(final int uid, int reason) {
            runOnUiThread(() -> {

                Log.d(TAG, "run: user offline");
                onRemoteUserLeft(uid);
            });
        }

        @Override
        public void onError(int err) {
            super.onError(err);
            Log.d(TAG, "onError: error code == " + err);
            istost = true;

            endCall(251);
        }


        @Override
        public void onLeaveChannel(RtcStats stats) {
            super.onLeaveChannel(stats);
            Log.d(TAG, "onLeaveChannel: ===================================================================");
            if (!isFinishing()) {
                finish();
            }
        }
    };


    private String hostAgencyId = "";
    private MediaPlayer player2;
    private CommentAdapterOriginal_a commentAdapter = new CommentAdapterOriginal_a();

    private void setupRemoteVideo(int uid) {
        ViewGroup parent = mRemoteContainer;
        if (mLocalVideo != null && mLocalVideo.view != null) {
            if (parent.indexOfChild(mLocalVideo.view) > -1) {
                parent = mLocalContainer;
            }
        }
        if (mRemoteVideo != null) {
            return;
        }
        SurfaceView view = RtcEngine.CreateRendererView(getBaseContext());
        view.setZOrderMediaOverlay(parent == mLocalContainer);
        parent.addView(view);
        mRemoteVideo = new VideoCanvas(view, VideoCanvas.RENDER_MODE_HIDDEN, uid);
        // Initializes the video view of a remote user.
        mRtcEngine.setupRemoteVideo(mRemoteVideo);
    }

    private void onRemoteUserLeft(int uid) {
        if (mRemoteVideo != null && mRemoteVideo.uid == uid) {
            removeFromParent(mRemoteVideo);
            // Destroys remote view
            mRemoteVideo = null;
            istost = true;
            endCall(304);
        }
    }

    private void getHost() {
        ApiCalling_a apiCalling = new ApiCalling_a(this);
        apiCalling.getHostProfile(hostId, new ApiCalling_a.OnHostProfileGetListnear() {
            @Override
            public void onHostGet(HostRoot.Host user) {
                hostAgencyId = user.getAgencyId();
                binding.btnRepot.setVisibility(View.VISIBLE);
            }

            @Override
            public void onFail() {

            }
        });
    }

    private void reduseCoin(int coin) {
        callApiWorkG.callAccepted(callId, String.valueOf(coin), seconds, new CallApiWork_a.CallCoinCutApiListener() {
            @Override
            public void onSuccess(String callId) {
                setupMyData();
            }

            @Override
            public void onFailure(String error) {
                Log.d(TAG, "onFailure: callAccepted =========" + error);
                updateUserAndCall();
            }
        });
    }

    private void updateUserAndCall() {
        Call<UserRoot> call = RetrofitBuilder_a.create().getProfile(Const_a.DEVKEY, sessionManager.getUser().getId());
        call.enqueue(new Callback<UserRoot>() {
            @Override
            public void onResponse(Call<UserRoot> call, Response<UserRoot> response) {
                if (response.code() == 200 && response.body().isStatus()) {
                    if (response.body().getUser() != null) {
                        sessionManager.saveUser(response.body().getUser());
                        Toast.makeText(VideoCallActivity_a.this, "Not Enough Balance", Toast.LENGTH_SHORT).show();
                        endCall(354);
                    }
                }
            }

            @Override
            public void onFailure(Call<UserRoot> call, Throwable t) {

            }
        });
    }

    CallHandler callHandler = new CallHandler() {
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
            runOnUiThread(() -> {
                if (args[0] != null) {
                    if (!isFinishing()) {
                        try {
                            JSONObject jsonObject = new JSONObject(args[0].toString());
                            if (callId.equals(jsonObject.getString("callId"))) {
                                endCall(329);
                            }
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
            });

        }

        @Override
        public void onCallDisconnect(Object[] args) {
            if (args[0] != null) {
                Log.d(TAG, "onCallDisconnect: args[0].toString() === " + args[0].toString());
                String callId = args[0].toString();
                if (!isFinishing() && callId.equals(videoCallDataRoot.getCallId())) {
                    runOnUiThread(() -> {
                        endCall(336);
                    });
                }
            }
        }

        @Override
        public void onGiftRequest(Object[] args) {
            if (args[0] != null) {
                String objStr = args[0].toString();
                Log.d(TAG, "onGiftRequest: objStr === " + objStr);
                if (objStr != null && !objStr.isEmpty()) {
                    if (objStr != null) {
                        runOnUiThread(() -> {
                            String filtertype = null;
                            filtertype = objStr.toString();
                            EmojiIconRoot.Datum requestedGift = new Gson().fromJson(filtertype, EmojiIconRoot.Datum.class);
                            if (requestedGift != null) {
                                Log.d(TAG, "onGiftRequest: requestedGift =======   === " + requestedGift.toString());
                                emojiRequestPopup.dismiss();
                                emojiRequestPopup.openPopup(requestedGift1 -> sendGift(requestedGift), requestedGift, videoCallDataRoot.getHostName());
                            }
                        });
                    }
                }
            }
        }

        @Override
        public void onVgift(Object[] args1) {
            if (args1[0] != null) {
                Log.d(TAG, "initSoket: vgift   " + args1);
                Log.d(TAG, "initSoket: vgift   " + args1[0]);
                String objStr = args1[0].toString();
                if (!objStr.isEmpty()) {
                    runOnUiThread(() -> {
                        String filtertype = null;
                        filtertype = objStr;
                        GiftRoot gift = new Gson().fromJson(filtertype, GiftRoot.class);
                        Log.d(TAG, "initSoket: ggg " + gift.toString());
                        Glide.with(getApplicationContext()).load(BuildConfig.BASE_URL + gift.getImage()).into(binding.imgAnimation);
                        Log.d(TAG, "initSoket: gift 2");
                        ScaleAnimation btnAnimation = new ScaleAnimation(1f, 0.8f, // Start and end values for the X axis scaling
                                1f, 0.8f, Animation.RELATIVE_TO_SELF, 0.5f, // Pivot point of X scaling
                                Animation.RELATIVE_TO_SELF, 0.5f);
                        btnAnimation.setDuration(250); //1 second duration for each animation cycle
                        btnAnimation.setRepeatCount(Animation.REVERSE); //repeating indefinitely
                        btnAnimation.setFillAfter(true);
                        btnAnimation.setRepeatMode(Animation.REVERSE); //animation will start from end point once ended.
                        binding.imgAnimation.startAnimation(btnAnimation);
                        new Handler().postDelayed(() -> {
                            binding.imgAnimation.setImageDrawable(null);
                            btnAnimation.cancel();
                        }, 2500);
                        makeSound();

                        socket.emit("refresh", "refreshh");

                    });

                }

            }

            if (args1[1] != null) {  // user
                runOnUiThread(() -> {
                    Log.d(TAG, "giftReciveListnear : user data === " + args1[1].toString());
                    User user = new Gson().fromJson(args1[1].toString(), User.class);

                    if (sessionManager.getUser().getId().equals(user.getId())) {
                        sessionManager.saveUser(user);
                    }
                    binding.bottomPage.tvUsereCoin.setText(String.valueOf(sessionManager.getUser().getCoin()));
                    Log.d(TAG, "giftReciveListnear: user save thya che ===== ");
                });
            }


        }

        @Override
        public void onComment(Object[] args1) {
            if (args1[0] != null) {
                runOnUiThread(() -> {
                    try {
                        JSONObject response = (JSONObject) args1[0];

                        commentAdapter.addComment(response);
                        binding.rvComments.scrollToPosition(commentAdapter.getItemCount() - 1);
                    } catch (Exception o) {
                        Log.d(TAG, "run: eooros" + o.getMessage());
                    }

                });

            }
        }

        @Override
        public void onMakeCall(Object[] args) {

        }

        @Override
        public void onIsBusy(Object[] args) {

        }

        @Override
        public void onRefresh(Object[] args1) {
            String objStr = args1[0].toString();
            if (objStr != null && !objStr.isEmpty()) {

                if (objStr != null) {
                    runOnUiThread(() -> {

                        String filtertype = null;

                        filtertype = objStr.toString();
                        EmojiIconRoot.Datum requestedGift = new Gson().fromJson(filtertype, EmojiIconRoot.Datum.class);
                        if (requestedGift != null) {
                            emojiRequestPopup.dismiss();
                            emojiRequestPopup.openPopup(requestedGift1 -> sendGift(requestedGift), requestedGift, videoCallDataRoot.getHostName());

                        }

                    });

                }
            }
        }
    };

    private void setupMyData() {
        int myCoin = sessionManager.getUser().getCoin();
        binding.bottomPage.tvUsereCoin.setText(String.valueOf(myCoin));
        Log.d(TAG, "setupMyData: my coin " + myCoin);
        Log.d(TAG, "setupMyData: vid charge  " + sessionManager.getSetting().getUserCallCharge());
        if (myCoin <= sessionManager.getSetting().getUserCallCharge() * 3) {
            binding.lytLowBalance.lytmain.setVisibility(View.VISIBLE);
            initLowBalanceLayoutListner(binding.lytLowBalance);
        }

    }

    private void initListner() {
        binding.btnCall.setOnClickListener(view -> endCall(529));
        binding.etComment.setOnClickListener(view -> {
            binding.rvComments.postDelayed(() -> {
                binding.rvComments.scrollToPosition(commentAdapter.getItemCount() - 1);
            }, 500);
        });
        binding.btnsend.setOnClickListener(v -> {
            String message = binding.etComment.getText().toString();
            if (message.isEmpty()) {
                return;
            }
            JSONObject object = new JSONObject();
            try {
                if (sessionManager.getUser().getName().equals("Gil U Live User")) {
                    object.put("name", "User");
                } else {
                    object.put("name", sessionManager.getUser().getName());
                }

                object.put("comment", binding.etComment.getText().toString());
                object.put("token", token);
                object.put("callId", videoCallDataRoot.getCallId());

                socket.emit("comment", object);
//                ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(binding.etComment.getWindowToken(), 0);
            } catch (JSONException e) {
                Log.d(TAG, "onClick: btnsend er  " + e.getMessage());
                e.printStackTrace();
            }


            binding.etComment.setText("");
        });
    }


    private void makeSound() {
        if (player2 != null) {
            player2.release();
            player2 = null;
        }
        try {
            player2 = new MediaPlayer();
            try {
                AssetFileDescriptor afd2 = getAssets().openFd("pop.mp3");
                player2.setDataSource(afd2.getFileDescriptor(), afd2.getStartOffset(), afd2.getLength());
                player2.prepare();
                player2.start();
            } catch (IOException e) {
                Log.d(TAG, "initUI: " + e.getMessage());
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.d(TAG, "initUI: errrr " + e.getMessage());
        }
    }

    private void initUI() {
        mLocalContainer = findViewById(R.id.local_video_view_container);
        mRemoteContainer = findViewById(R.id.remote_video_view_container);

        mMuteBtn = findViewById(R.id.btn_mute);
        emojiRequestPopup = new EmojiRequestPopup_g(this);

        getGiftsCategories();

        binding.rvComments.setAdapter(commentAdapter);
        // Sample logs are optional.

    }

    private void getGiftsCategories() {
        Call<EmojicategoryRoot> call = RetrofitBuilder_a.create().getCategories(Const_a.DEVKEY);
        call.enqueue(new Callback<EmojicategoryRoot>() {
            @Override
            public void onResponse(Call<EmojicategoryRoot> call, Response<EmojicategoryRoot> response) {
                if (response.code() == 200 && response.body().getStatus() && !response.body().getData().isEmpty()) {

                    List<EmojicategoryRoot.Datum> categories = response.body().getData();
                    Log.d(TAG, "onResponse: categorysixe " + categories.size());
                    finelCategories = new ArrayList<>();
                    EmojicategoryRoot.Datum tempobj = null;
                    for (int i = 0; i < categories.size(); i++) {
                        if (Boolean.TRUE.equals(categories.get(i).getIsTop())) {
                            tempobj = categories.get(i);

                        } else {
                            finelCategories.add(categories.get(i));
                        }
                    }

                    if (tempobj != null) {
                        finelCategories.add(0, tempobj);
                    }


                    BottomViewPagerAdapter_a bottomViewPagerAdapterG = new BottomViewPagerAdapter_a(finelCategories);
                    binding.bottomPage.viewpager.setAdapter(bottomViewPagerAdapterG);
                    settabLayout(finelCategories);
                    binding.bottomPage.viewpager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(binding.bottomPage.tablayout));
                    binding.bottomPage.tablayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
                        @Override
                        public void onTabSelected(TabLayout.Tab tab) {
                            binding.bottomPage.viewpager.setCurrentItem(tab.getPosition());
                        }

                        @Override
                        public void onTabUnselected(TabLayout.Tab tab) {
                            //ll
                        }

                        @Override
                        public void onTabReselected(TabLayout.Tab tab) {
                            //ll
                        }
                    });
                    bottomViewPagerAdapterG.setEmojiListnerViewPager((bitmap, coin, emoji) -> {
                        sendGift(emoji);

                    });
                }
            }


            private void settabLayout(List<EmojicategoryRoot.Datum> categories) {
                binding.bottomPage.tablayout.setTabGravity(TabLayout.GRAVITY_FILL);
                for (int i = 0; i < categories.size(); i++) {

                    binding.bottomPage.tablayout.addTab(binding.bottomPage.tablayout.newTab().setCustomView(createCustomView(categories.get(i))));

                }
            }

            private View createCustomView(EmojicategoryRoot.Datum datum) {
                Log.d(TAG, "settabLayout: " + datum.getName());
                Log.d(TAG, "settabLayout: " + datum.getIcon());
                View v = LayoutInflater.from(VideoCallActivity_a.this).inflate(R.layout.custom_tabgift, null);
                TextView tv = (TextView) v.findViewById(R.id.tvTab);
                tv.setText(datum.getName());
                ImageView img = (ImageView) v.findViewById(R.id.imagetab);

                Glide.with(getApplicationContext()).load(BuildConfig.BASE_URL + datum.getIcon()).placeholder(R.drawable.ic_gift).into(img);
                return v;

            }


            @Override
            public void onFailure(Call<EmojicategoryRoot> call, Throwable t) {
//ll
            }
        });
    }

    private void updetUI(int state) {
        if (state == SHEET_OPEN) {
            binding.bottomPage.lyt2.setVisibility(View.VISIBLE);
            binding.controlPanel.setVisibility(View.GONE);
            binding.rvComments.setVisibility(View.GONE);

        } else {
            binding.bottomPage.lyt2.setVisibility(View.GONE);
            binding.controlPanel.setVisibility(View.VISIBLE);
            binding.rvComments.setVisibility(View.VISIBLE);

        }
    }

    private void sendGift(EmojiIconRoot.Datum emoji) {
        if (emoji.getCoin() >= sessionManager.getUser().getCoin()) {
            updetUI(SHEET_CLOSE);
            Toast.makeText(VideoCallActivity_a.this, "Not Enough Balance", Toast.LENGTH_SHORT).show();
//            endCall(737);
        } else {
            GiftRoot giftRoot = new GiftRoot();
            giftRoot.setCoin(emoji.getCoin());
            giftRoot.setImage(emoji.getIcon());
            giftRoot.setGiftId(emoji.get_id());
            giftRoot.setHost_id(hostId);
            giftRoot.setCallId(callId);
            giftRoot.setUsername(sessionManager.getUser().getName());
            giftRoot.setUserid(sessionManager.getUser().getId());

            socket.emit("vGift", new Gson().toJson(giftRoot));
            //   setupMyData();  todo
            updetUI(SHEET_CLOSE);
        }
    }


    private boolean checkSelfPermission(String permission, int requestCode) {
        if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
//            ActivityCompat.requestPermissions(this, REQUESTED_PERMISSIONS, requestCode);
            return false;
        }

        return true;
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == PERMISSION_REQ_ID) {

            if (grantResults[0] != PackageManager.PERMISSION_GRANTED || grantResults[1] != PackageManager.PERMISSION_GRANTED || grantResults[2] != PackageManager.PERMISSION_GRANTED || grantResults[3] != PackageManager.PERMISSION_GRANTED) {
                Log.e("TAG", "onRequestPermissionsResult: 11 ");

                showLongToast("Need permissions " + Manifest.permission.WRITE_EXTERNAL_STORAGE);
                return;
            }

            initEngineAndJoinChannel();
        } else {
            if (grantResults[0] != PackageManager.PERMISSION_GRANTED || grantResults[1] != PackageManager.PERMISSION_GRANTED || grantResults[2] != PackageManager.PERMISSION_GRANTED || grantResults[3] != PackageManager.PERMISSION_GRANTED || grantResults[4] != PackageManager.PERMISSION_GRANTED) {
                Log.e("TAG", "onRequestPermissionsResult: 22 ");

                showLongToast("Need permissions " + "/" + Manifest.permission.WRITE_EXTERNAL_STORAGE);

                return;
            }
            initEngineAndJoinChannel();
        }
    }

    private void showLongToast(final String msg) {
        this.runOnUiThread(() -> Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show());
    }

    private void initEngineAndJoinChannel() {
        // This is our usual steps for joining
        // a channel and starting a call.
        initializeEngine();
        setupVideoConfig();
        setupLocalVideo();
        joinChannel();
    }

    private void initializeEngine() {
        try {
            Log.d(TAG, "initializeEngine: agora id ========================" + sessionManager.getSetting().getAgoraId());
            mRtcEngine = RtcEngine.create(getBaseContext(), sessionManager.getSetting().getAgoraId(), mRtcEventHandler);
            mRtcEngine.setClientRole(IRtcEngineEventHandler.ClientRole.CLIENT_ROLE_AUDIENCE);
        } catch (Exception e) {
            Log.e(TAG, Log.getStackTraceString(e));
            throw new RuntimeException("NEED TO check rtc sdk init fatal error\n" + Log.getStackTraceString(e));
        }
    }

    private void setupVideoConfig() {
        // In simple use cases, we only need to enable video capturing
        // and rendering once at the initialization step.
        // Note: audio recording and playing is enabled by default.
        mRtcEngine.enableVideo();

        // Please go to this page for detailed explanation
        // https://docs.agora.io/en/Video/API%20Reference/java/classio_1_1agora_1_1rtc_1_1_rtc_engine.html#af5f4de754e2c1f493096641c5c5c1d8f
        mRtcEngine.setVideoEncoderConfiguration(new VideoEncoderConfiguration(VideoEncoderConfiguration.VD_840x480, VideoEncoderConfiguration.FRAME_RATE.FRAME_RATE_FPS_15, VideoEncoderConfiguration.COMPATIBLE_BITRATE, VideoEncoderConfiguration.ORIENTATION_MODE.ORIENTATION_MODE_FIXED_PORTRAIT));
    }

    private void setupLocalVideo() {
        // This is used to set a local preview.
        // The steps setting local and remote view are very similar.
        // But note that if the local user do not have a uid or do
        // not care what the uid is, he can set his uid as ZERO.
        // Our server will assign one and return the uid via the event
        // handler callback function (onJoinChannelSuccess) after
        // joining the channel successfully.
        SurfaceView view = RtcEngine.CreateRendererView(getBaseContext());
        view.setZOrderMediaOverlay(true);
        mLocalContainer.addView(view);
        // Initializes the local video view.
        // RENDER_MODE_HIDDEN: Uniformly scale the video until it fills the visible boundaries. One dimension of the video may have clipped contents.
        mLocalVideo = new VideoCanvas(view, VideoCanvas.RENDER_MODE_HIDDEN, 0);
        mRtcEngine.setupLocalVideo(mLocalVideo);
    }

    private void joinChannel() {
        mRtcEngine.setDefaultAudioRoutetoSpeakerphone(true);

        /** Sets the channel profile of the Agora RtcEngine.
         CHANNEL_PROFILE_COMMUNICATION(0): (Default) The Communication profile.
         Use this profile in one-on-one calls or group calls, where all users can talk freely.
         CHANNEL_PROFILE_LIVE_BROADCASTING(1): The Live-Broadcast profile. Users in a android-broadcast
         channel have a role as either broadcaster or audience. A broadcaster can both send and receive streams;
         an audience can only receive streams.*/
        mRtcEngine.setChannelProfile(Constants.CHANNEL_PROFILE_LIVE_BROADCASTING);
        /**In the demo, the default is to enter as the anchor.*/
        mRtcEngine.setClientRole(IRtcEngineEventHandler.ClientRole.CLIENT_ROLE_BROADCASTER);
        // Enable video module
        mRtcEngine.enableVideo();
        // Setup video encoding configs

        // 1. Users can only see each other after they join the
        // same channel successfully using the same app id.
        // 2. One token is only valid for the channel name that
        // you use to generate this token.
        if (TextUtils.isEmpty(token) || TextUtils.equals(token, "#YOUR ACCESS TOKEN#")) {
            token = null; // default, no token
        }
        Log.d(TAG, "joinChannel: getChannel ================================================ " + videoCallDataRoot.getChannel());
        Log.d(TAG, "joinChannel: token ================================================ " + token);
        mRtcEngine.joinChannel(token, videoCallDataRoot.getChannel(), "Extra Optional Data", 0);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_video_call);
        sessionManager = new SessionManager__a(this);
        BaseActivity_a.STATUS_VIDEO_CALL = true;
        Intent intent = getIntent();
        socket = MySocketManager.getInstance().getSocet();
        MySocketManager.getInstance().addCallListener(callHandler);
        MySocketManager.getInstance().addSocketConnectHandler(socketConnectHandler);
        callApiWorkG = new CallApiWork_a(VideoCallActivity_a.this);
        BaseActivity_a.isUserBusyLocal = true;
        if (intent != null) {
            String onjStr = intent.getStringExtra("datastr");
            Log.d(TAG, "onCreate: intent " + onjStr);
            if (!onjStr.equals("")) {
                videoCallDataRoot = new Gson().fromJson(onjStr, VideoCallDataRoot.class);
                hostId = videoCallDataRoot.getHostId();
                token = videoCallDataRoot.getToken();
                callId = videoCallDataRoot.getCallId();
                Log.d(TAG, "onCreate: videoCallDataRoot.toString() ==== " + videoCallDataRoot.toString());
                initUI();
                setupMyData();
                handleKeyboard();
                getHost();
                initListner();

                Log.d(TAG, "vcall: clientid " + videoCallDataRoot.getClientId());
                Log.d(TAG, "vcall: clientname " + videoCallDataRoot.getClientName());
                Log.d(TAG, "vcall: hostid " + videoCallDataRoot.getHostId());
                Log.d(TAG, "vcall: hostname " + videoCallDataRoot.getHostName());


                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    Log.e("TAG", "onCreate: >>>>>>>>>>>>>  11 ");

                    if (checkSelfPermission(storge_permissions_33[0], REQ_ID) && checkSelfPermission(storge_permissions_33[1], REQ_ID) && checkSelfPermission(storge_permissions_33[2], REQ_ID) && checkSelfPermission(storge_permissions_33[3], REQ_ID) && checkSelfPermission(storge_permissions_33[4], REQ_ID)) {
                        initEngineAndJoinChannel();
                        handler.postDelayed(runnable, 6000);
                    } else {
                        ActivityCompat.requestPermissions(this, storge_permissions_33, REQ_ID);
                    }

                } else {
                    Log.e("TAG", "onCreate: >>>>>>>>>>>>>  22 ");
                    if (checkSelfPermission(REQUESTED_PERMISSIONS[0], PERMISSION_REQ_ID) && checkSelfPermission(REQUESTED_PERMISSIONS[1], PERMISSION_REQ_ID) && checkSelfPermission(REQUESTED_PERMISSIONS[2], PERMISSION_REQ_ID) && checkSelfPermission(REQUESTED_PERMISSIONS[3], PERMISSION_REQ_ID)) {
                        initEngineAndJoinChannel();
                        handler.postDelayed(runnable, 6000);
                    } else {

                        ActivityCompat.requestPermissions(this, REQUESTED_PERMISSIONS, PERMISSION_REQ_ID);

                    }
                }
            }
        }

    }

    public void onLocalAudioMuteClicked(View view) {
        mMuted = !mMuted;
        // Stops/Resumes sending the local audio stream.
        mRtcEngine.muteLocalAudioStream(mMuted);
        int res = mMuted ? R.drawable.btn_mute : R.drawable.btn_unmute;
        mMuteBtn.setImageResource(res);
    }

    public void onSwitchCameraClicked(View view) {
        // Switches between front and rear cameras.
        mRtcEngine.switchCamera();
    }

    private void endCall(int line) {
        Log.d(TAG, "endCall: line === " + line);
        if (!isFinishing()) {
            Log.d(TAG, "endCall: ========================= ");
            mRtcEngine.leaveChannel();
            MySocketManager.getInstance().getSocet().emit(Const_a.CALLDISCONNECT, callId);
            BaseActivity_a.isUserBusyLocal = false;
            BaseActivity_a.STATUS_VIDEO_CALL = false;
            timerHandler.removeCallbacks(timerRunnable);
            removeFromParent(mLocalVideo);
            mLocalVideo = null;
            removeFromParent(mRemoteVideo);
            mRemoteVideo = null;
            finish();
        }
    }

    private ViewGroup removeFromParent(VideoCanvas canvas) {
        if (canvas != null) {
            ViewParent parent = canvas.view.getParent();
            if (parent != null) {
                ViewGroup group = (ViewGroup) parent;
                group.removeView(canvas.view);
                return group;
            }
        }
        return null;
    }

    private void switchView(VideoCanvas canvas) {
        ViewGroup parent = removeFromParent(canvas);
        if (parent == mLocalContainer) {
            if (canvas.view instanceof SurfaceView) {
                ((SurfaceView) canvas.view).setZOrderMediaOverlay(false);
            }
            mRemoteContainer.addView(canvas.view);
        } else if (parent == mRemoteContainer) {
            if (canvas.view instanceof SurfaceView) {
                ((SurfaceView) canvas.view).setZOrderMediaOverlay(true);
            }
            mLocalContainer.addView(canvas.view);
        }
    }

    public void onLocalContainerClick(View view) {
        switchView(mLocalVideo);
        switchView(mRemoteVideo);
    }

    public void onClickReport(View view) {

        new BottomSheetReport_g(this, hostAgencyId, videoCallDataRoot.getHostId(), () -> {
            LayoutInflater inflater = getLayoutInflater();
            View layout = inflater.inflate(R.layout.toast_layout, (ViewGroup) findViewById(R.id.customtoastlyt));


            Toast toast = new Toast(getApplicationContext());
            toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
            toast.setDuration(Toast.LENGTH_LONG);
            toast.setView(layout);
            toast.show();
        });

    }

    public void onclickGiftIcon(View view) {
        updetUI(SHEET_OPEN);
        binding.bottomPage.btnclose.setOnClickListener(v -> updetUI(SHEET_CLOSE));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (!mCallEnd) {

            endCall(1013);
            MySocketManager.getInstance().removeCallListener(callHandler);
            MySocketManager.getInstance().removeSocketListener(socketConnectHandler);
        }
        BaseActivity_a.isUserBusyLocal = false;
        /*
          Destroys the RtcEngine instance and releases all resources used by the Agora SDK.

          This method is useful for apps that occasionally make voice or video calls,
          to free up resources for other operations when not making calls.
         */
        RtcEngine.destroy();

    }


    public void initLowBalanceLayoutListner(ItemNotificationLowBalanceBinding lytLowBalance) {
        int balance = sessionManager.getUser().getCoin();
        lytLowBalance.tvCoin.setText(String.valueOf(balance));
        lytLowBalance.btnClose.setOnClickListener(v -> lytLowBalance.lytmain.setVisibility(View.GONE));
        lytLowBalance.btnRecharge.setOnClickListener(v -> {
            endCall(1034);
            startActivity(new Intent(VideoCallActivity_a.this, CoinPlanActivity_a.class).putExtra("isVip", false));
        });
    }

    SocketConnectHandler socketConnectHandler = new SocketConnectHandler() {
        @Override
        public void onConnect() {

        }

        @Override
        public void onDisconnect() {

        }

        @Override
        public void onReconnecting() {

        }

        @Override
        public void onReconnected(Object[] args) {
            if (args[0] != null) {
                runOnUiThread(() -> {
                    new Handler().postDelayed(() -> {
                        Log.d(TAG, "onReconnected: " + args[0].toString());
                        JSONObject jsonObject = new JSONObject();
                        try {
                            jsonObject.put("id", sessionManager.getUser().getId());
                            jsonObject.put("roomId", callId);
                            MySocketManager.getInstance().getSocet().emit("socketReconnectedJoin", jsonObject);
                            Log.d(TAG, "onReconnected: 11111 " + jsonObject.toString());
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }
                    }, 1000);
                });

            }
        }
    };


    private void handleKeyboard() {
        binding.activityVideoChatView.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            private int previousHeight;

            @Override
            public boolean onPreDraw() {
                Rect r = new Rect();
                binding.activityVideoChatView.getWindowVisibleDisplayFrame(r);

                int screenHeight = binding.activityVideoChatView.getHeight();

                // Calculate the height difference between the screen height and the visible display frame height
                int keypadHeight = screenHeight - r.bottom;

                // Check if the height difference is significant enough to consider the keyboard as visible
                if (keypadHeight > screenHeight * 0.15) {
                    // Keyboard is visible
                    if (keypadHeight != previousHeight) {
                        // Keyboard state has changed
                        onKeyboardVisible(true);
                        previousHeight = keypadHeight;
                    }
                } else {
                    // Keyboard is not visible
                    if (previousHeight != 0) {
                        // Keyboard state has changed
                        onKeyboardVisible(false);
                        previousHeight = 0;
                    }
                }

                return true;
            }
        });
    }

    private void onKeyboardVisible(boolean isVisible) {
        // Handle keyboard visibility change
        if (isVisible) {
            Log.d(TAG, "onKeyboardVisible: Yes");
        } else {
            Log.d(TAG, "onKeyboardVisible: NOT");

        }
    }

}