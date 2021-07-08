package com.lhz.netty.second;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

/**
 * @author lhzlhz
 * @create 2021/7/1
 */
public class Client {
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
					protected void initChannel(SocketChannel ch) throws Exception {
						ch.pipeline().addLast(new SimpleChannelInboundHandler<ByteBuf>() {
							@Override
							protected void channelRead0(ChannelHandlerContext ctx, ByteBuf buf) throws Exception {
								String str;
								if (buf.hasArray()) { // 处理堆缓冲区
									str = new String(buf.array(), buf.arrayOffset() + buf.readerIndex(), buf.readableBytes());
								} else { // 处理直接缓冲区以及复合缓冲区
									byte[] bytes = new byte[buf.readableBytes()];
									buf.getBytes(buf.readerIndex(), bytes);
									str = new String(bytes, 0, buf.readableBytes());
								}
								System.out.println(str);
							}
						});
					}
				});
	            ChannelFuture f = b.connect(host, port).sync();
	            f.channel().closeFuture().sync();
	        } finally {
	            workerGroup.shutdownGracefully();
	        }
	    }
}
