/*
 * Copyright 2012 The Netty Project
 *
 * The Netty Project licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package com.moon.splitpack;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.util.Date;
import java.util.logging.Logger;

/**
 * Handles both client-side and server-side handler depending on which
 * constructor was called.
 */
public class ServerHandler extends ChannelInboundHandlerAdapter {
    private int counter;
    Logger LOG = Logger.getLogger("ClientHandler");

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg)throws Exception {
        // Echo back the received object to the client.
        //ctx.write(msg);
        ByteBuf buf = (ByteBuf) msg;
        int cnt = buf.readableBytes()/4;
        StringBuilder body = new StringBuilder();
        for(int i=0; i<cnt; i++){
            body.append(String.valueOf(buf.getInt(i*4))).append(";");
        }
        System.out.println("接收数据.." + body + "次数:"+ (++counter));
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        LOG.info("服务器读取完毕..");
        ctx.flush();//刷新后才将数据发出到SocketChannel }
    }
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
}
