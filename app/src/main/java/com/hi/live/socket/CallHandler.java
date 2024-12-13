package com.hi.live.socket;

public interface CallHandler {

    void onCallRequest(Object[] args);

    void onCallReceive(Object[] args);

    void onCallConfirm(Object[] args);

    void onCallAnswer(Object[] args);

    void onCallCancel(Object[] args);

    void onCallDisconnect(Object[] args);

    void onGiftRequest(Object[] args);

    void onVgift(Object[] args);

    void onComment(Object[] args);

    void onMakeCall(Object[] args);
    void onIsBusy(Object[] args);

    void onRefresh(Object[] args);
}
