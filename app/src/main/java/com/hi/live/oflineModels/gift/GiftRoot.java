package com.hi.live.oflineModels.gift;

public class GiftRoot {
    String image;
    String username;

    String user_id;

    public String getCallId() {
        return callId;
    }

    public void setCallId(String callId) {
        this.callId = callId;
    }

    String callId;

    public String getGiftId() {
        return giftId;
    }

    public void setGiftId(String giftId) {
        this.giftId = giftId;
    }

    String giftId;

    public String getUserid() {
        return user_id;
    }

    public void setUserid(String user_id) {
        this.user_id = user_id;
    }

    Long coin;

    String liveStreamingId;

    public String getHost_id() {
        return host_id;
    }

    public void setHost_id(String host_id) {
        this.host_id = host_id;
    }

    String host_id;


    public String getLiveStreamingId() {
        return liveStreamingId;
    }

    public void setLiveStreamingId(String liveStreamingId) {
        this.liveStreamingId = liveStreamingId;
    }

    @Override
    public String toString() {
        return "GiftRoot{" +
                "image='" + image + '\'' +
                ", username='" + username + '\'' +
                ", user_id='" + user_id + '\'' +
                ", coin=" + coin +
                ", liveStreamingId='" + liveStreamingId + '\'' +
                '}';
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Long getCoin() {
        return coin;
    }

    public void setCoin(Long coin) {
        this.coin = coin;
    }
}
