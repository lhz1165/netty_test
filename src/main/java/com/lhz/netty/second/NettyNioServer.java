package com.lhz.netty.second;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * Created by: hz.lai
 * Date: 2021/7/6
 * Description:
 */
public class NettyNioServer {
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
                             ch.pipeline().addLast(new ChannelInboundHandlerAdapter(){
                                 final ByteBuf buf = Unpooled.copiedBuffer("Hi!\r\n", StandardCharsets.UTF_8);
                                 @Override
                                 public void channelActive(ChannelHandlerContext ctx) throws Exception {
                                     ctx.writeAndFlush(buf.duplicate()).addListener(ChannelFutureListener.CLOSE);
                                 }
                             });
                         }
                     });
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
