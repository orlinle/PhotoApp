package com.example.leah.photoapp;
import android.util.Log;
import java.io.DataOutputStream;import java.io.IOException;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;

public class TcpClient {

    private InetAddress serverAddr;
    private Socket socket;

    private static final TcpClient instance = new TcpClient();

    //private constructor for singleton class
    private TcpClient() {}
    //public  method to get client instance
    public static TcpClient GetInstance() {
        return instance;
    }

    /**
     * open communication with server
     */
    public void openCommunication() {
        Thread t = new Thread(){
            public void run() {
                try {
                    serverAddr = InetAddress.getByName("10.0.2.2");
                    //create a socket to make the connection with the server
                    socket = new Socket(serverAddr, 8200);
                } catch (Exception e) {
                    Log.e("TCP","open socket: Error",e);
                }
            }
        };
        t.start();
    }

    /**
     * close communication with server
     */
    public void closeCommunication() {
        Thread t = new Thread(){
            public void run() {
                try {
                    socket.close();
                } catch (Exception e) {
                    Log.e("TCP","close socket: Error",e);
                }
            }
        };
        t.start();
    }

    /**
     * send image via socket
     * @param size image size
     * @param imageBytes image in byte array form
     * @param name image name
     */
    public void SendImage(final int size, final byte[] imageBytes, final String name) {
        Thread t = new Thread(){
            public void run() {
                try {
                    OutputStream output = socket.getOutputStream();
                    DataOutputStream dataOutputStream = new DataOutputStream(output);
                    dataOutputStream.writeInt(Integer.reverseBytes(size));
                    dataOutputStream.flush();
                    dataOutputStream.write(imageBytes, 0, size);
                    dataOutputStream.flush();
                    dataOutputStream.writeInt(Integer.reverseBytes(name.length()));
                    dataOutputStream.flush();
                    byte[] nameByte = name.getBytes("UTF-8");
                    dataOutputStream.write(nameByte, 0, name.length());
                    dataOutputStream.flush();
                } catch (Exception e) {
                    Log.e("TCP","sending: Error",e);
                }
            }
        };
        t.start();
        try {
            t.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
