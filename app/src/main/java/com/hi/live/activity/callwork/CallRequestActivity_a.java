package com.hi.live.activity.callwork;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.databinding.DataBindingUtil;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.hi.live.LivexUtils_a;
import com.hi.live.R;
import com.hi.live.activity.BaseActivity_a;
import com.hi.live.databinding.ActivityCallRequestBinding;
import com.hi.live.models.CallCreateRoot;
import com.hi.live.oflineModels.CallAnswerRoot;
import com.hi.live.oflineModels.VideoCallDataRoot;
import com.hi.live.retrofit.Const_a;
import com.hi.live.socket.CallHandler;
import com.hi.live.socket.MySocketManager;
import com.hi.live.socket.SocketConst;
import com.hi.live.token.RtcTokenBuilderSample;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import io.socket.client.Socket;

public class CallRequestActivity_a extends BaseActivity_a {
    private static final String TAG = "CallRequestActivity_a";
    ActivityCallRequestBinding binding;
    private MediaPlayer mediaPlayer;
    private String hostName;
    Handler handler = new Handler();
    private String callId;
    private VideoCallDataRoot videoCallDataRoot = new VideoCallDataRoot();
    private Vibrator v;
    private String hostID;
    Runnable runnable = () -> {
        Log.d(TAG, "runnable : runnable onBackPressed   ===== ");
        if (!isFinishing()) {
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("userId", sessionManager.getUser().getId());
                jsonObject.put("hostId", hostID);
                jsonObject.put("callId", videoCallDataRoot.getCallId());
                jsonObject.put("type", "user");
                MySocketManager.getInstance().getSocet().emit(SocketConst.EVENT_CALL_CANCEL, jsonObject);
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
            Toast.makeText(this, hostName + " didn't answer your call.", Toast.LENGTH_SHORT).show();
            if (videoCallDataRoot != null) {
                LivexUtils_a.addMissedCallNotify(videoCallDataRoot.getHostId(), sessionManager.getUser().getId());
            }
            onBackPressed();
        }
    };
    private boolean isCalled = false;
    private boolean isFake;

