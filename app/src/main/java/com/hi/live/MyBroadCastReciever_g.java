package com.hi.live;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.hi.live.retrofit.Const_a;

public class MyBroadCastReciever_g extends BroadcastReceiver {
    static int countPowerOff = 0;
    private Activity activity = null;

    public MyBroadCastReciever_g(Activity activity) {
        this.activity = activity;
    }


    @Override
    public void onReceive(Context context, Intent intent) {

        if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {

            SessionManager__a sessionManager = new SessionManager__a(context);
            if (sessionManager.getBooleanValue(Const_a.ISLOGIN)) {


            }


        } else if (intent.getAction().equals(Intent.ACTION_SCREEN_ON)) {
            Log.e("myBrodcastReciever", "Screen On");
        }


    }
}
