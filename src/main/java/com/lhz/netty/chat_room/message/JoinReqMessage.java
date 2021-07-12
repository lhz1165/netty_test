package com.lhz.netty.chat_room.message;

import lombok.Data;

import java.util.Set;

/**
 * Date: 2021/7/12
 * Description:
 *
 * @author hz.lai
 */
@Data
public class JoinReqMessage extends Message {
    String from;
    Set<String> members;
    private String groupName;
    public JoinReqMessage(String groupName,String from,Set<String> members) {
        this. groupName= groupName;
        this.from = from;
        this.members = members;
    }

    @Override
    public int getMessageType() {
        return join;
    }
}
