//package com.lhz.netty.chat_room.server.handler;
//
//import com.lhz.netty.chat_room.message.LoginRequestMessage;
//import com.lhz.netty.chat_room.message.LoginResponseMessage;
//import com.lhz.netty.chat_room.server.service.UserServiceFactory;
//import com.lhz.netty.chat_room.server.session.SessionFactory;
//import io.netty.channel.ChannelHandler;
//import io.netty.channel.ChannelHandlerContext;
//import io.netty.channel.SimpleChannelInboundHandler;
//import lombok.extern.slf4j.Slf4j;
//
///**
// * Date: 2021/7/12
// * Description:
// *
// * @author hz.lai
// */
//@Slf4j
//@ChannelHandler.Sharable
//public class LoginRequestMessage2Handler extends SimpleChannelInboundHandler<LoginRequestMessage> {
//    @Override
//    protected void channelRead0(ChannelHandlerContext ctx, LoginRequestMessage msg) throws Exception {
//        String username = msg.getUsername();
//        String password = msg.getPassword();
//        if (UserServiceFactory.getUserService().login(username, password)) {
//            log.debug("msg: {}", msg);
//            SessionFactory.getSession().bind(ctx.channel(), username);
//            ctx.writeAndFlush(new LoginResponseMessage(true, "登录成功"));
//        } else {
//            ctx.writeAndFlush(new LoginResponseMessage(false, "登录失败"));
//        }
//    }
//}
