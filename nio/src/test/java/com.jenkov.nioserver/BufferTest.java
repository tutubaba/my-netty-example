package com.jenkov.nioserver;

import com.moon.common.util.Log;
import org.junit.Test;
import sun.nio.ch.DirectBuffer;

import java.lang.annotation.Target;
import java.nio.ByteBuffer;

public class BufferTest{
    @Test
    public void test1(){
        ByteBuffer buffer = ByteBuffer.allocateDirect(5);
        Log.log(""+buffer);  //[pos=0 lim=5 cap=5]
        byte[]  bytes = new byte[1];
        buffer.put("1".getBytes());
        Log.log(""+buffer); //[pos=1 lim=5 cap=5]
        buffer.put("2".getBytes()); // 超过cap会报 BufferOverflowException
        Log.log(""+buffer); //[pos=2 lim=5 cap=5]
        buffer.flip();
        Log.log(""+buffer);  //[pos=0 lim=2 cap=5]
        buffer.get();
        Log.log(""+buffer);  //[pos=1 lim=2 cap=5]
        buffer.get();
        Log.log(""+buffer);  // [pos=2 lim=2 cap=5]
        //buffer.get(); // pos超过了lim会 java.nio.BufferUnderflowException
    }
}
