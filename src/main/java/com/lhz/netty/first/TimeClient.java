package com.lhz.netty.first;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by: hz.lai
 * Date: 2021/7/2
 * Description:
 */
public class TimeClient {
    public static void main(String[] args) throws Exception {
        String host = "localhost";
        int port = 8899;
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        try {
            Bootstrap b = new Bootstrap();
            b.group(workerGroup);
            b.channel(NioSocketChannel.class);
            b.option(ChannelOption.SO_KEEPALIVE, true);
            b.handler(new ChannelInitializer<SocketChannel>() {
                @Override
                public void initChannel(SocketChannel ch) throws Exception {
                    ch.pipeline().addLast(new ByteToMessageDecoder() {
                        @Override
                        protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
                            if (in.readableBytes() < 4) {
                                return;
                            }
                            out.add(new UnixTime(in.readUnsignedInt()));
                        }
                    }).addLast(new ChannelInboundHandlerAdapter() {
                        @Override
                        public void channelRead(ChannelHandlerContext ctx, Object msg) {
                            UnixTime m = (UnixTime) msg;
                            System.out.println(m);
                            ctx.close();
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

