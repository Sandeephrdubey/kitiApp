package com.hi.live;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.hi.live.models.ChatUserListRoot;
import com.hi.live.models.RestResponse;
import com.hi.live.models.Thumb;
import com.hi.live.retrofit.Const_a;
import com.hi.live.retrofit.RetrofitBuilder_a;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LivexUtils_a {
    private static final String TAG = "livexutils";
    private static final String STR_INDIA = "INDIA";

    public static void setCustomToast(Context context, String s) {
        Toast.makeText(context, s, Toast.LENGTH_SHORT).show();
    }

    public static List<String> getNames() {
        List<String> names = new ArrayList<>();
        names.add("Nayan");
        names.add("Babu");
        names.add("Ramesh");
        names.add("Nayan");
        names.add("Prem");
        names.add("Raja");
        names.add("Vikrant");

        return names;
    }

    public static List<String> getComments() {
        List<String> names = new ArrayList<>();
        names.add("Hello ji");
        names.add("Heyy!!");
        names.add("I love you");
        names.add("you are so cute");
        names.add("7899044356 ye mera number he");
        names.add("Aap kaha se ho?");
        names.add("hello ji ");

        return names;
    }

    public static String getIPAddress(boolean useIPv4) {
        try {
            List<NetworkInterface> interfaces = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface intf : interfaces) {
                List<InetAddress> addrs = Collections.list(intf.getInetAddresses());
                for (InetAddress addr : addrs) {
                    if (!addr.isLoopbackAddress()) {
                        String sAddr = addr.getHostAddress().toUpperCase();
                        //   boolean isIPv4 = InetAddressUtils.isIPv4Address(sAddr);
                        boolean isIPv4 = sAddr.indexOf(':') < 0;
                        if (useIPv4) {
                            if (isIPv4)
                                return sAddr;
                        } else {
                            if (!isIPv4) {
                                int delim = sAddr.indexOf('%'); // drop ip6 port suffix
                                return delim < 0 ? sAddr : sAddr.substring(0, delim);
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public static void setProfileVisit(String hostId, String userId) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("user_id", userId);
        jsonObject.addProperty("host_id", hostId);
        Call<RestResponse> call = RetrofitBuilder_a.create().profileVisit(Const_a.DEVKEY, jsonObject);
        call.enqueue(new Callback<RestResponse>() {
            @Override
            public void onResponse(Call<RestResponse> call, Response<RestResponse> response) {
//ll
            }

            @Override
            public void onFailure(Call<RestResponse> call, Throwable t) {
//ll
            }
        });

    }

    public static void addMissedCallNotify(String hostId, String userId) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("user_id", userId);
        jsonObject.addProperty("host_id", hostId);
        Call<RestResponse> call = RetrofitBuilder_a.create().missCall(Const_a.DEVKEY, jsonObject);
        call.enqueue(new Callback<RestResponse>() {
            @Override
            public void onResponse(Call<RestResponse> call, Response<RestResponse> response) {
//ll
                Log.d(TAG, "onResponse: misscall sendecd");
            }

            @Override
            public void onFailure(Call<RestResponse> call, Throwable t) {
//ll
            }
        });

    }
}
