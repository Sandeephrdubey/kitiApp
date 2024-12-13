package com.hi.live.socket;

public interface LiveHandler {

    void onSimpleFilter(Object[] args);


    void onGif(Object[] args);

    void onComment(Object[] args);

    void onGift(Object[] args);

    void onView(Object[] args);

    void onSticker(Object[] args1);

    void onEmoji(Object[] args1);

    void onRefresh(Object[] args1);

    void onEnded(Object[] args);

    void onGiftUser(Object[] args);

}
