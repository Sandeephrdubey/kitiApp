package com.hi.live.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
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
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.tabs.TabLayout;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.hi.live.BuildConfig;
import com.hi.live.LivexUtils_a;
import com.hi.live.R;
import com.hi.live.SessionManager__a;
import com.hi.live.activity.purchase.CoinPlanActivity_a;
import com.hi.live.adaptor.BottomViewPagerAdapter_a;
import com.hi.live.adaptor.CommentAdapterOriginal_a;
import com.hi.live.adaptor.EmojiAdapter_a;
import com.hi.live.bottomsheet.BottomSheetReport_g;
import com.hi.live.databinding.ActivityWatchBinding;
import com.hi.live.databinding.BottomSheetChatBinding;
import com.hi.live.databinding.ItemNotificationLowBalanceBinding;
import com.hi.live.models.ChatSendRoot;
import com.hi.live.models.CommentRootOriginal;
import com.hi.live.models.EmojiIconRoot;
import com.hi.live.models.EmojicategoryRoot;
import com.hi.live.models.HostEmojiRoot;
import com.hi.live.models.HostRoot;
import com.hi.live.models.RestResponse;
import com.hi.live.models.StikerRoot;
import com.hi.live.models.Thumb;
import com.hi.live.models.User;
import com.hi.live.networkManager.NetworkChangeReceiver_a;
import com.hi.live.oflineModels.Filters.FilterRoot;
import com.hi.live.oflineModels.Filters.FilterUtils;
import com.hi.live.oflineModels.gif.GifRoot;
import com.hi.live.oflineModels.gift.GiftRoot;
import com.hi.live.retrofit.ApiCalling_a;
import com.hi.live.retrofit.CoinWork_a;
import com.hi.live.retrofit.Const_a;
import com.hi.live.retrofit.RetrofitBuilder_a;
import com.hi.live.socket.LiveHandler;
import com.hi.live.socket.MySocketManager;
import com.hi.live.socket.SocketConnectHandler;
import com.hi.live.socket.SocketConst;

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
import io.socket.emitter.Emitter;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WatchLiveActivity_a extends VideoBaseActivity_a {
    private static final String TAG = "watchact";
    private static final int PERMISSION_REQ_ID = 22;

    private static final int REQ_ID = 1;
    private static final String[] REQUESTED_PERMISSIONS = {
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.CAMERA,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE
    };
    public static String[] storge_permissions_33 = {
            Manifest.permission.READ_MEDIA_IMAGES,
            Manifest.permission.READ_MEDIA_AUDIO,
            Manifest.permission.READ_MEDIA_VIDEO,
            Manifest.permission.CAMERA,
            Manifest.permission.RECORD_AUDIO

    };

    private static final String STR_CMT = "comment";
    private static final int SHEET_OPEN = 1;
    private static final int SHEET_CLOSE = 2;
    private static final String STR_TKN = "token";
    ActivityWatchBinding binding;
    Handler timerHandler = new Handler();

    int totalCoins;
    boolean mCallEnd;
    int rate = 0;
    String hostAgencyId = "";
    private MediaPlayer player2;
    private int uidAgora = -1;
    private SessionManager__a sessionManager;
    private String userId;
    private Thumb datum;
    private String tkn;
    private String hostId;

    private String chennal;
    private RelativeLayout mRemoteContainer;
    private RtcEngine mRtcEngine;
    private List<EmojicategoryRoot.Datum> categories = new ArrayList<>();
    private EmojicategoryRoot.Datum tempobj;
    private CommentAdapterOriginal_a commentAdapter;

    private Socket socket;
    private VideoCanvas mRemoteVideo;
    private String liveStramingId;

    private void sendGift(Long coin, EmojiIconRoot.Datum emoji) {
        if (emoji.getCoin() >= sessionManager.getUser().getCoin()) {
            Toast.makeText(WatchLiveActivity_a.this, "Not Enough Balance", Toast.LENGTH_SHORT).show();
//            endActivityAndCall();
        } else {
            GiftRoot giftRoot = new GiftRoot();
            giftRoot.setCoin(coin);
            giftRoot.setImage(emoji.getIcon());
            giftRoot.setUsername(sessionManager.getUser().getName());
            giftRoot.setLiveStreamingId(liveStramingId);
            giftRoot.setHost_id(hostId);
            giftRoot.setUserid(sessionManager.getUser().getId());
            socket.emit("gift", new Gson().toJson(giftRoot));
            setupMyData();
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_watch);
        socket = MySocketManager.getInstance().getSocet();
        MySocketManager.getInstance().addLiveListener(liveHandler);
        MySocketManager.getInstance().addSocketConnectHandler(socketConnectHandler);
        MainActivityG_a.isHostLive = false;
        sessionManager = new SessionManager__a(this);
        if (sessionManager.getBooleanValue(Const_a.ISLOGIN)) {
            userId = sessionManager.getUser().getId();
        }
        binding.lytLowBalance.lytmain.setVisibility(View.GONE);
        getGiftsCategories();
        Intent intent = getIntent();
        BaseActivity_a.isUserBusyLocal = true;
        if (intent != null) {
            String objstr = intent.getStringExtra("model");
            if (objstr != null && !objstr.equals("")) {
                Log.d(TAG, "onCreate: intent objstr " + objstr);
                datum = new Gson().fromJson(objstr, Thumb.class);
                Log.d(TAG, "onCreate: data " + datum.getVideo());
                tkn = datum.getToken();
                hostId = datum.getHost_id();
                chennal = datum.getChannel();
                liveStramingId = datum.getLiveStreamingId();

                Log.d(TAG, "onCreate: tkn " + tkn);
                Log.d(TAG, "onCreate:hostid " + hostId);
                Log.d(TAG, "onCreate: chanel " + chennal);
                Log.d(TAG, "onCreate: liveId" + liveStramingId);
                initUI();
                createChatRoom();
                setupMyData();
                getHost();
//

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    Log.e("TAG", "onCreate: >>>>>>>>>>>>>  11 ");

                    if (checkSelfPermission(storge_permissions_33[0], REQ_ID) && checkSelfPermission(storge_permissions_33[1], REQ_ID) && checkSelfPermission(storge_permissions_33[2], REQ_ID) && checkSelfPermission(storge_permissions_33[3], REQ_ID) && checkSelfPermission(storge_permissions_33[4], REQ_ID)) {
                        initEngineAndJoinChannel();
                    } else {
                        ActivityCompat.requestPermissions(this, storge_permissions_33, REQ_ID);
                    }

                } else {
                    Log.e("TAG", "onCreate: >>>>>>>>>>>>>  22 ");
                    if (checkSelfPermission(REQUESTED_PERMISSIONS[0], PERMISSION_REQ_ID) &&
                            checkSelfPermission(REQUESTED_PERMISSIONS[1], PERMISSION_REQ_ID) &&
                            checkSelfPermission(REQUESTED_PERMISSIONS[2], PERMISSION_REQ_ID) && checkSelfPermission(REQUESTED_PERMISSIONS[3], PERMISSION_REQ_ID)) {

                        initEngineAndJoinChannel();

                    } else {

                        ActivityCompat.requestPermissions(this, REQUESTED_PERMISSIONS, PERMISSION_REQ_ID);

                    }
                }

            }
        }
        NetworkChangeReceiver_a networkChangeReceiver = new NetworkChangeReceiver_a();
        networkChangeReceiver.setOnNetworkListner(new NetworkChangeReceiver_a.OnNetworkListner() {
            @Override
            public void onDisconnnected() {
                mCallEnd = true;

                if (!isFinishing()) {
                    endCall();
                }
//                endCall();


            }

            @Override
            public void onConnected() {
//ll
            }
        });

//        binding.layProfile.setOnClickListener(view -> {
//            startActivity(new Intent(WatchLiveActivity_a.this, GuestProfileActivityG_a.class).putExtra("guestId", hostId));
//            endCall();
//        });

    }

    private void lessViewCount() {

        try {
            JSONObject object = new JSONObject();
            object.put("user_id", sessionManager.getUser().getId());
            object.put("name", sessionManager.getUser().getName());
            object.put(STR_TKN, tkn);
            object.put("image", sessionManager.getUser().getImage());
            object.put(SocketConst.LIVE_ID, liveStramingId);
            socket.emit("viewless", object);
            Log.d(TAG, "lessViewCount: object ==" + object.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    Runnable timerRunnable = new Runnable() {
        @Override
        public void run() {
            Log.d(TAG, "run: onr min finishedc");

            CoinWork_a coinWork = new CoinWork_a();
            coinWork.transferCoin(userId, hostId, String.valueOf(sessionManager.getSetting().getUserLiveStreamingCharge()));
            coinWork.setOnCoinWorkLIstner(new CoinWork_a.OnCoinWorkLIstner() {
                @Override
                public void onSuccess(User user) {

                    binding.bottomPage.tvUsereCoin.setText(String.valueOf(user.getCoin()));
                    Log.d(TAG, "onResponse: success coin minused");
                    sessionManager.saveUser(user);

                    JSONObject jsonObject = new JSONObject();
                    try {
                        jsonObject.put(SocketConst.LIVE_ID, liveStramingId);
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }

                    socket.emit("refresh", jsonObject);
                    setupMyData();

                    if (sessionManager.getSetting().getUserLiveStreamingCharge() > user.getCoin()) {
                        onInsufficientBalance();
                    }

                }

                @Override
                public void onFailure() {
///ll
                }

                @Override
                public void onInsufficientBalance() {
                    Toast.makeText(WatchLiveActivity_a.this, "Not Enough Balance", Toast.LENGTH_SHORT).show();
                    endActivityAndCall();
//                    openWalletActivity();
                }
            });

            timerHandler.postDelayed(this, 60000);
        }
    };
    Emitter.Listener vidEndedListnear = args -> {
        Log.d(TAG, "call: listnerrrrVideoEnd" + args.length);
        Log.d(TAG, "call: listnerrrrVidEnd   " + args[0].toString());

        if (args[0] != null) {
            runOnUiThread(this::endActivityAndCall);

        }


    };
    private boolean isVideoDecoded = false;

    private void initListear() {
        binding.imgshare.setOnClickListener(view -> {
            String hostName = "";
            if (datum != null && datum.getUsername() != null) {
                hostName = datum.getUsername();
            }
            try {
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("text/plain");
                shareIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.app_name));
                String shareMessage = "\nHello Dear, I am @" + hostName + "\nLet me recommend you this application\n and watch my LiveVideo \n";
                shareMessage = shareMessage + "https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID + "\n\n";
                shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage);
                startActivity(Intent.createChooser(shareIntent, "choose one"));
            } catch (Exception e) {
                //ll
            }
        });
        binding.btnsend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!binding.etComment.getText().toString().equals("")) {
                    JSONObject object = new JSONObject();
                    try {
                        object.put("name", sessionManager.getUser().getName());
                        object.put(STR_CMT, binding.etComment.getText().toString());
                        object.put(STR_TKN, tkn);
                        object.put(SocketConst.LIVE_ID, liveStramingId);
                        socket.emit("msg", object);
                    } catch (JSONException e) {
                        Log.d(TAG, "onClick: btnsend er  " + e.getMessage());
                        e.printStackTrace();
                    }

                    sendCommentToBackend();
                    binding.etComment.setText("");
                }
            }

            private void sendCommentToBackend() {
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("name", sessionManager.getUser().getName());
                jsonObject.addProperty(STR_CMT, binding.etComment.getText().toString());
                jsonObject.addProperty(STR_TKN, tkn);

                Call<RestResponse> call = RetrofitBuilder_a.create().sendCommentToServer(Const_a.DEVKEY, jsonObject);
                call.enqueue(new Callback<RestResponse>() {

                    @Override
                    public void onResponse(Call<RestResponse> call, Response<RestResponse> response) {
                        if (response.code() == 200 && response.body().isStatus()) {
                            Log.d(TAG, "onResponse: comment sended");
                        }
                    }

                    @Override
                    public void onFailure(Call<RestResponse> call, Throwable t) {
                        Log.d(TAG, "onFailure: rest send cmtt " + t.getMessage());
                    }
                });
            }
        });

    }

    private final IRtcEngineEventHandler mRtcEventHandler = new IRtcEngineEventHandler() {
        private void addViewCount() {
            Log.d(TAG, "addViewCount: ");
            JSONObject object = new JSONObject();
            try {
                object.put("user_id", sessionManager.getUser().getId());
                object.put("name", sessionManager.getUser().getName());
                object.put(STR_TKN, tkn);
                object.put("image", sessionManager.getUser().getImage());
                object.put(SocketConst.LIVE_ID, liveStramingId);
                if (socket != null) {
                    socket.emit("viewadd", object);
                }
            } catch (JSONException e) {
                Log.d(TAG, "addViewCount: err " + e.getMessage());
                e.printStackTrace();
            }

        }

        private void setupRemoteVideo(int uid) {
            Log.d(TAG, "setupRemoteVideo: ");
            ViewGroup parent = mRemoteContainer;

            if (mRemoteVideo != null) {
                Log.d(TAG, "setupRemoteVideo: mremote is null");
                return;
            }
            Log.d(TAG, "setupRemoteVideo: hash");
            SurfaceView view = RtcEngine.CreateRendererView(getBaseContext());

            parent.addView(view);
            mRemoteVideo = new VideoCanvas(view, VideoCanvas.RENDER_MODE_ADAPTIVE, uid);
            // Initializes the video view of a remote user.
            mRtcEngine.setupRemoteVideo(mRemoteVideo);
            Log.d(TAG, "setupRemoteVideo: added video");
        }

        private void run() {
            endActivityAndCall();
        }

        private void initTimer() {
            timerHandler.postDelayed(timerRunnable, 60000);
        }

        private void getoldComments() {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty(STR_TKN, tkn);
            Call<CommentRootOriginal> call = RetrofitBuilder_a.create().getOldComments(Const_a.DEVKEY, tkn);
            call.enqueue(new Callback<CommentRootOriginal>() {
                @Override
                public void onResponse(Call<CommentRootOriginal> call, Response<CommentRootOriginal> response) {
                    if (response.code() == 200 && response.body().getStatus() && !response.body().getData().isEmpty()) {
                        for (int i = 0; i < response.body().getData().size(); i++) {
                            JSONObject jsonObject = new JSONObject();
                            try {
                                jsonObject.put("name", response.body().getData().get(i).getName());
                                jsonObject.put(STR_CMT, response.body().getData().get(i).getComment());
                                commentAdapter.addComment(jsonObject);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }
                    }
                }

                @Override
                public void onFailure(Call<CommentRootOriginal> call, Throwable t) {
                    Log.d(TAG, "onFailure: getold cmt errr " + t.getMessage());
                }
            });
        }

        @Override
        public void onJoinChannelSuccess(String channel, final int uid, int elapsed) {
            uidAgora = uid;
            runOnUiThread(() -> {
                Log.d(TAG, "sssss=- run: joined chenal");
                getoldComments();
                initTimer();
                showFollowPopup();

                new Handler().postDelayed(() -> {
                    if (isVideoDecoded) {
                        Log.d(TAG, "sssss=- run: yreeeeeeehhhhh  video decoded");
                    } else {
                        deleteHostInform();
                    }
                }, 5000);
                //

            });
        }

        private void deleteHostInform() {

            Call<RestResponse> call = RetrofitBuilder_a.create().destoryHost(Const_a.DEVKEY, datum.getChannel());
            call.enqueue(new Callback<RestResponse>() {
                @Override
                public void onResponse(Call<RestResponse> call, Response<RestResponse> response) {
                    Log.d(TAG, "sssss=- onResponse: host destoried");
                    mCallEnd = true;


                    if (!isFinishing()) {
                        endCall();
                    }
//                    endCall();

                }

                @Override
                public void onFailure(Call<RestResponse> call, Throwable t) {
//ll
                }
            });

        }

        private void showFollowPopup() {
            Log.d(TAG, "showFollowPopup: ");
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("host_id", hostId);
            jsonObject.addProperty("guest_id", userId);
            Call<RestResponse> call = RetrofitBuilder_a.create().checkFollow(Const_a.DEVKEY, jsonObject);
            call.enqueue(new Callback<RestResponse>() {
                @Override
                public void onResponse(Call<RestResponse> call, Response<RestResponse> response) {
                    if (response.code() == 200 && !response.body().isStatus()) {
                        Log.d(TAG, "onResponse: is following false ");
                        new Handler().postDelayed(() -> {
                            Log.d(TAG, "run: 5 sec delayed");
                            if (isFinishing()) {
                                Log.d(TAG, "run: is finishing when popup");
                                return;
                            }

                            if (response.body().getMessage().equals("follow")) {
                                binding.btnfollow.setVisibility(View.GONE);
                            } else {
                                Animation pop = AnimationUtils.loadAnimation(WatchLiveActivity_a.this, R.anim.zoom);
                                binding.btnfollow.startAnimation(pop);
                                binding.btnfollow.setVisibility(View.VISIBLE);
                            }

                            binding.btnfollow.setOnClickListener(v -> {
                                ApiCalling_a apiCalling = new ApiCalling_a(WatchLiveActivity_a.this);
                                apiCalling.followUser(WatchLiveActivity_a.this, userId, hostId);
                                apiCalling.setResponseListnear(new ApiCalling_a.ResponseListnear() {
                                    @Override
                                    public void responseSuccess() {
                                        Log.d(TAG, "responseSuccess: followed");

                                        binding.btnfollow.setVisibility(View.GONE);
                                    }

                                    @Override
                                    public void responseFail() {
                                        binding.btnfollow.setVisibility(View.VISIBLE);
                                    }
                                });
                            });


                        }, 1000);
                    } else {
                        binding.btnfollow.setVisibility(View.GONE);
                    }
                }

                @Override
                public void onFailure(Call<RestResponse> call, Throwable t) {
                    Log.d("TAG", "onFailure: followroort" + t.getMessage());
                }
            });

        }

        @Override
        public void onFirstRemoteVideoDecoded(final int uid, int width, int height, int elapsed) {
            runOnUiThread(() -> {
                isVideoDecoded = true;
                Log.d(TAG, "sssss=- run: vide decode");
                setupRemoteVideo(uid);
                addViewCount();
            });
        }

        @Override
        public void onLeaveChannel(RtcStats stats) {
            super.onLeaveChannel(stats);
            LivexUtils_a.setCustomToast(WatchLiveActivity_a.this, "Video Ended!!");
        }

        @Override
        public void onUserOffline(final int uid, int reason) {
            Log.d(TAG, "sssss=- onUserOffline: watch");
            runOnUiThread(this::run);
        }

        @Override
        public void onError(int err) {
            super.onError(err);
            Log.d(TAG, "sssss=- onError: " + err);
            if (err == 109) {
                deleteHostInform();
            }
        }

        @Override
        public void onFirstLocalAudioFrame(int elapsed) {
            super.onFirstLocalAudioFrame(elapsed);
            Log.d(TAG, "sssss=- onFirstLocalAudioFrame: ");
        }

        @Override
        public void onMediaEngineLoadSuccess() {
            super.onMediaEngineLoadSuccess();
            Log.d(TAG, "sssss=- onMediaEngineLoadSuccess: ");
        }

        @Override
        public void onMediaEngineStartCallSuccess() {
            super.onMediaEngineStartCallSuccess();
            Log.d(TAG, "sssss=- onMediaEngineStartCallSuccess: ");
        }
    };
    private EmojiAdapter_a emojiAdapter;
    private ArrayList<EmojicategoryRoot.Datum> finelCategories;
    private BottomSheetDialog bottomSheetDialog;
    private String chatRoomId;

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

    private void openWalletActivity() {
        Toast.makeText(this, "Reacharge your Wallet First", Toast.LENGTH_SHORT).show();
        startActivity(new Intent(this, MyWalletActivity_a.class));
    }

    private void endActivityAndCall() {
        //
        timerHandler.removeCallbacks(timerRunnable);
        new Handler().postDelayed(() -> {
            Log.d(TAG, "run: user left");
            if (uidAgora != -1) {

                onRemoteUserLeft(uidAgora);
            }


            if (!isFinishing()) {
                endCall();
            }


        }, 2000);
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy: 1");
        mCallEnd = true;

        if (!isFinishing()) {
            endCall();
        }
//        endCall();
        BaseActivity_a.isUserBusyLocal = false;
        MySocketManager.getInstance().removeLiveListener(liveHandler);
        MySocketManager.getInstance().removeSocketListener(socketConnectHandler);
        Log.d(TAG, "onDestroy: 2");
        super.onDestroy();
    }

    private void initEngineAndJoinChannel() {
        // This is our usual steps for joining
        // a channel and starting a call.
        try {
            mRtcEngine = RtcEngine.create(this, sessionManager.getSetting().getAgoraId(), mRtcEventHandler);
            mRtcEngine.setClientRole(IRtcEngineEventHandler.ClientRole.CLIENT_ROLE_AUDIENCE);

        } catch (Exception e) {
            Log.e(TAG, Log.getStackTraceString(e));
            throw new RuntimeException("NEED TO check rtc sdk init fatal error\n" + Log.getStackTraceString(e));
        }

        mRtcEngine.enableVideo();

        // Please go to this page for detailed explanation
        // https://docs.agora.io/en/Video/API%20Reference/java/classio_1_1agora_1_1rtc_1_1_rtc_engine.html#af5f4de754e2c1f493096641c5c5c1d8f
        mRtcEngine.setVideoEncoderConfiguration(new VideoEncoderConfiguration(
                VideoEncoderConfiguration.VD_960x720,
                VideoEncoderConfiguration.FRAME_RATE.FRAME_RATE_FPS_15,
                VideoEncoderConfiguration.COMPATIBLE_BITRATE,
                VideoEncoderConfiguration.ORIENTATION_MODE.ORIENTATION_MODE_FIXED_PORTRAIT));

        mRtcEngine.setChannelProfile(Constants.CHANNEL_PROFILE_LIVE_BROADCASTING);
        /**In the demo, the default is to enter as the anchor.*/
        mRtcEngine.setClientRole(IRtcEngineEventHandler.ClientRole.CLIENT_ROLE_AUDIENCE);

        Log.i(TAG, "token " + tkn);

        String token = tkn;
        if (TextUtils.isEmpty(token) || TextUtils.equals(token, "#YOUR ACCESS TOKEN#")) {
            token = null; // default, no token
        }

        Log.i(TAG, "token " + token);

        mRtcEngine.joinChannel(token, chennal, "Extra Optional Data", 0);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == PERMISSION_REQ_ID) {

            if (grantResults[0] != PackageManager.PERMISSION_GRANTED ||
                    grantResults[1] != PackageManager.PERMISSION_GRANTED ||
                    grantResults[2] != PackageManager.PERMISSION_GRANTED || grantResults[3] != PackageManager.PERMISSION_GRANTED) {
                Log.e("TAG", "onRequestPermissionsResult: 11 ");

                showLongToast("Need permissions " + Manifest.permission.WRITE_EXTERNAL_STORAGE);
                return;
            }

            initEngineAndJoinChannel();
        } else {
            if (grantResults[0] != PackageManager.PERMISSION_GRANTED ||
                    grantResults[1] != PackageManager.PERMISSION_GRANTED ||
                    grantResults[2] != PackageManager.PERMISSION_GRANTED || grantResults[3] != PackageManager.PERMISSION_GRANTED || grantResults[4] != PackageManager.PERMISSION_GRANTED) {
                Log.e("TAG", "onRequestPermissionsResult: 22 ");

                showLongToast("Need permissions " +
                        "/" + Manifest.permission.WRITE_EXTERNAL_STORAGE);

                return;
            }
            initEngineAndJoinChannel();
        }
    }

    private void showLongToast(String s) {
        Toast.makeText(this, s, Toast.LENGTH_SHORT).show();
    }

    private void getHost() {
        ApiCalling_a apiCalling = new ApiCalling_a(WatchLiveActivity_a.this);
        apiCalling.getHostProfile(hostId, new ApiCalling_a.OnHostProfileGetListnear() {
            @Override
            public void onHostGet(HostRoot.Host user) {
                hostAgencyId = user.getAgencyId();
                binding.btnRepot.setVisibility(View.VISIBLE);
                binding.tvCoin.setText(String.valueOf(user.getCoin()));
            }

            @Override
            public void onFail() {

            }
        });
    }

    private void createChatRoom() {
        Log.d(TAG, "createChatRoom: ");
        ApiCalling_a apiCalling = new ApiCalling_a(WatchLiveActivity_a.this);
        apiCalling.createChatRoom(userId, hostId, new ApiCalling_a.OnRoomGenereteListnear() {
            @Override
            public void onRoomGenereted(String roomId) {
                chatRoomId = roomId;
            }

            @Override
            public void onFail() {
                Log.d(TAG, "onFail: chatroom crete faillll");
            }
        });
    }


    private void onRemoteUserLeft(int uid) {
        if (mRemoteVideo != null && mRemoteVideo.uid == uid) {
            removeFromParent(mRemoteVideo);
            // Destroys remote view
            mRemoteVideo = null;
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

    private void setupMyData() {
        int myCoin = sessionManager.getUser().getCoin();
        binding.bottomPage.tvUsereCoin.setText(String.valueOf(myCoin));
        binding.tvusercoins.setText(String.valueOf(myCoin));
        Log.d(TAG, "setupMyData: my coin " + myCoin);
        Log.d(TAG, "setupMyData: vid charge  " + sessionManager.getSetting().getUserCallCharge());
        if (myCoin <= sessionManager.getSetting().getUserCallCharge() * 3) {
            binding.lytLowBalance.lytmain.setVisibility(View.VISIBLE);
            initLowBalanceLayoutListner(binding.lytLowBalance);
        }

    }

    private void initUI() {
        mRemoteContainer = findViewById(R.id.remote_video_view_container);
        commentAdapter = new CommentAdapterOriginal_a();
        binding.rvComments.setAdapter(commentAdapter);


        Glide.with(getApplicationContext())
                .load(datum.getImage())
                .circleCrop()
                .into(binding.imgprofile);
        String s = String.valueOf(datum.getName().charAt(0)).toUpperCase();
        binding.tvName.setText(s.concat(datum.getName().substring(1)));


        totalCoins = sessionManager.getUser().getCoin();
        binding.tvusercoins.setText(String.valueOf(totalCoins));
        binding.tvCoin.setText(String.valueOf(datum.getCoin()));
        // Sample logs are optional.


        initListear();

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

                    setGiftList(finelCategories);


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
                        sendGift(coin, emoji);
                        updetUI(SHEET_CLOSE);
                    });
                }
            }

            private void setGiftList(List<EmojicategoryRoot.Datum> finelCategories) {
                Call<EmojiIconRoot> call1 = RetrofitBuilder_a.create().getEmojiByCategory(Const_a.DEVKEY, finelCategories.get(0).get_id());
                call1.enqueue(new Callback<EmojiIconRoot>() {
                    private void onEmojiClick(Bitmap bitmap, Long coin, EmojiIconRoot.Datum emoji) {
                        sendGift(coin, emoji);
                    }

                    @Override
                    public void onResponse(Call<EmojiIconRoot> call, Response<EmojiIconRoot> response) {
                        Log.d(TAG, "onResponse: emoji yes" + response.code());
                        if (response.code() == 200 && response.body().getStatus() && !response.body().getData().isEmpty()) {

                            emojiAdapter = new EmojiAdapter_a(response.body().getData());
                            binding.rvEmogi.setAdapter(emojiAdapter);
                            emojiAdapter.setOnEmojiClickListnear(this::onEmojiClick);


                        }
                    }

                    @Override
                    public void onFailure(Call<EmojiIconRoot> call, Throwable t) {
//ll
                    }
                });
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
                View v = LayoutInflater.from(WatchLiveActivity_a.this).inflate(R.layout.custom_tabgift, null);
                TextView tv = (TextView) v.findViewById(R.id.tvTab);
                tv.setText(datum.getName());
                ImageView img = (ImageView) v.findViewById(R.id.imagetab);

                Glide.with(getApplicationContext())
                        .load(BuildConfig.BASE_URL + datum.getIcon())
                        .placeholder(R.drawable.ic_gift)
                        .into(img);
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
            binding.rvComments.setVisibility(View.GONE);
            binding.rvEmogi.setVisibility(View.GONE);
            binding.lytbottom.setVisibility(View.GONE);
            binding.lytShare.setVisibility(View.GONE);
            binding.lytusercoin.setVisibility(View.GONE);
        } else {
            binding.bottomPage.lyt2.setVisibility(View.GONE);
            binding.rvComments.setVisibility(View.VISIBLE);
            binding.rvEmogi.setVisibility(View.VISIBLE);
            binding.lytbottom.setVisibility(View.VISIBLE);
            binding.lytShare.setVisibility(View.VISIBLE);
            binding.lytusercoin.setVisibility(View.VISIBLE);
        }
    }

    private boolean checkSelfPermission(String permission, int requestCode) {
        if (ContextCompat.checkSelfPermission(this, permission) !=
                PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, REQUESTED_PERMISSIONS, requestCode);
            return false;
        }

        return true;
    }

    public void onClickchat(View view) {

        Log.e(TAG, "onClickchat: >>>>>>>>>>>>>>>>>>>>>>> chatclick ");
        bottomSheetDialog = new BottomSheetDialog(this, R.style.customStyle);

        bottomSheetDialog = new BottomSheetDialog(WatchLiveActivity_a.this, R.style.CustomBottomSheetDialogTheme);
        bottomSheetDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        BottomSheetChatBinding bottomSheetChatBinding = DataBindingUtil.inflate(LayoutInflater.from(this), R.layout.bottom_sheet_chat, null, false);

        bottomSheetDialog.setContentView(bottomSheetChatBinding.getRoot());

        Glide.with(this).load(datum.getImage()).circleCrop().into(bottomSheetChatBinding.imgprofile);
        String title = "Hello " + sessionManager.getUser().getName() + " ,Send your Message to " + datum.getName() + "";
        bottomSheetChatBinding.tvtitle.setText(title);
        bottomSheetChatBinding.btnsend.setOnClickListener(v -> {
            bottomSheetDialog.dismiss();
            if (chatRoomId != null && !chatRoomId.equals("")) {
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("user_id", userId);
                jsonObject.addProperty("host_id", hostId);
                jsonObject.addProperty("sender", "user");
                jsonObject.addProperty("topic", chatRoomId);
                jsonObject.addProperty("message", bottomSheetChatBinding.etChat.getText().toString().trim());
                Call<ChatSendRoot> call = RetrofitBuilder_a.create().sendMessageToBackend(Const_a.DEVKEY, jsonObject);
                call.enqueue(new Callback<ChatSendRoot>() {
                    @Override
                    public void onResponse(Call<ChatSendRoot> call, Response<ChatSendRoot> response) {
                        if (response.code() == 200 && response.body().getStatus() && response.body().getData() != null) {
                            Log.d("TAG", "onResponse: sended msg success to backend");
                            bottomSheetChatBinding.etChat.setText("");
                            Toast.makeText(WatchLiveActivity_a.this, "Message Send Successfully", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<ChatSendRoot> call, Throwable t) {
                        Log.d("TAG", "onFailure: " + t.getMessage());

                    }
                });


            }


        });


        bottomSheetDialog.show();
    }

    public void onclickGiftIcon(View view) {
        updetUI(SHEET_OPEN);
        binding.bottomPage.btnclose.setOnClickListener(v -> updetUI(SHEET_CLOSE));
    }

    public void onClickClose(View view) {
        mCallEnd = true;

        if (!isFinishing()) {
            endCall();
        }
//        endCall();

    }

/*    @Override
    protected void onPause() {
        super.onPause();
        lessViewCount();
        mCallEnd = true;
        endCall();
    }*/

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "onStop: ");
//        mCallEnd = true;
//        endCall();
    }


    private void endCall() {
        Log.d(TAG, "endCall: 1");
        lessViewCount();
        timerHandler.removeCallbacks(timerRunnable);
        removeFromParent(mRemoteVideo);
        mRemoteVideo = null;
        if (mRtcEngine != null) {
            mRtcEngine.leaveChannel();
        }
        RtcEngine.destroy();
        Toast.makeText(this, "Live Ended", Toast.LENGTH_SHORT).show();
        finish();
        Log.d(TAG, "endCall: 2");
    }

    public void onClickReport(View view) {
        new BottomSheetReport_g(this, hostAgencyId, hostId, () -> {
            LayoutInflater inflater = getLayoutInflater();
            View layout = inflater.inflate(R.layout.toast_layout,
                    (ViewGroup) findViewById(R.id.customtoastlyt));

            Toast toast = new Toast(getApplicationContext());
            toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
            toast.setDuration(Toast.LENGTH_LONG);
            toast.setView(layout);
            toast.show();
        });
    }

    LiveHandler liveHandler = new LiveHandler() {
        @Override
        public void onSimpleFilter(Object[] args) {
            Log.d(TAG, "call: filterlistnear" + args.length);
            Log.d(TAG, "call: filterlistnear   " + args[0].toString());
            runOnUiThread(() -> {


                if (args[0] != null) {
                    runOnUiThread(() -> {


                        String filtertype = null;

                        filtertype = args[0].toString();
                        FilterRoot filterRoot = new Gson().fromJson(filtertype, FilterRoot.class);
                        if (filterRoot != null) {
                            if (filterRoot.getFilter() == 0) {
                                Log.d(TAG, "run: filter");
                                binding.imgFilter.setImageDrawable(null);
                            } else {
                                Log.d(TAG, "run: filteryes" + filterRoot.getFilter());
                                /* binding.imgFilter.setImageDrawable(ContextCompat.getDrawable(WatchLiveActivity_a.this, filterRoot.getFilter()));*/
                                Glide.with(WatchLiveActivity_a.this).load(FilterUtils.getDraw(filterRoot.getTitle())).into(binding.imgFilter);
                            }
                        }

                    });

                }

            });
        }

        @Override
        public void onGif(Object[] args) {
            runOnUiThread(() -> {

                if (args[0] != null) {
                    runOnUiThread(() -> {

                        String filtertype = null;

                        filtertype = args[0].toString();
                        GifRoot gifRoot = new Gson().fromJson(filtertype, GifRoot.class);

                        if (isFinishing()) return;

                        if (gifRoot != null) {
                            if (gifRoot.getFilter() == 0) {
                                binding.imgFilter2.setImageDrawable(null);
                            } else {
                                Log.d(TAG, "run: filteryes" + gifRoot.getTitle());
                                Glide.with(WatchLiveActivity_a.this).asGif().load(FilterUtils.getDraw(gifRoot.getTitle())).centerCrop().into(binding.imgFilter2);
                            }
                        }
                    });
                }
            });
        }

        @Override
        public void onComment(Object[] args) {
            Log.d(TAG, "call: listnerrrrmsg   " + args[0].toString());
            if (args[0] != null) {
                runOnUiThread(() -> {
                    try {
                        JSONObject response = (JSONObject) args[0];

                        commentAdapter.addComment(response);
                        binding.rvComments.scrollToPosition(commentAdapter.getItemCount() - 1);
                    } catch (Exception o) {
                        Log.d(TAG, "run: eooros" + o.getMessage());
                    }

                });

            }
        }

        @Override
        public void onGift(Object[] args) {

            if (args[0] != null) {
                runOnUiThread(() -> {
                    Log.d(TAG, "call: gifListnear" + args.length);
                    Log.d(TAG, "call: gifListnear   " + args[0].toString());
                    String filtertype = null;

                    filtertype = args[0].toString();
                    GiftRoot gift = new Gson().fromJson(filtertype, GiftRoot.class);
                    if (gift != null && !isFinishing()) {

                        Glide.with(WatchLiveActivity_a.this).load(BuildConfig.BASE_URL + gift.getImage()).into(binding.imgEmoji);
                        ScaleAnimation btnAnimation = new ScaleAnimation(1f, 0.8f, // Start and end values for the X axis scaling
                                1f, 0.8f,
                                Animation.RELATIVE_TO_SELF, 0.5f, // Pivot point of X scaling
                                Animation.RELATIVE_TO_SELF, 0.5f);
                        btnAnimation.setDuration(250); //1 second duration for each animation cycle
                        btnAnimation.setRepeatCount(Animation.REVERSE); //repeating indefinitely
                        btnAnimation.setFillAfter(true);
                        btnAnimation.setRepeatMode(Animation.REVERSE); //animation will start from end point once ended.
                        binding.imgEmoji.startAnimation(btnAnimation);
                        new Handler().postDelayed(() -> {
                            binding.imgEmoji.setImageDrawable(null);
                            btnAnimation.cancel();
                        }, 2500);

                        makeSound();

                    }

                });

            }
            if (args[1] != null) {  // user
                runOnUiThread(() -> {
                    Log.d(TAG, "giftReciveListnear : user data === " + args[1].toString());
                    User user = new Gson().fromJson(args[1].toString(), User.class);

                    if (sessionManager.getUser().getId().equals(user.getId())) {
                        sessionManager.saveUser(user);
                    }
                    binding.bottomPage.tvUsereCoin.setText(String.valueOf(sessionManager.getUser().getCoin()));
                    Log.d(TAG, "giftReciveListnear: user save thya che ===== ");
                });
            }

            if (args[2] != null) {   // host
                runOnUiThread(() -> {
                    Log.d(TAG, "giftReciveListnear : host data ==== " + args[2].toString());
                    HostRoot.Host host = new Gson().fromJson(args[2].toString(), HostRoot.Host.class);
                    binding.tvCoin.setText(String.valueOf(host.getCoin()));
                });
            }

        }

        @Override
        public void onView(Object[] args) {
            Log.d(TAG, "call: listnerrrr" + args.length);
            Log.d(TAG, "call: listnerrrrmsg   " + args[0].toString());

            if (args[0] != null) {
                runOnUiThread(() -> {
                    try {

                        binding.tvviews.setText(String.valueOf(args[0].toString()));
                        Log.d(TAG, "run: viewssss " + args[0].toString());
                    } catch (Exception o) {
                        Log.d(TAG, "run: eooros" + o.getMessage());
                    }

                });

            }
        }

        @Override
        public void onSticker(Object[] args) {
            Log.d(TAG, "call: gifListnear" + args.length);
            Log.d(TAG, "call: gifListnear   " + args[0].toString());
            runOnUiThread(() -> {

                binding.imgstiker.setVisibility(View.VISIBLE);
                if (args[0] != null) {
                    runOnUiThread(() -> {

                        String filtertype = null;

                        filtertype = args[0].toString();
                        StikerRoot.DataItem stiker = new Gson().fromJson(filtertype, StikerRoot.DataItem.class);
                        if (stiker != null) {

                            Glide.with(WatchLiveActivity_a.this).asGif().load(BuildConfig.BASE_URL + stiker.getSticker()).centerCrop().into(binding.imgstiker);
                            new Handler().postDelayed(() -> binding.imgstiker.setVisibility(View.GONE), 2500);
                        }

                    });

                }

            });
        }

        @Override
        public void onEmoji(Object[] args) {
            Log.d(TAG, "call: emiji" + args.length);
            Log.d(TAG, "call: emijoi   " + args[0].toString());
            runOnUiThread(() -> {


                if (args[0] != null) {
                    runOnUiThread(() -> {

                        String filtertype = null;

                        filtertype = args[0].toString();
                        HostEmojiRoot.DataItem emoji = new Gson().fromJson(filtertype, HostEmojiRoot.DataItem.class);
                        if (emoji != null) {
                            Glide.with(WatchLiveActivity_a.this).load(BuildConfig.BASE_URL + emoji.getEmoji()).centerCrop().into(binding.imgEmoji);

                            ScaleAnimation btnAnimation = new ScaleAnimation(1f, 0.8f, // Start and end values for the X axis scaling
                                    1f, 0.8f,
                                    Animation.RELATIVE_TO_SELF, 0.5f, // Pivot point of X scaling
                                    Animation.RELATIVE_TO_SELF, 0.5f);
                            btnAnimation.setDuration(250); //1 second duration for each animation cycle
                            btnAnimation.setRepeatCount(Animation.REVERSE); //repeating indefinitely
                            btnAnimation.setFillAfter(true);
                            btnAnimation.setRepeatMode(Animation.REVERSE); //animation will start from end point once ended.
                            binding.imgEmoji.startAnimation(btnAnimation);
                            new Handler().postDelayed(() -> {
                                binding.imgEmoji.setImageDrawable(null);
                                btnAnimation.cancel();
                            }, 2500);
                            makeSound();

                        }

                    });

                }

            });
        }

        @Override
        public void onRefresh(Object[] args) {
            Log.d("TAG", "call: refreshhh" + args.length);
            Log.d("TAG", "call: refreshhh   " + args[0].toString());
            runOnUiThread(() -> {

                setupMyData();
                getHost();
            });
        }

        @Override
        public void onEnded(Object[] args) {
            Log.d(TAG, "call: listnerrrrVideoEnd" + args.length);
            Log.d(TAG, "call: listnerrrrVidEnd   " + args[0].toString());

            if (args[0] != null) {
                timerHandler.removeCallbacks(timerRunnable);
                new Handler().postDelayed(() -> {
                    Log.d(TAG, "run: user left");
                    if (uidAgora != -1) {

                        onRemoteUserLeft(uidAgora);
                    }

                    if (!isFinishing()) {
                        endCall();
                    }
//                    endCall();

                }, 2000);
            }
        }

        @Override
        public void onGiftUser(Object[] args) {

        }
    };

    public void initLowBalanceLayoutListner(ItemNotificationLowBalanceBinding lytLowBalance) {
        int balance = sessionManager.getUser().getCoin();
        lytLowBalance.tvCoin.setText(String.valueOf(balance));
        lytLowBalance.btnClose.setOnClickListener(v -> lytLowBalance.lytmain.setVisibility(View.GONE));
        lytLowBalance.btnRecharge.setOnClickListener(v -> {

            if (!isFinishing()) {
                endCall();
            }
//            endCall();
            startActivity(new Intent(WatchLiveActivity_a.this, CoinPlanActivity_a.class).putExtra("isVip", false));
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
            runOnUiThread(() -> {
                new Handler().postDelayed(() -> {
                    JSONObject jsonObject = new JSONObject();
                    try {
                        jsonObject.put("id", sessionManager.getUser().getId());
                        jsonObject.put("roomId", liveStramingId);
                        socket.emit("socketReconnectedJoin", jsonObject);
                        Log.d(TAG, "onReconnected: " + jsonObject.toString());
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                }, 2000);
            });

        }
    };

}