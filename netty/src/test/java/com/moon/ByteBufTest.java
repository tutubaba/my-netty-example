package com.moon;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import org.junit.Test;

public class ByteBufTest {

    @Test
    public void test1(){
        ByteBuf byteBuf = Unpooled.buffer(10);
        System.out.println(byteBuf); //(ridx: 0, widx: 0, cap: 10)
        for(int i= 1; i<= 5; i++) {
            byteBuf.writeBytes(String.valueOf(i).getBytes());
        }
        System.out.println(byteBuf);//(ridx: 0, widx: 5, cap: 10)
        byteBuf.readBytes(1);
        System.out.println(byteBuf); //(ridx: 1, widx: 5, cap: 10)
        byteBuf.readBytes(1);
        System.out.println(byteBuf); //(ridx: 2, widx: 5, cap: 10)
        byteBuf.discardReadBytes();
        System.out.println(byteBuf); //(ridx: 0, widx: 3, cap: 10)
        for(int i=0; i<10;i++){
            System.out.print((char)byteBuf.getByte(i) ) ;  // 34545
        }
        System.out.println();
        System.out.println(byteBuf); //(ridx: 0, widx: 3, cap: 10)
    }
}