    Socket socket;
    private CallCreateRoot.CallId callRoot;

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
        public void onCallAnswer(Object[] args1) {
            Log.d("TAG", "lisenForEvent: " + args1[0].toString());
            CallAnswerRoot callAnswerRoot = new Gson().fromJson(args1[0].toString(), CallAnswerRoot.class);
            if (callAnswerRoot != null) {

                Log.d("TAG", "lisenForEvent: hostid by data" + callAnswerRoot.getHostId());
                Log.d("TAG", "lisenForEvent: hostid by videocallroot " + videoCallDataRoot.getHostId());
                Log.d("TAG", "lisenForEvent:  isaccepted" + callAnswerRoot.isAccepted());

                handler.removeCallbacks(runnable);
                if (callAnswerRoot.isAccepted()) {
                    if (isCalled) {
                        isCalled = false;
                        videoCallDataRoot.setRate(callAnswerRoot.getRate());  // for add onother user rate
                        videoCallDataRoot.setCallId(callAnswerRoot.getCallId());
                        if (!isFinishing()) {
                            startActivity(new Intent(CallRequestActivity_a.this, VideoCallActivity_a.class).putExtra("datastr", new Gson().toJson(videoCallDataRoot)).putExtra("callId", callId));
                        }
                    }
                } else {
                    runOnUiThread(() -> {
                        Toast.makeText(CallRequestActivity_a.this, "Declined ", Toast.LENGTH_SHORT).show();
                    });
                }
                v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                v.vibrate(500);
                finish();
            } else {
                Log.d("TAG", "lisenForEvent: obj is null");
            }

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
    private boolean isPrivate = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_call_request);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        socket = MySocketManager.getInstance().getSocet();
        MySocketManager.getInstance().addCallListener(callHandler);
        Intent intent = getIntent();
        if (intent != null) {
            String callRootStr = intent.getStringExtra(Const_a.CALL_DATA);
            boolean isFromList = intent.getBooleanExtra(Const_a.IS_FROM_LIST, false);
            isPrivate = intent.getBooleanExtra(Const_a.IS_PRIVATE, false);
            if (isFromList) {
                callRoot = new Gson().fromJson(callRootStr, CallCreateRoot.CallId.class);
                initUI(callRoot.getHostId().getImage(), callRoot.getHostId().getName());
                isCalled = true;
                hostName = callRoot.getHostId().getName();
                hostID = callRoot.getHostId().getId();
                handler.postDelayed(runnable, 10000);
                emitCallEvent(callRoot.getHostId().getImage(), callRoot.getHostId().getName(), callRoot.getHostId().getId(), callRoot.getId());
                lisenForEvent(callRoot.getHostId().getId());
            } else {
                videoCallDataRoot = new Gson().fromJson(callRootStr, VideoCallDataRoot.class);
                Log.d(TAG, "onCreate:  videoCallDataRoot.toString() isFromChat ===== " + videoCallDataRoot.toString());
                initUI(videoCallDataRoot.getHostImage(), videoCallDataRoot.getHostName());
                isCalled = true;
                hostName = videoCallDataRoot.getHostName();
                hostID = videoCallDataRoot.getHostId();
                handler.postDelayed(runnable, 10000);
                emitCallEvent(videoCallDataRoot.getHostImage(), videoCallDataRoot.getHostName(), videoCallDataRoot.getHostId(), videoCallDataRoot.getCallId());
                lisenForEvent(videoCallDataRoot.getHostId());
            }
        }


    }

    private void emitCallEvent(String hostImage, String hostName, String hostId, String callId) {
//        if (isPrivate) {
            Log.d(TAG, "emitCallEvent: callId == " + callId);
            try {
                String tkn = RtcTokenBuilderSample.main(sessionManager.getSetting().getAgoraId(), sessionManager.getSetting().getAgoraCertificate(), callId);
                videoCallDataRoot.setHostCall(false);
                videoCallDataRoot.setHostName(hostName);
                videoCallDataRoot.setChannel(callId);
                videoCallDataRoot.setClientId(sessionManager.getUser().getId());
                videoCallDataRoot.setHostId(hostId);
                videoCallDataRoot.setToken(tkn);
                videoCallDataRoot.setClientImage(sessionManager.getUser().getImage());
                videoCallDataRoot.setHostImage(hostImage);
                videoCallDataRoot.setClientName(sessionManager.getUser().getName());
                videoCallDataRoot.setCallId(callId);
                socket.emit("call", new Gson().toJson(videoCallDataRoot));
                Log.d(TAG, "emitCallEvent: videoCallDataRoot = " + videoCallDataRoot.toString());
                Log.d(TAG, "emitCallEvent: videoCallDataRoot.getToken() ==  " + videoCallDataRoot.getToken());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
//        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.release();
        }
        MySocketManager.getInstance().removeCallListener(callHandler);
    }

    @Override
    public void onBackPressed() {
        if (mediaPlayer != null) {
            mediaPlayer.release();
        }
        if (v != null) {
            v.cancel();
        }
        if (!isFinishing()) {
            super.onBackPressed();
        }


    }

    @Override
    protected void onPause() {
        if (mediaPlayer != null) {
            mediaPlayer.release();
        }
        if (v != null) {
            v.cancel();
        }
        super.onPause();
    }

    @SuppressLint("SetTextI18n")
    private void initUI(String image, String name) {
        if (!isFinishing()) {
            Glide.with(this).load(sessionManager.getUser().getImage()).circleCrop().into(binding.image1);
            Glide.with(this).load(image).circleCrop().into(binding.image2);
            binding.tvName.setText("Waiting for " + name + "'s Reply ....");
            mediaPlayer = new MediaPlayer();
            try {
                AssetFileDescriptor assetFileDescriptor = getAssets().openFd("call_ringing.mp3");
                mediaPlayer.setDataSource(assetFileDescriptor.getFileDescriptor(), assetFileDescriptor.getStartOffset(), assetFileDescriptor.getLength());
                mediaPlayer.prepare();
                mediaPlayer.start();
                mediaPlayer.setLooping(true);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

    }

    private void lisenForEvent(String hostId) {
        binding.btnDecline.setOnClickListener(v1 -> {
            handler.removeCallbacks(runnable);
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("userId", sessionManager.getUser().getId());
                jsonObject.put("hostId", hostId);
                jsonObject.put("callId", videoCallDataRoot.getCallId());
                jsonObject.put("type", "user");
                socket.emit(SocketConst.EVENT_CALL_CANCEL, jsonObject);
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
            onBackPressed();
        });
    }
}