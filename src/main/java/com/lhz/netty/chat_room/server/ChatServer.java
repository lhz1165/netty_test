package com.lhz.netty.chat_room.server;


import com.lhz.netty.chat_room.message.ChatRequestMessage;
import com.lhz.netty.chat_room.message.ChatResponseMessage;
import com.lhz.netty.chat_room.message.LoginRequestMessage;
import com.lhz.netty.chat_room.message.LoginResponseMessage;
import com.lhz.netty.chat_room.protocol.MessageCodecSharable;
import com.lhz.netty.chat_room.protocol.ProtocolFrameDecoder;
import com.lhz.netty.chat_room.server.service.UserServiceFactory;
import com.lhz.netty.chat_room.server.session.SessionFactory;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ChatServer {
    public static void main(String[] args) {
        NioEventLoopGroup boss = new NioEventLoopGroup();
        NioEventLoopGroup worker = new NioEventLoopGroup();
        LoggingHandler LOGGING_HANDLER = new LoggingHandler(LogLevel.INFO);

        try {
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.channel(NioServerSocketChannel.class);
            serverBootstrap.group(boss, worker);
            serverBootstrap.childHandler(new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel ch) throws Exception {
                    ChannelPipeline pipeline = ch.pipeline();
                    pipeline.addLast(new ProtocolFrameDecoder());
                    pipeline.addLast(LOGGING_HANDLER);
                    pipeline.addLast(new MessageCodecSharable());
                    pipeline.addLast(new SimpleChannelInboundHandler<LoginRequestMessage>() {
                        @Override
                        protected void channelRead0(ChannelHandlerContext ctx, LoginRequestMessage msg) throws Exception {
                            String username = msg.getUsername();
                            String password = msg.getPassword();
                            if (UserServiceFactory.getUserService().login(username, password)) {
                                log.debug("msg: {}", msg);
                                SessionFactory.getSession().bind(ctx.channel(), username);
                                ctx.writeAndFlush(new LoginResponseMessage(true, "登录成功"));
                            }else {
                                ctx.writeAndFlush(new LoginResponseMessage(false, "登录失败"));
                            }
                        }
                    });
                    pipeline.addLast(new SimpleChannelInboundHandler<ChatRequestMessage>() {
                        @Override
                        protected void channelRead0(ChannelHandlerContext ctx, ChatRequestMessage msg) throws Exception {
                            String from = msg.getFrom();
                            Channel toCtx = SessionFactory.getSession().getChannel(from);
                            ChatResponseMessage message = new ChatResponseMessage(from, msg.getContent());
                            message.setSuccess(true);
                            toCtx.writeAndFlush(message);
                        }
                    });
                }
            });
            Channel channel = serverBootstrap.bind(8899).sync().channel();
            channel.closeFuture().sync();
        } catch (InterruptedException e) {
            log.error("server error", e);
        } finally {
            boss.shutdownGracefully();
            worker.shutdownGracefully();
        }
    }

}
