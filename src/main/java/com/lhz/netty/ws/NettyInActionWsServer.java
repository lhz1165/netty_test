package com.lhz.netty.ws;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.util.concurrent.GlobalEventExecutor;
import io.netty.util.concurrent.ImmediateEventExecutor;

/**
 * Created by: hz.lai
 * Date: 2021/7/8
 * Description:
 */
public class NettyInActionWsServer {
   public static void main(String[] args) {
       NioEventLoopGroup bossGroup = new NioEventLoopGroup();
       NioEventLoopGroup workerGroup = new NioEventLoopGroup();
        ChannelGroup channels = new DefaultChannelGroup(ImmediateEventExecutor.INSTANCE);
       try {
           ServerBootstrap serverBootstrap = new ServerBootstrap();
           serverBootstrap.group(bossGroup, workerGroup)
                   .channel(NioServerSocketChannel.class)
                   .childHandler(new ChatServerInitializer(channels));
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
