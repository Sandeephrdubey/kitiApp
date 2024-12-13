package com.hi.live.models;

import com.google.gson.annotations.SerializedName;

public class ChatTopicRoot {

    @SerializedName("data")
    private Data data;

    @SerializedName("message")
    private String message;

    @SerializedName("status")
    private boolean status;

    public Data getData() {
        return data;
    }

    public String getMessage() {
        return message;
    }

    public boolean isStatus() {
        return status;
    }

    public static class Data {

        @SerializedName("createdAt")
        private String createdAt;

        @SerializedName("user_id")
        private String userId;

        @SerializedName("__v")
        private int V;

        @SerializedName("_id")
        private String id;

        @SerializedName("host_id")
        private String hostId;

        @SerializedName("updatedAt")
        private String updatedAt;

        public String getCreatedAt() {
            return createdAt;
        }

        public String getUserId() {
            return userId;
        }

        public int getV() {
            return V;
        }

        public String getId() {
            return id;
        }

        public String getHostId() {
            return hostId;
        }

        public String getUpdatedAt() {
            return updatedAt;
        }
    }
}