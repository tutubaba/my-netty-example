package com.moon.memory;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.util.ReferenceCountUtil;

import java.util.concurrent.TimeUnit;

public class PooledDemo {
    public static void main(String[] args)throws Exception {
        test();
    }

    public static void test()throws Exception{

        ByteBuf byteBuf = Unpooled.directBuffer(1024000);

        //tiny规格内存分配 会变成大于等于16的整数倍的数：这里254 会规格化为256

        //读写bytebuf
        byteBuf.writeInt(126);
        System.out.println(byteBuf.writableBytes());
        System.out.println(byteBuf.readInt());
        TimeUnit.SECONDS.sleep(30);
        //很重要，内存释放
        //byteBuf.release();
        ReferenceCountUtil.release(byteBuf);
        System.out.println("释放内存");
        System.out.println(byteBuf.writableBytes());
        TimeUnit.SECONDS.sleep(15);
        System.out.println("结束");
    }

}
