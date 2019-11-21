package com.moon.protobufdemo;


import com.moon.protobufdemo.protobuf.DemoProtocol;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import com.moon.protobufdemo.protobuf.DemoProtocol.DemoRequest;

public class DemoServerHandler extends SimpleChannelInboundHandler<DemoRequest> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, DemoProtocol.DemoRequest msg)
            throws Exception {
        System.out.println("--->" + msg.getRequestMsg());
        DemoProtocol.DemoResponse.Builder builder = DemoProtocol.DemoResponse.newBuilder();
        builder.setResponseMsg("服务器信息")
                .setRet(0);
        ctx.write(builder.build());

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