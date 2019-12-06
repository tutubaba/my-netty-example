package com.moon;


import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

public class ReactorTest {

    public static void main(String[] args) throws Exception{
        new Thread(new Reactor()).start();
    }
}


class Reactor implements Runnable {
    Selector selector = null;
    ServerSocketChannel serverSocketChannel = null;

    @Override
    public void run() {
        try {
            serverSocketChannel = ServerSocketChannel.open();
            serverSocketChannel.bind(new InetSocketAddress(9992));
            serverSocketChannel.configureBlocking(false);
            selector = Selector.open();
            SelectionKey selectionKey = serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
            selectionKey.attach(new Acceptor());
        } catch (Exception e) {
            e.printStackTrace();
        }
        while (!Thread.interrupted()) {
            try {
                selector.select();
                Set<SelectionKey> keys = selector.selectedKeys();
                Iterator<SelectionKey> iterator = keys.iterator();
                while (iterator.hasNext()) {
                    SelectionKey selectionKey = iterator.next();
                    dispatch(selectionKey);
                    keys.clear();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void dispatch(SelectionKey selectKey) {
        Runnable runnable = (Runnable) selectKey.attachment();
        if (runnable != null) {
            runnable.run();
        }
    }


    final class Acceptor implements Runnable {

        @Override
        public void run() {
            try {
                SocketChannel socketChannel = serverSocketChannel.accept();
                System.out.println("socketChannel" + socketChannel);
                System.out.println("selector" + selector);
                new Handle(socketChannel, selector).run();
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    final class Handle implements Runnable {

        SocketChannel socketChannel1 = null;
        Selector selector1 = null;
        static final int READING = 0, SENDING = 1;
        int state = READING;
        SelectionKey sk = null;
        final ByteBuffer input = ByteBuffer.allocate(1024);
        final ByteBuffer output = ByteBuffer.allocate(1024);

        public Handle(SocketChannel socketChannel, Selector selector) {
            try {
                selector1 = selector;
                socketChannel1 = socketChannel;
                socketChannel1.configureBlocking(false);
                sk = socketChannel1.register(selector1, SelectionKey.OP_READ);
                sk.attach(this);
                selector.wakeup();
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        @Override
        public void run() {
            try {
                if (state == READING) read();
                else if (state == SENDING) send();
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        void read() throws IOException {
            socketChannel1.read(input);
            if (inputIsComplete()) {
                process();
                state = SENDING;
                sk.interestOps(SelectionKey.OP_WRITE);
            }
        }

        void send() throws IOException {
            output.flip();
            socketChannel1.write(output);
            if (outputIsComplete()) {
                sk.cancel();
            }
            state = READING;
            sk.interestOps(SelectionKey.OP_READ);
        }

        boolean inputIsComplete() { /* ... */
            return input.hasRemaining();
        }

        boolean outputIsComplete() {
            return output.hasRemaining();
        }

        void process() {
            //读数据
            StringBuilder reader = new StringBuilder();
            input.flip();
            while (input.hasRemaining()) {
                reader.append((char) input.get());
            }
            System.out.println("[Client-INFO]");
            System.out.println(reader.toString());
            String str = "HTTP/1.1 200 OK\r\nDate: Fir, 10 March 2017 21:20:01 GMT\r\n" +
                    "Content-Type: text/html;charset=UTF-8\r\nContent-Length: 23\r\nConnection:close" +
                    "\r\n\r\nHelloWorld" + System.currentTimeMillis();
            System.out.println(str);
            output.put(str.getBytes());
            System.out.println("process over.... ");
        }
    }
}