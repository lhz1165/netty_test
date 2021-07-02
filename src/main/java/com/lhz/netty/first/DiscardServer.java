package com.lhz.netty.first;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.util.ReferenceCountUtil;

/**
 * Created by: hz.lai
 * Date: 2021/7/2
 * Description:
 */
public class DiscardServer {
    public static void main(String[] args) {
        NioEventLoopGroup bossGroup = new NioEventLoopGroup();
        NioEventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline().addLast(new ChannelInboundHandlerAdapter() {
                                //每当从客户端接收到新数据时，使用该方法来接收客户端的消息
                                @Override
                                public void channelRead(ChannelHandlerContext ctx, Object msg) { // (2)

                                    ByteBuf in = (ByteBuf) msg;
                                    try {
                                        while (in.isReadable()) { // (1)
                                            System.out.print((char) in.readByte());
                                            System.out.flush();
                                        }
                                    } finally {
                                        ReferenceCountUtil.release(msg); // (2)
                                    }
                                }
                                @Override
                                public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) { // (4)
                                    // 出现异常时关闭连接。
                                    cause.printStackTrace();
                                    ctx.close();
                                }

                            });
                        }
                    })
                    .option(ChannelOption.SO_BACKLOG, 128)
                    .childOption(ChannelOption.SO_KEEPALIVE, true);
            ChannelFuture f = serverBootstrap.bind(8899).sync();
            f.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }

    }



}
