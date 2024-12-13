package com.hi.live.models;

import android.text.format.DateUtils;

import com.google.gson.annotations.SerializedName;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class CallHistoryListRoot {

    @SerializedName("CallHistory")
    private List<CallHistoryItem> callHistory;

    @SerializedName("message")
    private String message;

    @SerializedName("status")
    private boolean status;

    public List<CallHistoryItem> getCallHistory() {
        return callHistory;
    }

    public String getMessage() {
        return message;
    }

    public boolean isStatus() {
        return status;
    }

    public static class CallHistoryItem {

        @SerializedName("date")
        private String date;

        @SerializedName("image")
        private String image;

        @SerializedName("user_id")
        private String userId;

        @SerializedName("name")
        private String name;

        @SerializedName("_id")
        private String id;

        @SerializedName("time")
        private String time;

        @SerializedName("type")
        private String type;

        @SerializedName("host_id")
        private String hostId;

        @SerializedName("coin")
        private int coin;

        public String getDate() {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.ENGLISH);
            sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
            try {
                long time = sdf.parse(date).getTime();
                long now = System.currentTimeMillis();
                CharSequence ago =
                        DateUtils.getRelativeTimeSpanString(time, now, DateUtils.MINUTE_IN_MILLIS);

                return ago.toString();
            } catch (ParseException e) {
                e.printStackTrace();
            }
            return date;
        }

        public String getImage() {
            return image;
        }

        public String getUserId() {
            return userId;
        }

        public String getName() {
            return name;
        }

        public String getId() {
            return id;
        }

        public String getTime() {
            return time;
        }

        public String getType() {
            return type;
        }

        public String getHostId() {
            return hostId;
        }

        public int getCoin() {
            return coin;
        }
    }
}