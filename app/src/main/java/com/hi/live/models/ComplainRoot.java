package com.hi.live.models;

import android.text.format.DateUtils;

import com.google.gson.annotations.SerializedName;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class ComplainRoot {

    @SerializedName("data")
    private List<DataItem> data;

    @SerializedName("message")
    private String message;

    @SerializedName("status")
    private boolean status;

    public List<DataItem> getData() {
        return data;
    }

    public String getMessage() {
        return message;
    }

    public boolean isStatus() {
        return status;
    }

    public static class DataItem {

        @SerializedName("image")
        private String image;

        @SerializedName("createdAt")
        private String createdAt;

        @SerializedName("user_id")
        private String userId;

        @SerializedName("contact")
        private String contact;

        @SerializedName("__v")
        private int V;

        @SerializedName("agency_id")
        private Object agencyId;

        @SerializedName("solved")
        private boolean solved;

        @SerializedName("_id")
        private String id;

        @SerializedName("message")
        private String message;

        @SerializedName("host_id")
        private Object hostId;

        @SerializedName("updatedAt")
        private String updatedAt;

        public String getImage() {
            return image;
        }

        public String getCreatedAt() {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.ENGLISH);
            sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
            try {
                long time = sdf.parse(createdAt).getTime();
                long now = System.currentTimeMillis();
                CharSequence ago =
                        DateUtils.getRelativeTimeSpanString(time, now, DateUtils.MINUTE_IN_MILLIS);

                return ago.toString();
            } catch (ParseException e) {
                e.printStackTrace();
            }
            return createdAt;
        }

        public String getUserId() {
            return userId;
        }

        public String getContact() {
            return contact;
        }

        public int getV() {
            return V;
        }

        public Object getAgencyId() {
            return agencyId;
        }

        public boolean isSolved() {
            return solved;
        }

        public String getId() {
            return id;
        }

        public String getMessage() {
            return message;
        }

        public Object getHostId() {
            return hostId;
        }

        public String getUpdatedAt() {
            return updatedAt;
        }
    }
}