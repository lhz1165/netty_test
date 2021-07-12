package com.lhz.netty.chat_room.message;

import lombok.Data;

/**
 * Date: 2021/7/12
 * Description:
 *
 * @author hz.lai
 */
@Data
public class JoinAccRespMsg extends AbstractResponseMessage {
 String creator;
 @Override
 public int getMessageType() {
  return accResp;
 }
}
