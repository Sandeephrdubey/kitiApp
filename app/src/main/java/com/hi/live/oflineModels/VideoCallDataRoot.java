package com.hi.live.oflineModels;

public class VideoCallDataRoot {
    String token;
    String hostId;
    String ClientId;
    String channel;
    String hostName;
    String hostImage;
    String clientImage;
    String clientName;
    String rate;
    String CallId;
    String video;
    boolean isHostCall;

    @Override
    public String toString() {
        return "VideoCallDataRoot{" +
                "token='" + token + '\'' +
                ", hostId='" + hostId + '\'' +
                ", ClientId='" + ClientId + '\'' +
                ", channel='" + channel + '\'' +
                ", hostName='" + hostName + '\'' +
                ", hostImage='" + hostImage + '\'' +
                ", clientImage='" + clientImage + '\'' +
                ", clientName='" + clientName + '\'' +
                ", rate='" + rate + '\'' +
                ", CallId='" + CallId + '\'' +
                ", video='" + video + '\'' +
                ", isHostCall=" + isHostCall +
                '}';
    }

    public String getVideo() {
        return video;
    }

    public void setVideo(String video) {
        this.video = video;
    }

    public String getCallId() {
        return CallId;
    }

    public void setCallId(String callId) {
        CallId = callId;
    }

    public boolean isHostCall() {
        return isHostCall;
    }

    public void setHostCall(boolean hostCall) {
        isHostCall = hostCall;
    }

    public String getRate() {
        return rate;
    }

    public void setRate(String rate) {
        this.rate = rate;
    }

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public String getHostImage() {
        return hostImage;
    }

    public void setHostImage(String hostImage) {
        this.hostImage = hostImage;
    }

    public String getClientImage() {
        return clientImage;
    }

    public void setClientImage(String clientImage) {
        this.clientImage = clientImage;
    }

    public String getHostName() {
        return hostName;
    }

    public void setHostName(String hostName) {
        this.hostName = hostName;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getHostId() {
        return hostId;
    }

    public void setHostId(String hostId) {
        this.hostId = hostId;
    }

    public String getClientId() {
        return ClientId;
    }

    public void setClientId(String clientId) {
        ClientId = clientId;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }
}
