package com.lhz.netty.first;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by: hz.lai
 * Date: 2021/7/2
 * Description:
 */
public class TimeClient {
    public static void main(String[] args) throws Exception {
        String host = "localhost";
        int port =8899;
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        try {
            Bootstrap b = new Bootstrap();
            b.group(workerGroup);
            b.channel(NioSocketChannel.class);
            b.option(ChannelOption.SO_KEEPALIVE, true);
            b.handler(new ChannelInitializer<SocketChannel>() {
                @Override
                public void initChannel(SocketChannel ch) throws Exception {
                    ch.pipeline().addLast(new ChannelInboundHandlerAdapter() {
                        @Override
                        public void channelRead(ChannelHandlerContext ctx, Object msg) {
                            ByteBuf m = (ByteBuf) msg; // (1)
                            try {
                                long currentTimeMillis = (m.readLong());
                                Date currentTime = new Date(currentTimeMillis);
                                System.out.println("Default Date Format:" + currentTime.toString());

                                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                String dateString = formatter.format(currentTime);
                                // 转换一下成中国人的时间格式
                                System.out.println("Date Format:" + dateString);

                                ctx.close();
                            } finally {
                                m.release();
                            }
                        }

                        @Override
                        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
                            cause.printStackTrace();
                            ctx.close();
                        }

                    });
                }
            });

            // Start the client.
            ChannelFuture f = b.connect(host, port).sync(); // (5)

            // Wait until the connection is closed.
            f.channel().closeFuture().sync();
        } finally {
            workerGroup.shutdownGracefully();
        }
    }
}

