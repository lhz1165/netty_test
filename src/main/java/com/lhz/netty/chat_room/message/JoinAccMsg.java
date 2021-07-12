package com.lhz.netty.chat_room.message;

import lombok.Data;

/**
 * Date: 2021/7/12
 * Description:
 *
 * @author hz.lai
 */
@Data
public class JoinAccMsg extends Message{
    String creator;
    String yesOrNo;
    String from;
    String groupName;
    public JoinAccMsg(String from,String groupName,String yesOrNo) {
        this.from=from;
        this.groupName= groupName;
        this.yesOrNo = yesOrNo;
    }

    @Override
    public int getMessageType() {
        return acc;
    }
}
