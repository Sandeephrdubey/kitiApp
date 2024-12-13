package com.hi.live.models;

import com.google.gson.annotations.SerializedName;

public class HostRoot {

    @SerializedName("host")
    private Host host;

    @SerializedName("message")
    private String message;

    @SerializedName("status")
    private boolean status;

    public Host getHost() {
        return host;
    }

    public String getMessage() {
        return message;
    }

    public boolean isStatus() {
        return status;
    }

    public static class Host {

        @SerializedName("country")
        private String country;

        @SerializedName("channel")
        private Object channel;

        @SerializedName("bio")
        private String bio;

        @SerializedName("isOnline")
        private boolean isOnline;

        @SerializedName("agencyId")
        private String agencyId;

        @SerializedName("isLogout")
        private boolean isLogout;

        @SerializedName("createdAt")
        private String createdAt;

        @SerializedName("password")
        private String password;

        @SerializedName("isLive")
        private boolean isLive;

        @SerializedName("__v")
        private int V;

        @SerializedName("fcm_token")
        private String fcmToken;

        @SerializedName("hostCountry")
        private Object hostCountry;

        @SerializedName("block")
        private boolean block;

        @SerializedName("updatedAt")
        private String updatedAt;

        @SerializedName("receivedCoin")
        private int receivedCoin;

        @SerializedName("image")
        private String image;

        @SerializedName("isBusy")
        private boolean isBusy;

        @SerializedName("hostId")
        private String hostId;

        @SerializedName("mobileNo")
        private String mobileNo;

        @SerializedName("token")
        private Object token;

        @SerializedName("following_count")
        private int followingCount;

        @SerializedName("followers_count")
        private int followersCount;

        @SerializedName("name")
        private String name;

        @SerializedName("_id")
        private String id;

        @SerializedName("IPAddress")
        private String iPAddress;

        public String getUniqueId() {
            return uniqueId;
        }

        @SerializedName("uniqueId")
        private String uniqueId;

        @SerializedName("coin")
        private int coin;

        @SerializedName("username")
        private String username;

        public String getCountry() {
            return country;
        }

        public Object getChannel() {
            return channel;
        }

        public String getBio() {
            return bio;
        }

        public boolean isIsOnline() {
            return isOnline;
        }

        public String getAgencyId() {
            return agencyId;
        }

        public boolean isIsLogout() {
            return isLogout;
        }

        public String getCreatedAt() {
            return createdAt;
        }

        public String getPassword() {
            return password;
        }

        public boolean isIsLive() {
            return isLive;
        }

        public int getV() {
            return V;
        }

        public String getFcmToken() {
            return fcmToken;
        }

        public Object getHostCountry() {
            return hostCountry;
        }

        public boolean isBlock() {
            return block;
        }

        public String getUpdatedAt() {
            return updatedAt;
        }

        public int getReceivedCoin() {
            return receivedCoin;
        }

        public String getImage() {
            return image;
        }

        public boolean isIsBusy() {
            return isBusy;
        }

        public String getHostId() {
            return hostId;
        }

        public String getMobileNo() {
            return mobileNo;
        }

        public Object getToken() {
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

        public String getIPAddress() {
            return iPAddress;
        }

        public int getCoin() {
            return coin;
        }

        public String getUsername() {
            return username;
        }
    }
}