package com.lhz.netty.first;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;

/**
 * @author lhzlhz
 * @create 2021/7/1
 */
public class HttpServer {
	public static void main(String[] args) {
		EventLoopGroup boosGroup = new NioEventLoopGroup();
		EventLoopGroup workerGroup = new NioEventLoopGroup();

		try {
			//配置工具类
			ServerBootstrap bootstrap = new ServerBootstrap();
			bootstrap.group(boosGroup,workerGroup)
					.channel(NioServerSocketChannel.class)
					.childHandler(new ChannelInitializer<SocketChannel>() {
						@Override
						protected void initChannel(SocketChannel socketChannel) throws Exception {
							ChannelPipeline pipeline = socketChannel.pipeline();
							pipeline.addLast("httpServerCodec", new HttpServerCodec());

							//入栈处理器
//							读取客户端的请求
							pipeline.addLast("testHandler", new SimpleChannelInboundHandler<HttpObject>() {
								@Override
								protected void channelRead0(ChannelHandlerContext ctx, HttpObject msg) throws Exception {
									System.out.println("====================rec====================");
									if (msg instanceof HttpRequest) {
										HttpRequest request = (HttpRequest) msg;
										ByteBuf byteBuf = Unpooled.copiedBuffer("{\"msg\":\"hello world\"}", CharsetUtil.UTF_8);
										DefaultFullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK, byteBuf);
										response.headers().set(HttpHeaderNames.CONTENT_TYPE, "application/json");
										response.headers().set(HttpHeaderNames.CONTENT_LENGTH, byteBuf.readableBytes());
										ctx.writeAndFlush(response);
										ctx.channel().close();
									}
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
