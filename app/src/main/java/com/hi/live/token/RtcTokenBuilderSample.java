package com.hi.live.token;


import android.util.Log;

public class RtcTokenBuilderSample {

    static String channelName = "channel1";
    static String userAccount = "2082341273";
    static int uid = 2082341273;
    static int expirationTimeInSeconds = 3600;

    public static String main(String appId, String appCertificate, String chennalName) throws Exception {
        RtcTokenBuilder token = new RtcTokenBuilder();
        int timestamp = (int) (System.currentTimeMillis() / 1000 + expirationTimeInSeconds);
        String result = token.buildTokenWithUserAccount(appId, appCertificate,
                chennalName, "0", RtcTokenBuilder.Role.Role_Publisher, timestamp);
        System.out.println(result);
        Log.d("liveact", "main: tkn == " + result);
        return result;


    }
}
