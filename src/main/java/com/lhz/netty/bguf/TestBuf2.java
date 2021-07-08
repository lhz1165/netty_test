package com.lhz.netty.bguf;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;

import java.nio.charset.StandardCharsets;

/**
 * Created by: hz.lai
 * Date: 2021/7/6
 * Description:
 */
public class TestBuf2 {
    public static void main(String[] args) {
        ByteBuf buf = Unpooled.copiedBuffer("Netty in Action rocks!", StandardCharsets.UTF_8);
        System.out.println((char)buf.readByte());
        int readerIndex = buf.readerIndex();
        int writerIndex = buf.writerIndex();
        buf.writeByte((byte)'?');
        assert readerIndex == buf.readerIndex();
        assert writerIndex != buf.writerIndex();
        System.out.println(ByteBufUtil.hexDump(buf));
        ByteBuf buffer = ByteBufAllocator.DEFAULT.buffer(1024);

    }



}
