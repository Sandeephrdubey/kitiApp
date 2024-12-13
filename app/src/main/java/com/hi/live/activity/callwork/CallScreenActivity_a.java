package com.hi.live.activity.callwork;

import android.content.Context;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Log;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.databinding.DataBindingUtil;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.hi.live.R;
import com.hi.live.activity.BaseActivity_a;
import com.hi.live.databinding.ActivityCallScreenBinding;
import com.hi.live.oflineModels.CallAnswerRoot;
import com.hi.live.oflineModels.VideoCallDataRoot;
import com.hi.live.socket.CallHandler;
import com.hi.live.socket.MySocketManager;

import java.io.IOException;

import io.socket.client.Socket;

public class CallScreenActivity_a extends BaseActivity_a {
    private static final String TAG = "callscr";
    public String CallId;
    ActivityCallScreenBinding binding;
    Handler handler = new Handler();
    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            binding.btnDecline.performClick();
        }
    };
    private VideoCallDataRoot videoCallDataRoot;
    private Socket socket;
    private Vibrator v;
    private MediaPlayer player2;
    private boolean iscalled = false;

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

            if (v != null) {
                v.cancel();
            }
            if (player2 != null) {
                player2.release();
            }
            finish();

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


    private void initUI() {

        v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
// Vibrate for 500 milliseconds
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            long[] pattern = {100, 200, 400, 500, 100, 200, 300, 400, 500, 100, 200, 300, 400, 500, 500, 200, 100, 500, 500, 500};
            v.vibrate(VibrationEffect.createWaveform(pattern, VibrationEffect.DEFAULT_AMPLITUDE));
        } else {
            v.vibrate(500);
        }

        try {

            player2 = new MediaPlayer();
            try {
                AssetFileDescriptor afd2 = getAssets().openFd("call.mp3");
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

        if (!isFinishing()) {
            Glide.with(this).load(videoCallDataRoot.getHostImage()).circleCrop().into(binding.imageview);
        }
        binding.tvName.setText(videoCallDataRoot.getHostName());
//kjj
        binding.btnAccept.setOnClickListener(v -> {
            if (sessionManager.getUser().getCoin() >= sessionManager.getSetting().getUserCallCharge()) {
                if (!isFinishing()) {
                    handler.removeCallbacks(runnable);
                    iscalled = true;
                    fireEvent(true,binding.btnAccept);
                }
            } else {
                if (!isFinishing()) {
                    Toast.makeText(this, "Insufficient Coins.", Toast.LENGTH_SHORT).show();
                    binding.btnDecline.performClick();
                }
            }
        });
        binding.btnDecline.setOnClickListener(v -> {
            handler.removeCallbacks(runnable);
            fireEvent(false,binding.btnDecline);
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_call_screen);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        socket = MySocketManager.getInstance().getSocet();
        MySocketManager.getInstance().addCallListener(callHandler);
        isActOpened = false;
        Intent intent = getIntent();
        BaseActivity_a.isCallIncoming = true;
        if (intent != null) {
            String onjStr = intent.getStringExtra("datastr");
            Log.d(TAG, "onCreate: intent " + onjStr);
            if (!onjStr.equals("")) {
                videoCallDataRoot = new Gson().fromJson(onjStr, VideoCallDataRoot.class);
                Log.d(TAG, "onCreate: videoCallDataRoot.getCallId() ==  " + videoCallDataRoot.getCallId());
                CallId = videoCallDataRoot.getCallId();
                handler.postDelayed(runnable, 5000);
                initUI();


            }
        }

    }

    private void fireEvent(boolean isAccepted, ImageView btnAccept) {
        if (!isFinishing()) {
            btnAccept.setEnabled(false);
            CallAnswerRoot callAnswerRoot = new CallAnswerRoot();
            callAnswerRoot.setChannel(videoCallDataRoot.getChannel());
            callAnswerRoot.setClientId(videoCallDataRoot.getClientId());
            callAnswerRoot.setHostId(videoCallDataRoot.getHostId());
            callAnswerRoot.setToken(videoCallDataRoot.getToken());
            callAnswerRoot.setCallId(videoCallDataRoot.getCallId());
            callAnswerRoot.setAccepted(isAccepted);
            socket.emit("callAnswer", new Gson().toJson(callAnswerRoot));
            if (isAccepted) {
                startActivity(new Intent(this, VideoCallActivity_a.class).putExtra("datastr", new Gson().toJson(videoCallDataRoot)).putExtra("callId", CallId));
            }
            if (v != null) {
                v.cancel();
            }
            if (player2 != null) {
                player2.release();
            }
            btnAccept.setEnabled(true);
            finish();
        }
    }

    @Override
    protected void onPause() {
        handler.removeCallbacks(runnable);
        handler.removeCallbacksAndMessages(null);
        if (v != null) {
            v.cancel();
        }
        if (player2 != null) {
            player2.release();
        }
        BaseActivity_a.isCallIncoming = false;
        super.onPause();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(runnable);
        handler.removeCallbacksAndMessages(null);
        MySocketManager.getInstance().removeCallListener(callHandler);
    }
}