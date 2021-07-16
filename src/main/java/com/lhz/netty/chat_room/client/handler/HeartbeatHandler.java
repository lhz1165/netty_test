package com.lhz.netty.chat_room.client.handler;

import com.lhz.netty.chat_room.message.PingMessage;
import com.sun.org.apache.bcel.internal.generic.IF_ACMPEQ;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.*;
import io.netty.handler.timeout.IdleStateEvent;

import java.nio.charset.StandardCharsets;

/**
 * Date: 2021/7/16
 * Description:
 *
 * @author hz.lai
 */
@ChannelHandler.Sharable
public class HeartbeatHandler extends ChannelInboundHandlerAdapter {


    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            PingMessage msg = new PingMessage(ctx.name());
            ctx.writeAndFlush(msg).addListener(ChannelFutureListener.CLOSE_ON_FAILURE);
        } else {
            super.userEventTriggered(ctx, evt);
        }
    }
}
