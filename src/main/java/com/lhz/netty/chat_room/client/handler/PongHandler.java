package com.lhz.netty.chat_room.client.handler;

import com.lhz.netty.chat_room.message.PongMessage;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * Date: 2021/7/16
 * Description:
 *
 * @author hz.lai
 */
public class PongHandler extends SimpleChannelInboundHandler<PongMessage> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, PongMessage msg) throws Exception {
        System.out.println("--------from server pong--------");
    }
}
