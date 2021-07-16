package com.lhz.netty.chat_room.message;

public class PingMessage extends Message {
    public String from;

    public PingMessage(String from) {
        this.from = from;
    }

    @Override
    public int getMessageType() {
        return PingMessage;
    }
}
