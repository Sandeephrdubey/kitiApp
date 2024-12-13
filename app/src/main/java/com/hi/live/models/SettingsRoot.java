package com.hi.live.models;

import com.google.gson.annotations.SerializedName;

public class SettingsRoot {

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

        @SerializedName("hostCharge")
        private int hostCharge;

        @SerializedName("chatCharge")
        private int chatCharge;
        @SerializedName("userLiveStreamingCharge")
        private int userLiveStreamingCharge;
        @SerializedName("dailyTaskMaxValue")
        private int dailyTaskMaxValue;
        @SerializedName("stripeSwitch")
        private boolean stripeSwitch;
        @SerializedName("loginBonus")
        private int loginBonus;
        @SerializedName("createdAt")
        private String createdAt;
        @SerializedName("streamingMinValue")
        private int streamingMinValue;
        @SerializedName("googlePaySwitch")
        private boolean googlePaySwitch;
        @SerializedName("razorPayId")
        private String razorPayId;
        @SerializedName("agoraCertificate")
        private String agoraCertificate;
        @SerializedName("__v")
        private int V;
        @SerializedName("howManyCoins")
        private int howManyCoins;
        @SerializedName("razorPaySwitch")
        private boolean razorPaySwitch;
        @SerializedName("redeemGateway")
        private String redeemGateway;
        @SerializedName("googlePayId")
        private String googlePayId;
        @SerializedName("currency")
        private String currency;
        @SerializedName("streamingMaxValue")
        private int streamingMaxValue;
        @SerializedName("updatedAt")
        private String updatedAt;
        @SerializedName("agoraId")
        private String agoraId;
        @SerializedName("minPoints")
        private int minPoints;
        @SerializedName("stripePublishableKey")
        private String stripePublishableKey;
        @SerializedName("stripeSecreteKey")
        private String stripeSecreteKey;
        @SerializedName("dailyTaskMinValue")
        private int dailyTaskMinValue;
        @SerializedName("userCallCharge")
        private int userCallCharge;
        @SerializedName("toCurrency")
        private int toCurrency;
        @SerializedName("stripeId")
        private String stripeId;
        @SerializedName("_id")
        private String id;
        @SerializedName("policyLink")
        private String policyLink;

        public int getChatCharge() {
            return chatCharge;
        }

        public int getHostCharge() {
            return hostCharge;
        }

        public int getUserLiveStreamingCharge() {
            return userLiveStreamingCharge;
        }

        public int getDailyTaskMaxValue() {
            return dailyTaskMaxValue;
        }

        public boolean isStripeSwitch() {
            return stripeSwitch;
        }

        public int getLoginBonus() {
            return loginBonus;
        }

        public String getCreatedAt() {
            return createdAt;
        }

        public int getStreamingMinValue() {
            return streamingMinValue;
        }

        public boolean isGooglePaySwitch() {
            return googlePaySwitch;
        }

        public String getRazorPayId() {
            return razorPayId;
        }

        public String getAgoraCertificate() {
            return agoraCertificate;
        }

        public int getV() {
            return V;
        }

        public int getHowManyCoins() {
            return howManyCoins;
        }

        public boolean isRazorPaySwitch() {
            return razorPaySwitch;
        }

        public String getRedeemGateway() {
            return redeemGateway;
        }

        public String getGooglePayId() {
            return googlePayId;
        }

        public String getCurrency() {
            return currency;
        }

        public int getStreamingMaxValue() {
            return streamingMaxValue;
        }

        public String getUpdatedAt() {
            return updatedAt;
        }

        public String getAgoraId() {
            return agoraId;
        }

        public int getMinPoints() {
            return minPoints;
        }

        public String getStripePublishableKey() {
            return stripePublishableKey;
        }

        public String getStripeSecreteKey() {
            return stripeSecreteKey;
        }

        public int getDailyTaskMinValue() {
            return dailyTaskMinValue;
        }

        public int getUserCallCharge() {
            return userCallCharge;
        }

        public int getToCurrency() {
            return toCurrency;
        }

        public String getStripeId() {
            return stripeId;
        }

        public String getId() {
            return id;
        }

        public String getPolicyLink() {
            return policyLink;
        }
    }
}