package com.moon.nio;


import com.moon.common.util.Log;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.util.Iterator;
import java.util.Set;

/**
 * User: mihasya
 * Date: Jul 25, 2010
 * Time: 9:09:03 AM
 */

/*
 * the thread is completely unnecessary, it could all just happen
 * in main()
 */
class ListenerThread extends Thread {
    Selector sel = null;
    ListenerThread(Selector sel) {
        this.sel = sel;
    }
    @Override
    public void run() {
        while (true) {

            // our canned response for now
            ByteBuffer resp = ByteBuffer.wrap(new String("got it\n").getBytes());
            try {
                // loop over all the sockets that are ready for some activity
                while (this.sel.select() > 0) {
                    Set keys = this.sel.selectedKeys();
                    Iterator i = keys.iterator();
                    while (i.hasNext()) {
                        SelectionKey key = (SelectionKey)i.next();
                        if (key.isAcceptable()) {
                            // this means that a new client has hit the port our main
                            // socket is listening on, so we need to accept the  connection
                            // and add the new client socket to our select pool for reading
                            // a command later
                            Log.log("Accepting connection!");
                            // this will be the ServerSocketChannel we initially registered
                            // with the selector in main()
                            ServerSocketChannel sch = (ServerSocketChannel)key.channel();
                            SocketChannel ch = sch.accept();
                            ch.configureBlocking(false);
                            ch.register(this.sel, SelectionKey.OP_READ);
                        } else if (key.isReadable()) {
                            // one of our client sockets has received a command and
                            // we're now ready to read it in
                            Log.log("Accepting command!");
                            SocketChannel ch = (SocketChannel)key.channel();
                            ByteBuffer buf = ByteBuffer.allocate(200);
                            ch.read(buf);
                            buf.flip();
                            Charset charset = Charset.forName("UTF-8");
                            CharsetDecoder decoder = charset.newDecoder();
                            CharBuffer cbuf = decoder.decode(buf);
                            Log.log(cbuf.toString());
                            // re-register this socket with the selector, this time
                            // for writing since we'll want to write something to it
                            // on the next go-around
                            ch.register(this.sel, SelectionKey.OP_WRITE);
                        } else if (key.isWritable()) {
                            // we are ready to send a response to one of the client sockets
                            // we had read a command from previously
                            Log.log("Sending response!");
                            SocketChannel ch = (SocketChannel)key.channel();
                            ch.write(resp);
                            resp.rewind();
                            // we may get another command from this guy, so prepare
                            // to read again. We could also close the channel, but
                            // that sort of defeats the whole purpose of doing async
                            ch.register(this.sel, SelectionKey.OP_READ);
                        }
                        i.remove();
                    }
                }
            } catch (IOException e) {
                Log.log("Error in poll loop");
                Log.log(e.getMessage());
                System.exit(1);
            }
        }
    }
}

/**
 * 一个简单的nio server,
 * reactor 单线程模型
 */
public class SampleNioServer {
    public static void main (String[] args) {
        ServerSocketChannel serverSocketChannel = null;
        Selector selector = null;

        try {
            // setup the socket we're listening for connections on.
            InetSocketAddress addr = new InetSocketAddress(8400);
            serverSocketChannel = ServerSocketChannel.open();
            serverSocketChannel.configureBlocking(false);
            serverSocketChannel.socket().bind(addr);
            // setup our selector and register the main socket on it
            selector = Selector.open();
            SelectionKey selectionKey = serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
        } catch (IOException e) {
            Log.log("Couldn't setup server socket");
            Log.log(e.getMessage());
            System.exit(1);
        }

        // fire up the listener thread, pass it our selector
        ListenerThread listener = new ListenerThread(selector);
        listener.run();
    }
}