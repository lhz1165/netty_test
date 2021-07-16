package com.lhz.netty.chat_room.server.handler;

import com.lhz.netty.chat_room.message.PingMessage;
import com.lhz.netty.chat_room.message.PongMessage;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * Date: 2021/7/16
 * Description:
 *
 * @author hz.lai
 */
public class PingHandler extends SimpleChannelInboundHandler<PingMessage> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, PingMessage msg) throws Exception {
        System.out.println("--------" +msg.from + "client ping--------");
        ctx.writeAndFlush(new PongMessage());
    }
}
