package com.example.crystal;


import android.content.Intent;
import android.os.Handler;
import android.os.Message;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerClient extends AppCompatActivity {
    RequestActivity requestActivities;
    Socket socket;

    Server server;
    Client client;
    Intent intent = getIntent();
    int port = Integer.parseInt(intent.getStringExtra("userPort"));
    String username = intent.getStringExtra("userName");
    static final int MESSAGE_READ = 1;


    Handler handler=new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message msg) {
            if(msg.what==MESSAGE_READ){
                byte[] readBuff=(byte[]) msg.obj;
                String tempMsg= new String(readBuff,0,msg.arg1);

            }
            return false;
        }
    });




    public class Client extends Thread {
        Socket socket;
        String hostAdd;
        int port;



        public Client(InetAddress hostAddress, int port) {
            this.port = port;
            hostAdd = hostAddress.getHostAddress();
            socket=new Socket();
        }

        @Override
        public void run() {
            try {
                socket.connect(new InetSocketAddress(hostAdd, port), 500);
            }catch (IOException e){
                e.printStackTrace();
            }
        }



    }

    public class Server extends Thread {
        ServerSocket serverSocket;
        String hosAdd;
        int port;
        public Server(int port){
            this.port=port;
        }
        @Override
        public void run() {
            try {
                serverSocket = new ServerSocket(port);
                socket = serverSocket.accept();

            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }
    //Requester use to create Post to Advisor
    private class SendReceivePost extends Thread{
        private Socket socket;
        private InputStream inputStream;
        private OutputStream outputStream;

        public SendReceivePost(Socket nSocket){
            socket=nSocket;
            try {
                inputStream=socket.getInputStream();
                outputStream=socket.getOutputStream();
            }catch (IOException e){
                e.printStackTrace();
            }
        }

        @Override
        public void run(){
            byte[] buffer= new byte[1024];
            int bytes;

            while (socket!= null){
                try{
                    bytes= inputStream.read(buffer);
                    if(bytes>0){
                        handler.obtainMessage(MESSAGE_READ,bytes,-1,buffer).sendToTarget();
                    }
                }catch (IOException e){
                    e.printStackTrace();
                }
            }
        }
        public void write(String msg) {
            new Thread(() -> {
                try {
                    outputStream.write(msg.getBytes());
                    //runOnUiThread(() ->
                            // your out put in post

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }).start();

        }
        }
}
