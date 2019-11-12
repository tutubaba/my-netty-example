package com.moon.helloword;

import io.netty.channel.*;
import io.netty.channel.ChannelHandler.Sharable;

import java.net.InetAddress;
import java.util.Date;
import java.util.logging.Logger;


@Sharable
public class ServerHandler  extends ChannelInboundHandlerAdapter {

    Logger LOG = Logger.getLogger("ServerHandler");
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        // 为新连接发送庆祝
        LOG.info("===channelActive===");
        ctx.write("Welcome to " + InetAddress.getLocalHost().getHostName() + "!\r\n");
        ctx.write("It is " + new Date() + " now.\r\n");
        ctx.flush();
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        LOG.info("===channelRead===");
        LOG.info("HelloWorldClientHandler read Message:" + msg);
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
}