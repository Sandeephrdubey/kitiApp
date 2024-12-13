package com.hi.live.models;

import com.google.gson.annotations.SerializedName;

public class Thumb {

    private boolean isFake;
    private String username;
    private String type;
    @SerializedName("image")
    private String image;
    ;
    @SerializedName("isBusy")
    private boolean isBusy;

    public boolean isOnline() {
        return isOnline;
    }

    @SerializedName("isOnline")
    private boolean isOnline;
    @SerializedName("view")
    private int view;

    @SerializedName("name")
    private String name;
    @SerializedName("country")
    private String countryName;
    @SerializedName("channel")
    private String channel;
    @SerializedName("hostId")
    private String hostId;

    public String getHost_id() {
        return host_id;
    }

    @SerializedName("host_id")
    private String host_id;
    @SerializedName("country_id")
    private String countryId;
    @SerializedName("coin")
    private int coin;
    @SerializedName("token")
    private String token;

    @SerializedName("liveStreamingHistoryId")
    private String liveStreamingId;

    public String getVideo() {
        return video;
    }

    @SerializedName("video")
    private String video;

    public String get_id() {
        return _id;
    }

    @SerializedName("_id")
    private String _id;

    @Override
    public String toString() {
        return "Thumb{" +
                "isFake=" + isFake +
                ", username='" + username + '\'' +
                ", type='" + type + '\'' +
                ", image='" + image + '\'' +
                ", isBusy=" + isBusy +
                ", isOnline=" + isOnline +
                ", view=" + view +
                ", name='" + name + '\'' +
                ", countryName='" + countryName + '\'' +
                ", channel='" + channel + '\'' +
                ", hostId='" + hostId + '\'' +
                ", countryId='" + countryId + '\'' +
                ", coin=" + coin +
                ", token='" + token + '\'' +
                ", liveStreamingId='" + liveStreamingId + '\'' +
                ", video='" + video + '\'' +
                ", _id='" + _id + '\'' +
                '}';
    }

    public Thumb() {

    }

    public Thumb(boolean isFake, String countryName, String image, String username, String name, String channel, int view, String type, String countryId, int coin, String token, String hostId) {
        this.isFake = isFake;
        this.countryName = countryName;
        this.image = image;
        this.username = username;
        this.name = name;
        this.channel = channel;
        this.view = view;
        this.type = type;
        this.countryId = countryId;
        this.coin = coin;
        this.token = token;
        this.hostId = hostId;
    }

    public String getLiveStreamingId() {
        return liveStreamingId;
    }

    public void setLiveStreamingId(String liveStreamingId) {
        this.liveStreamingId = liveStreamingId;
    }

    public boolean isFake() {
        return isFake;
    }

    public String getUsername() {
        return username;
    }

    public String getType() {
        return type;
    }

    public void setType(String type1) {
        this.type = type1;
    }

    public boolean isBusy() {
        return isBusy;
    }

    public void setBusy(boolean busy) {
        isBusy = busy;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }


    public boolean isIsBusy() {
        return isBusy;
    }

    public int getView() {
        return view;
    }

    public void setView(int view) {
        this.view = view;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCountryName() {
        return countryName;
    }

    public void setCountryName(String countryName) {
        this.countryName = countryName;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public String getHostId() {
        return hostId;
    }

    public void setHostId(String hostId) {
        this.hostId = hostId;
    }

    public String getCountryId() {
        return countryId;
    }

    public void setCountryId(String countryId) {
        this.countryId = countryId;
    }

    public int getCoin() {
        return coin;
    }

    public void setCoin(int coin) {
        this.coin = coin;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}