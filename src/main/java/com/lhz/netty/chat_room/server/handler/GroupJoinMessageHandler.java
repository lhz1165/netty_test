package com.lhz.netty.chat_room.server.handler;



import com.lhz.netty.chat_room.message.JoinReqMessage;

import com.lhz.netty.chat_room.message.JoinRespMessage;
import com.lhz.netty.chat_room.server.session.Group;
import com.lhz.netty.chat_room.server.session.GroupSession;
import com.lhz.netty.chat_room.server.session.GroupSessionFactory;
import com.lhz.netty.chat_room.server.session.SessionFactory;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.util.List;
import java.util.Set;

@ChannelHandler.Sharable
public class GroupJoinMessageHandler extends SimpleChannelInboundHandler<JoinReqMessage> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, JoinReqMessage msg) throws Exception {
        GroupSession groupSession = GroupSessionFactory.getGroupSession();
        Group group = groupSession.createGroup(msg.getGroupName());
        group.getMembers().add(msg.getFrom());
        String creator = msg.getFrom();
        Set<String> members = msg.getMembers();
        for (String member : members) {
            Channel channel = SessionFactory.getSession().getChannel(member);
            JoinRespMessage joinRespMessage = new JoinRespMessage(member,msg.getGroupName(),true,msg.getFrom() + "邀请你加入" + msg.getGroupName() + "是否加入? yes/no");
            joinRespMessage.setCreator(creator);
            channel.writeAndFlush(joinRespMessage);
        }
    }
}
