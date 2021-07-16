package com.lhz.netty.ws.netty_in_action;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.websocketx.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Date: 2021/7/16
 * Description:
 *
 * @author hz.lai
 */
public class WebSocketServerHandler extends SimpleChannelInboundHandler<Object> {
    private WebSocketServerHandshaker handshaker;

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof FullHttpRequest) {
            handleHttpRequest(ctx, (FullHttpRequest) msg);
        }
        if (msg instanceof WebSocketFrame) {
            handleWebSocketFrame(ctx, (WebSocketFrame) msg);
        }
    }

    private void handleHttpRequest(ChannelHandlerContext ctx, FullHttpRequest request) {
        WebSocketServerHandshakerFactory handshakerFactory =
                new WebSocketServerHandshakerFactory("ws://localhost:8899/ws", null, false);
        handshaker = handshakerFactory.newHandshaker(request);
        handshaker.handshake(ctx.channel(), request);
    }

    private void handleWebSocketFrame(ChannelHandlerContext ctx, WebSocketFrame frame) {
        if (frame instanceof CloseWebSocketFrame) {
            handshaker.close(ctx.channel(), ((CloseWebSocketFrame) frame).retain());
        }
        if (frame instanceof PingWebSocketFrame) {
            ctx.channel().writeAndFlush(new TextWebSocketFrame(frame.content().retain()));
            return;
        }
        if (frame instanceof TextWebSocketFrame) {
            String request = ((TextWebSocketFrame) frame).text();
            ctx.channel().writeAndFlush(new TextWebSocketFrame(request + "欢迎！！！ " + DateTimeFormatter.ofPattern("yyyy-MM-dd").format(LocalDateTime.now())));

        }

    }
}
