package com.lhz.netty.second;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

/**
 * @author lhzlhz
 * @create 2021/7/1
 */
public class Client {
	public static void main(String[] args) {
		NioEventLoopGroup eventLoopGroup = new NioEventLoopGroup();
		Bootstrap bootstrap = new Bootstrap();
		bootstrap.group(eventLoopGroup)
				.channel(NioSocketChannel.class)
				.handler(new SimpleChannelInboundHandler<>() {
				})
	}
}
