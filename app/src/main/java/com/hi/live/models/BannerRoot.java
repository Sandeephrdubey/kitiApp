package com.hi.live.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class BannerRoot {

    @SerializedName("banner")
    private List<BannerItem> banner;

    @SerializedName("message")
    private String message;

    @SerializedName("status")
    private boolean status;

    public List<BannerItem> getBanner() {
        return banner;
    }

    public String getMessage() {
        return message;
    }

    public boolean isStatus() {
        return status;
    }

    public static class BannerItem {

        @SerializedName("image")
        private String image;

        @SerializedName("createdAt")
        private String createdAt;

        @SerializedName("__v")
        private int V;

        @SerializedName("link")
        private String link;

        @SerializedName("_id")
        private String id;

        @SerializedName("updatedAt")
        private String updatedAt;

        public String getImage() {
            return image;
        }

        public String getCreatedAt() {
            return createdAt;
        }

        public int getV() {
            return V;
        }

        public String getLink() {
            return link;
        }

        public String getId() {
            return id;
        }

        public String getUpdatedAt() {
            return updatedAt;
        }
    }
}