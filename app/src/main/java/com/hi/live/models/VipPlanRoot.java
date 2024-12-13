package com.hi.live.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class VipPlanRoot {

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
        @SerializedName("price")
        private double price;
        @SerializedName("createdAt")
        private String createdAt;
        @SerializedName("productId")
        private String productid;
        @SerializedName("__v")
        private int V;
        @SerializedName("discount")
        private int discount;
        @SerializedName("_id")
        private String id;
        @SerializedName("time")
        private String time;
        @SerializedName("updatedAt")
        private String updatedAt;
        @SerializedName("paymentGateway")
        private String paymentGateway;

        public double getPrice() {
            return price;
        }

        public void setPrice(double price) {
            this.price = price;
        }

        public String getProductid() {
            return productid;
        }

        public void setProductid(String productid) {
            this.productid = productid;
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

        public int getDiscount() {
            return discount;
        }

        public void setDiscount(int discount) {
            this.discount = discount;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getTime() {
            return time;
        }

        public void setTime(String time) {
            this.time = time;
        }

        public String getUpdatedAt() {
            return updatedAt;
        }

        public void setUpdatedAt(String updatedAt) {
            this.updatedAt = updatedAt;
        }

        public String getPaymentGateway() {
            return paymentGateway;
        }

        public void setPaymentGateway(String paymentGateway) {
            this.paymentGateway = paymentGateway;
        }
    }
}