package com.lhz.netty.first;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
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
                            pipeline.addLast(new ByteToMessageDecoder() {
                                @Override
                                protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
                                    if (in.readableBytes() < 4) {
                                        return;
                                    }
                                    out.add(new UnixTime(in.readUnsignedInt()));
                                }
                            })
                                    .addLast(new ChannelInboundHandlerAdapter() {
                                        //建立连接并准备好生成流量时
                                        @Override
                                        public void channelActive(final ChannelHandlerContext ctx) { // (1)
                                            //需要分配一个包含消息的新缓冲区。
                                            final ByteBuf time = ctx.alloc().buffer(8); // (2)
                                            time.writeLong(System.currentTimeMillis());
                                            //ChannelFuture表示尚未发生的I/O操作。
                                            // 这意味着，任何请求的操作可能尚未执行，因为所有操作在Netty中是异步的
                                            final ChannelFuture f = ctx.writeAndFlush(time); // (3)
                                            f.addListener(new ChannelFutureListener() {
                                                @Override
                                                public void operationComplete(ChannelFuture future) {
                                                    assert f == future;
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
