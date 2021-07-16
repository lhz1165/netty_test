package com.lhz.netty.chat_room.server;


import com.lhz.netty.chat_room.message.ChatRequestMessage;
import com.lhz.netty.chat_room.message.ChatResponseMessage;
import com.lhz.netty.chat_room.message.LoginRequestMessage;
import com.lhz.netty.chat_room.message.LoginResponseMessage;
import com.lhz.netty.chat_room.protocol.MessageCodecSharable;
import com.lhz.netty.chat_room.protocol.ProtocolFrameDecoder;
import com.lhz.netty.chat_room.server.handler.*;
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
        ChatRequestMessageHandler chatRequestMessageHandler = new ChatRequestMessageHandler();
        GroupJoinMessageHandler joinMessageHandler = new GroupJoinMessageHandler();
        JoinAccMsgHandler joinAccMsgHandler = new JoinAccMsgHandler();
        LoginRequestMessage2Handler loginRequestMessage2Handler = new LoginRequestMessage2Handler();
        GroupChatRequestMessageHandler groupChatRequestMessageHandler = new GroupChatRequestMessageHandler();
        PingHandler pingHandler = new PingHandler();
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
                    pipeline.addLast(loginRequestMessage2Handler);
                    pipeline.addLast(chatRequestMessageHandler);
                    pipeline.addLast(joinMessageHandler);
                    pipeline.addLast(joinAccMsgHandler);
                    pipeline.addLast(groupChatRequestMessageHandler);
                    pipeline.addLast(pingHandler);
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
