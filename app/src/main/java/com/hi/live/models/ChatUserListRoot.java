package com.hi.live.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ChatUserListRoot {

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
        boolean isFake = false;

        public DataItem(String countryName, boolean isFake, String name, String image, String time, String message) {
            this.image = image;
            this.isFake = isFake;
            this.sender = sender;
            this.name = name;
            this.countryName = countryName;
            this.topic = topic;
            this.id = id;
            this.time = time;
            this.message = message;
        }

        public DataItem() {
        }

        public boolean isFake() {
            return isFake;
        }

        public void setFake(boolean fake) {
            isFake = fake;
        }

        @SerializedName("image")
        private String image;

        @SerializedName("createdAt")
        private String createdAt;

        @SerializedName("sender")
        private String sender;

        @SerializedName("name")
        private String name;

        @SerializedName("country_name")
        private String countryName;

        @SerializedName("topic")
        private String topic;

        @SerializedName("_id")
        private String id;

        @SerializedName("time")
        private String time;

        @SerializedName("message")
        private String message;

        public String getImage() {
            return image;
        }

        public String getCreatedAt() {
            return createdAt;
        }

        public String getSender() {
            return sender;
        }

        public String getName() {
            return name;
        }

        public String getCountryName() {
            return countryName;
        }

        public String getTopic() {
            return topic;
        }

        public String getId() {
            return id;
        }

        public String getTime() {
            return time;
        }

        public String getMessage() {
            return message;
        }
    }
}