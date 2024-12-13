package com.hi.live.token;

public interface PackableEx extends Packable {
    void unmarshal(ByteBuf in);
}
