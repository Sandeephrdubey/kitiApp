package com.hi.live.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class PlanRoot {

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
        @SerializedName("rupee")
        private double rupee;
        @SerializedName("productId")
        private String googleProductId;
        @SerializedName("createdAt")
        private String createdAt;
        @SerializedName("__v")
        private int V;
        @SerializedName("_id")
        private String id;
        @SerializedName("coin")
        private int coin;
        @SerializedName("updatedAt")
        private String updatedAt;

        public double getRupee() {
            return rupee;
        }

        public void setRupee(double rupee) {
            this.rupee = rupee;
        }

        public String getGoogleProductId() {
            return googleProductId;
        }

        public void setGoogleProductId(String googleProductId) {
            this.googleProductId = googleProductId;
        }

        public String getCreatedAt() {
            return createdAt;
        }

        public void setCreatedAt(String createdAt) {
            this.createdAt = createdAt;
        }

        public int getV() {
            return V;
        }

        public void setV(int v) {
            V = v;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public int getCoin() {
            return coin;
        }

        public void setCoin(int coin) {
            this.coin = coin;
        }

        public String getUpdatedAt() {
            return updatedAt;
        }

        public void setUpdatedAt(String updatedAt) {
            this.updatedAt = updatedAt;
        }
    }
}