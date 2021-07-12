package com.lhz.netty.chat_room.message;

import lombok.Data;
import lombok.ToString;

/**
 * Date: 2021/7/12
 * Description:
 *
 * @author hz.lai
 */
@Data
@ToString(callSuper = true)
public class JoinRespMessage extends AbstractResponseMessage {
    String groupName;
    String name;
    String creator;
    public JoinRespMessage(String name,String groupName,boolean success, String reason) {
        super(success, reason);
        this.name = name;
        this.groupName = groupName;
    }
    @Override
    public int getMessageType() {
        return joinResp;
    }
}
