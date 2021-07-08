package com.lhz.netty.bguf;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * Created by: hz.lai
 * Date: 2021/7/6
 * Description:
 */
public class TestBuf {
    public static void main(String[] args) {
        ByteBuf buf = Unpooled.copiedBuffer("Netty in Action rocks!", StandardCharsets.UTF_8);
        System.out.println((char) buf.getByte(0));
        int readerIndex = buf.readerIndex();
        int writerIndex = buf.writerIndex();
        buf.setByte(0, (byte) 'B');
        System.out.println((char) buf.getByte(0));
        System.out.println(buf.readerIndex());
        System.out.println(buf.writerIndex());
    }
}
