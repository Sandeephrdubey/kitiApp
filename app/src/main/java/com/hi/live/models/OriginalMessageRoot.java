package com.hi.live.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class OriginalMessageRoot {

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

        @Override
        public String toString() {
            return "DataItem{" +
                    "createdAt='" + createdAt + '\'' +
                    ", userId='" + userId + '\'' +
                    ", sender='" + sender + '\'' +
                    ", V=" + V +
                    ", topic='" + topic + '\'' +
                    ", id='" + id + '\'' +
                    ", message='" + message + '\'' +
                    ", hostId='" + hostId + '\'' +
                    ", updatedAt='" + updatedAt + '\'' +
                    '}';
        }

        @SerializedName("createdAt")
        private String createdAt;
        @SerializedName("user_id")
        private String userId;
        @SerializedName("sender")
        private String sender;
        @SerializedName("__v")
        private int V;
        @SerializedName("topic")
        private String topic;
        @SerializedName("_id")
        private String id;
        @SerializedName("message")
        private String message;
        @SerializedName("host_id")
        private String hostId;
        @SerializedName("updatedAt")
        private String updatedAt;

        public String getCreatedAt() {
            return createdAt;
        }

        public void setCreatedAt(String createdAt) {
            this.createdAt = createdAt;
        }

        public String getUserId() {
            return userId;
        }

        public void setUserId(String userId) {
            this.userId = userId;
        }

        public String getSender() {
            return sender;
        }

        public void setSender(String sender) {
            this.sender = sender;
        }

        public int getV() {
            return V;
        }

        public void setV(int v) {
            V = v;
        }

        public String getTopic() {
            return topic;
        }

        public void setTopic(String topic) {
            this.topic = topic;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public String getHostId() {
            return hostId;
        }

        public void setHostId(String hostId) {
            this.hostId = hostId;
        }

        public String getUpdatedAt() {
            return updatedAt;
        }

        public void setUpdatedAt(String updatedAt) {
            this.updatedAt = updatedAt;
        }
    }
}