package com.example.leah.photoapp;

import android.util.Log;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.Socket;

public class TcpClient {

    //private InetAddress serverAddr;
    //private Socket socket;

    private static final TcpClient instance = new TcpClient();

    private TcpClient() {
//        Thread t = new Thread(){
//            public void run() {
//                try {
//                    InetAddress serverAddr = InetAddress.getByName("10.0.2.2");
//                    //create a socket to make the connection with the server
//                    Socket socket = new Socket(serverAddr, 1234);
//                } catch (Exception e) {
//                    Log.e("TCP","open socket: Error",e);
//                }
//            }
//        };
//        t.start();

    }
    public static TcpClient GetInstance() {
        return instance;
    }

//    public void closeCommunication() {
//        Thread t = new Thread(){
//            public void run() {
//                try {
//                    socket.close();
//                } catch (Exception e) {
//                    Log.e("TCP","close socket: Error",e);
//                }
//            }
//        };
//        t.start();
//    }

    public void SendImage(final int size, final byte[] imageBytes, final String name) {
        Thread t = new Thread(){
            public void run() {
                Socket socket = null;
                try {
                    Log.e("client","in sendImage");
                    //sends the message to the server
                    InetAddress serverAddr = InetAddress.getByName("10.0.2.2");
                    //create a socket to make the connection with the server
                    socket = new Socket(serverAddr, 1234);
                    OutputStream output = socket.getOutputStream();
                    OutputStreamWriter stringOutput = new OutputStreamWriter(output, "UTF-8");
                    stringOutput.write(String.valueOf(size));
                    output.write(imageBytes, 0, size);
                    stringOutput.write(name);
                    output.flush();
                } catch (Exception e) {
                    Log.e("TCP","sending: Error",e);
                } finally {
                    try {
                        socket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        t.start();
    }
}
