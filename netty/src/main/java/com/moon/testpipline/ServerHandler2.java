package com.moon.testpipline;

import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.net.InetAddress;
import java.util.Date;
import java.util.logging.Logger;


@Sharable
public class ServerHandler2 extends ChannelInboundHandlerAdapter {

    Logger LOG = Logger.getLogger("ServerHandler2");
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        // 为新连接发送庆祝
        LOG.info("===ServerHandler2-channelActive===");
        ctx.write("Welcome to " + InetAddress.getLocalHost().getHostName() + "!\r\n");
        ctx.write("It is " + new Date() + " now.\r\n");
        ctx.flush();
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        LOG.info("===ServerHandler2-channelRead===");
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