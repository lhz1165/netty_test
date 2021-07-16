package com.lhz.netty.chat_room.server.handler;

import com.lhz.netty.chat_room.message.JoinAccMsg;
import com.lhz.netty.chat_room.message.JoinAccRespMsg;
import com.lhz.netty.chat_room.server.session.Group;
import com.lhz.netty.chat_room.server.session.GroupSession;
import com.lhz.netty.chat_room.server.session.GroupSessionFactory;
import com.lhz.netty.chat_room.server.session.SessionFactory;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * Date: 2021/7/12
 * Description:
 *
 * @author hz.lai
 */
@ChannelHandler.Sharable
public class JoinAccMsgHandler extends SimpleChannelInboundHandler<JoinAccMsg> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, JoinAccMsg msg) throws Exception {
        GroupSession groupSession = GroupSessionFactory.getGroupSession();
        String groupName = msg.getGroupName();
        String yesOrNo = msg.getYesOrNo();
        String creator = msg.getCreator();
        Channel channel = SessionFactory.getSession().getChannel(creator);
        if ("yes".equals(yesOrNo)) {
            groupSession.getMembers(groupName).add(msg.getFrom());
            JoinAccRespMsg joinAccRespMsg = new JoinAccRespMsg();
            joinAccRespMsg.setSuccess(true);
            joinAccRespMsg.setReason("用户"+msg.getFrom()+"加入成功");
            channel.writeAndFlush(joinAccRespMsg);
        }else {
            JoinAccRespMsg joinAccRespMsg = new JoinAccRespMsg();
            joinAccRespMsg.setSuccess(true);
            joinAccRespMsg.setReason("用户"+msg.getFrom()+"拒绝加入");
            channel.writeAndFlush(joinAccRespMsg);

        }
    }
}
