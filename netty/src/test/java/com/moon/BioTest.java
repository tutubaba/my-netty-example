package com.moon;



import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class BioTest {

    public static void main(String[] args) throws Exception{
        BioTest.testBio();
    }
    public static void testBio() throws Exception {
        ServerSocket serverSocket = new ServerSocket();
        serverSocket.bind(new InetSocketAddress(9988));
        while (true){
            System.out.print(".");
            Socket socket = serverSocket.accept();
            new Thread(new Client(socket)).start();
        }
    }


}
class Client implements Runnable{
    Socket socket;
    public Client(Socket socket){
        this.socket = socket;
        System.out.println("有client连上来了");
    }
    InputStream stream = null;
    @Override
    public void run() {
        InputStream in = null;
        OutputStream out = null;
        int sequence = 0;
        while (true){
            try{
                in = socket.getInputStream(); // 流：客户端->服务端（读）
                out = socket.getOutputStream(); // 流：服务端->客户端（写）
                int receiveBytes;
                byte[] receiveBuffer = new byte[128];
                String clientMessage = "";
                if((receiveBytes=in.read(receiveBuffer))!=-1) {
                    clientMessage = new String(receiveBuffer, 0, receiveBytes);
                    if(clientMessage.startsWith("I am the client")) {
                        String serverResponseWords =
                                "I am the server, and you are the " + (++sequence) + "th client.";
                        out.write(serverResponseWords.getBytes());
                    }
                }
                out.flush();
                System.out.println("Server: receives clientMessage->" + clientMessage);
            }catch (IOException exception){
                exception.printStackTrace();
            }
        }
    }
}
