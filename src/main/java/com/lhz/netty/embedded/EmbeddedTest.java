package com.lhz.netty.embedded;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.handler.codec.FixedLengthFrameDecoder;

import java.nio.charset.StandardCharsets;

/**
 * Created by: hz.lai
 * Date: 2021/7/7
 * Description:
 */
public class EmbeddedTest {
    public static void main(String[] args) {
        ByteBuf buf = Unpooled.buffer();
        for (int i = 0; i < 9; i++) {
            buf.writeByte(i);
        }
        ByteBuf input = buf.duplicate();
        EmbeddedChannel channel = new EmbeddedChannel(new FixedLengthFrameDecoder(3));
        System.out.println(channel.writeInbound(input.retain()));
        System.out.println(channel.finish());
        // read messages
        ByteBuf read = (ByteBuf) channel.readInbound();
        System.out.println(buf.readSlice(3).toString(StandardCharsets.UTF_8));
        System.out.println(read.toString(StandardCharsets.UTF_8));
    }



}
