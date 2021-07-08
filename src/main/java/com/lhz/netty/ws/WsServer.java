package com.lhz.netty.ws;

import com.lhz.netty.first.UnixTime;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.stream.ChunkedWriteHandler;
import io.netty.util.concurrent.GlobalEventExecutor;

import java.time.LocalDateTime;

/**
 * @author lhzlhz
 * @create 2021/7/4
 */
public class WsServer {
	public static void main(String[] args) {
		EventLoopGroup boosGroup = new NioEventLoopGroup();
		EventLoopGroup workerGroup = new NioEventLoopGroup();
		try {
			//配置工具类
			ServerBootstrap bootstrap = new ServerBootstrap();
			bootstrap.group(boosGroup, workerGroup)
					.channel(NioServerSocketChannel.class)
					.childHandler(new ChannelInitializer<SocketChannel>() {
						public  ChannelGroup channels = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

						@Override
						protected void initChannel(SocketChannel ch) throws Exception {
							ChannelPipeline pipeline = ch.pipeline();
							pipeline.addLast(new HttpServerCodec());
							pipeline.addLast(new ChunkedWriteHandler());
							pipeline.addLast(new HttpObjectAggregator(8192));
							pipeline.addLast(new WebSocketServerProtocolHandler("/ws"));
							pipeline.addLast(new SimpleChannelInboundHandler<TextWebSocketFrame>() {

								@Override
								protected void channelRead0(ChannelHandlerContext ctx, TextWebSocketFrame msg) throws Exception {
									System.out.println("收到消息: "+msg.text());
									//发送给客户端
									for (Channel channel : channels) {
										if (channel.id().asShortText().equals(ctx.channel().id().asShortText())) {
											channel.writeAndFlush(new TextWebSocketFrame("时间："+ LocalDateTime.now()+" 我自己: " +msg.text()));
										}else {
											channel.writeAndFlush(new TextWebSocketFrame("时间："+ LocalDateTime.now()+ctx.channel().id().asShortText()+": "+msg.text()));

										}
									}
								}

								@Override
								public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
									for (Channel channel : channels) {
										channel.writeAndFlush(new TextWebSocketFrame("新用户"+ctx.channel().id().asShortText()+"连接进来了....."));
									}
									System.out.println(ctx.channel().id().asShortText() +"连接进来..... ");
									channels.add(ctx.channel());
								}

								@Override
								public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
									System.out.println(ctx.channel().id().asLongText()+"断开连接......");
								}

								@Override
								public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
									System.out.println("出现异常");
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
