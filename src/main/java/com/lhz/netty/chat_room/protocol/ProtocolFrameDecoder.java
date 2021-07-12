package com.lhz.netty.chat_room.protocol;

import io.netty.channel.ChannelHandler;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;

/**
 * @author lhzlhz
 * @create 2021/7/8
 */

public class ProtocolFrameDecoder extends LengthFieldBasedFrameDecoder {
	public ProtocolFrameDecoder() {
		//最大长度 长度起始位置，长度的偏移量，长度int4字节，冗余几个字节，丢弃掉前面几个字节
		// ca fe ba be |01 |00 |00 |00 00 00 00 |ff| 00 00 00 c3 | xxxx
		//魔数字4字节， 1字节的版本，1 字节的序列化方式，1 字节的指令类型，4 个字节 目前用不上，1字节 无意义，对齐填充，4字节长度，内容
		this(1024,12,4,0,0);
	}
	public ProtocolFrameDecoder(int maxFrameLength, int lengthFieldOffset, int lengthFieldLength, int lengthAdjustment, int initialBytesToStrip) {
		super(maxFrameLength, lengthFieldOffset, lengthFieldLength, lengthAdjustment, initialBytesToStrip);
	}

}
