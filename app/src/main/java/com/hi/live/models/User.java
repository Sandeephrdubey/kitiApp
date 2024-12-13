package com.hi.live.models;

import com.google.gson.annotations.SerializedName;

public class User {

    @SerializedName("country")
    private String country;

    @SerializedName("dailyTaskFinishedCount")
    private int dailyTaskFinishedCount;

    @SerializedName("channel")
    private String channel;

    @SerializedName("bio")
    private String bio;

    @SerializedName("type")
    private String type;

    @SerializedName("uniqueId")
    private String uniqueId;
    @SerializedName("isLogout")
    private boolean isLogout;
    @SerializedName("createdAt")
    private String createdAt;
    @SerializedName("identity")
    private String identity;
    @SerializedName("__v")
    private int V;
    @SerializedName("fcm_token")
    private String fcmToken;
    @SerializedName("block")
    private boolean block;
    @SerializedName("plan_start_date")
    private String planStartDate;
    @SerializedName("updatedAt")
    private String updatedAt;
    @SerializedName("image")
    private String image;
    @SerializedName("spendCoin")
    private int spendCoin;
    @SerializedName("mobileNo")
    private String mobileNo;
    @SerializedName("isVIP")
    private boolean isVIP;
    @SerializedName("isHost")
    private boolean isHost;
    @SerializedName("token")
    private String token;
    @SerializedName("following_count")
    private int followingCount;
    @SerializedName("followers_count")
    private int followersCount;
    @SerializedName("name")
    private String name;
    @SerializedName("_id")
    private String id;
    @SerializedName("plan_id")
    private String planId;
    @SerializedName("coin")
    private int coin;
    @SerializedName("username")
    private String username;

    public String getUniqueId() {
        return uniqueId;
    }

    public void setUniqueId(String uniqueId) {
        this.uniqueId = uniqueId;
    }

    public boolean isIsHost() {
        return isHost;
    }

    public void setHost(boolean host) {
        isHost = host;
    }

    public String getCountry() {
        return country;
    }

    public int getDailyTaskFinishedCount() {
        return dailyTaskFinishedCount;
    }

    public String getChannel() {
        return channel;
    }

    public String getBio() {
        return bio;
    }

    public String getType() {
        return type;
    }

    public boolean isIsLogout() {
        return isLogout;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public String getIdentity() {
        return identity;
    }

    public int getV() {
        return V;
    }

    public String getFcmToken() {
        return fcmToken;
    }

    public boolean isBlock() {
        return block;
    }

    public String getPlanStartDate() {
        return planStartDate;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public String getImage() {
        return image;
    }

    public int getSpendCoin() {
        return spendCoin;
    }

    public String getMobileNo() {
        return mobileNo;
    }

    public boolean isIsVIP() {
        return isVIP;
    }

    public String getToken() {
        return token;
    }

    public int getFollowingCount() {
        return followingCount;
    }

    public int getFollowersCount() {
        return followersCount;
    }

    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }

    @Override
    public String toString() {
        return "User{" +
                "country='" + country + '\'' +
                ", dailyTaskFinishedCount=" + dailyTaskFinishedCount +
                ", channel='" + channel + '\'' +
                ", bio='" + bio + '\'' +
                ", type='" + type + '\'' +
                ", uniqueId='" + uniqueId + '\'' +
                ", isLogout=" + isLogout +
                ", createdAt='" + createdAt + '\'' +
                ", identity='" + identity + '\'' +
                ", V=" + V +
                ", fcmToken='" + fcmToken + '\'' +
                ", block=" + block +
                ", planStartDate='" + planStartDate + '\'' +
                ", updatedAt='" + updatedAt + '\'' +
                ", image='" + image + '\'' +
                ", spendCoin=" + spendCoin +
                ", mobileNo='" + mobileNo + '\'' +
                ", isVIP=" + isVIP +
                ", isHost=" + isHost +
                ", token='" + token + '\'' +
                ", followingCount=" + followingCount +
                ", followersCount=" + followersCount +
                ", name='" + name + '\'' +
                ", id='" + id + '\'' +
                ", planId='" + planId + '\'' +
                ", coin=" + coin +
                ", username='" + username + '\'' +
                '}';
    }

    public String getPlanId() {
        return planId;
    }

    public int getCoin() {
        return coin;
    }

    public String getUsername() {
        return username;
    }
}