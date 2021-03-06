package com.lhz.netty.chat_room.client;


import com.lhz.netty.chat_room.client.handler.HeartbeatHandler;
import com.lhz.netty.chat_room.client.handler.PongHandler;
import com.lhz.netty.chat_room.message.*;
import com.lhz.netty.chat_room.protocol.MessageCodecSharable;
import com.lhz.netty.chat_room.protocol.ProtocolFrameDecoder;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.handler.timeout.IdleStateHandler;
import jdk.nashorn.internal.ir.IfNode;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
public class ChatClient {
    public static void main(String[] args) {
        NioEventLoopGroup group = new NioEventLoopGroup();
        LoggingHandler LOGGING_HANDLER = new LoggingHandler(LogLevel.INFO);
        MessageCodecSharable MESSAGE_CODEC = new MessageCodecSharable();
        CountDownLatch waitFor = new CountDownLatch(1);
        AtomicBoolean isSucc = new AtomicBoolean(false);
        Scanner sc = new Scanner(System.in);
        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.channel(NioSocketChannel.class);
            bootstrap.group(group);
            bootstrap.handler(new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel ch) throws Exception {
                    ChannelPipeline pipeline = ch.pipeline();
                    pipeline.addLast(new ProtocolFrameDecoder());
                    pipeline.addLast(LOGGING_HANDLER);
                    pipeline.addLast(MESSAGE_CODEC);
                    pipeline.addLast(new IdleStateHandler(0, 0, 10));
                    pipeline.addLast(new HeartbeatHandler());

                    pipeline.addLast(new ChannelInboundHandlerAdapter(){
                        @Override
                        public void channelActive(ChannelHandlerContext ctx) throws Exception {
                            new Thread(() -> {
                                System.out.println("???????????????");
                                String username = sc.next();
                                System.out.println("???????????????");
                                String password = sc.next();
                                LoginRequestMessage message = new LoginRequestMessage(username,password);
                                // ????????????
                                ctx.writeAndFlush(message);
                                System.out.println("??????????????????...");
                                try {
                                    waitFor.await();
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                                if (!isSucc.get()) {
                                    ctx.channel().close();
                                    return;
                                }
                                while (true) {
                                    System.out.println("==================================");
                                    System.out.println("send [username] [content]");
                                    System.out.println("gsend [group name] [content]");
                                    System.out.println("gcreate [group name] [m1,m2,m3...]");
                                    System.out.println("gmembers [group name]");
                                    System.out.println("gjoin [group name]");
                                    System.out.println("gquit [group name]");
                                    System.out.println("quit");
                                    System.out.println("==================================");
                                    String command = null;
                                    try {
                                        command = sc.nextLine();
                                    } catch (Exception e) {
                                        break;
                                    }
                                    String[] s = command.split(" ");
                                    switch (s[0]){
                                        case "send":
                                            ctx.writeAndFlush(new ChatRequestMessage(username, s[1], s[2]));
                                            break;
                                        case "gsend":
                                            ctx.writeAndFlush(new GroupChatRequestMessage(username, s[1], s[2]));
                                            break;
                                        case "gcreate":
                                            Set<String> set = new HashSet<>(Arrays.asList(s[2].split(",")));
                                            ctx.writeAndFlush(new JoinReqMessage(s[1],username, set));
                                            break;
                                        case "gmembers":
                                            ctx.writeAndFlush(new GroupMembersRequestMessage(s[1]));
                                            break;
                                        case "gjoin":
                                            ctx.writeAndFlush(new GroupJoinRequestMessage(username, s[1]));
                                            break;
                                        case "gquit":
                                            ctx.writeAndFlush(new GroupQuitRequestMessage(username, s[1]));
                                            break;
                                        case "quit":
                                            ctx.channel().close();
                                            return;
                                        default:
                                            System.out.println("error command");
                                    }
                                }
                            }).start();
                        }
                    });
                    pipeline.addLast(new ChannelInboundHandlerAdapter() {
                        @Override
                        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                            log.debug("msg: {}", msg);
                            if ((msg instanceof LoginResponseMessage)) {
                                LoginResponseMessage response = (LoginResponseMessage) msg;
                                if (response.isSuccess()) {
                                    // ??????????????????
                                    System.out.println("succ");
                                    isSucc.set(true);
                                }else {
                                    isSucc.set(false);
                                }
                                // ?????? system in ??????
                                waitFor.countDown();
                            }
                            ctx.fireChannelRead(msg);
                        }


                    });
                    pipeline.addLast(new SimpleChannelInboundHandler<ChatResponseMessage>() {
                        @Override
                        protected void channelRead0(ChannelHandlerContext ctx, ChatResponseMessage msg) throws Exception {
                            System.out.println("from :" + msg.getFrom());
                            System.out.println("said :" + msg.getContent());
                        }
                    });
                    //??????
                    pipeline.addLast(new SimpleChannelInboundHandler<JoinRespMessage>() {
                        @Override
                        protected void channelRead0(ChannelHandlerContext ctx, JoinRespMessage msg) throws Exception {
                            System.out.println(msg.getReason());
                            Scanner reset = new Scanner(System.in);
                            String replay = reset.next();

                            String groupName = msg.getGroupName();
                            String name = msg.getName();
                            JoinAccMsg joinAccMsg = new JoinAccMsg(name, groupName, replay);
                            joinAccMsg.setCreator(msg.getCreator());
                            ctx.writeAndFlush(joinAccMsg);
                        }
                    });
                    //??????
                    pipeline.addLast(new SimpleChannelInboundHandler<JoinAccRespMsg>() {

                        @Override
                        protected void channelRead0(ChannelHandlerContext ctx, JoinAccRespMsg msg) throws Exception {
                            System.out.println(msg.getReason());
                        }
                    });
                    pipeline.addLast(new SimpleChannelInboundHandler<GroupChatResponseMessage>() {
                        @Override
                        protected void channelRead0(ChannelHandlerContext ctx, GroupChatResponseMessage msg) throws Exception {
                            System.out.println("group  msg from "+msg.getFrom());
                            System.out.println("content "+msg.getContent());
                        }
                    });
                    pipeline.addLast(new PongHandler());
                }
            });

            Channel channel = bootstrap.connect("localhost", 8899).sync().channel();
            channel.closeFuture().sync();
        } catch (Exception e) {
            log.error("client error", e);
        } finally {
            group.shutdownGracefully();
        }
    }
}
