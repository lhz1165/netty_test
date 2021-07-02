package com.lhz.netty.first;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.ByteToMessageCodec;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;


/**
 * Created by: hz.lai
 * Date: 2021/7/2
 * Description:
 */
public class TimeServer {
    public static void main(String[] args) {
        EventLoopGroup boosGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            //配置工具类
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(boosGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            ChannelPipeline pipeline = socketChannel.pipeline();
                            pipeline.addLast(new ChannelOutboundHandlerAdapter() {
                                @Override
                                public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
                                    UnixTime m = (UnixTime) msg;
                                    ByteBuf encoded = ctx.alloc().buffer(4);
                                    encoded.writeInt((int)m.value());
                                    ctx.write(encoded, promise);

                                }
                            })
                                    .addLast(new ChannelInboundHandlerAdapter() {
                                        //建立连接并准备好生成流量时
                                        @Override
                                        public void channelActive(final ChannelHandlerContext ctx) { // (1)
                                            ChannelFuture f = ctx.writeAndFlush(new UnixTime());

                                            f.addListener(new ChannelFutureListener() {
                                                @Override
                                                public void operationComplete(ChannelFuture future) {
                                                    System.out.println("输出成功ctx关闭");
                                                    ctx.close();
                                                }
                                            });
                                        }
                                        @Override
                                        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
                                            cause.printStackTrace();
                                            ctx.close();
                                        }
                                    });
                        }
                    });
            ChannelFuture channelFuture = bootstrap.bind(8899).sync();
            channelFuture.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            boosGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }

    }
}
