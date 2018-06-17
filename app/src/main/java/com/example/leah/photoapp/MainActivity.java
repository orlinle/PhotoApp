package com.example.leah.photoapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;

public class MainActivity extends AppCompatActivity {

    protected BroadcastReceiver yourReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.setFilter();
    }

    public void stopService(View view) {
        Intent intent = new Intent(this, ImageServiceService.class);
        stopService(intent);
    }

    public void startService(View view) {
        Intent intent = new Intent(this, ImageServiceService.class);
        startService(intent);
    }

    private void setFilter() {
        final IntentFilter theFilter = new IntentFilter();
        theFilter.addAction("android.net.wifi.supplicant.CONNECTION_CHANGE");
        theFilter.addAction("android.net.wifi.STATE_CHANGE");
        this.yourReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                WifiManager wifiManager = (WifiManager) context
                        .getSystemService(Context.WIFI_SERVICE);
                NetworkInfo networkInfo = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
                if (networkInfo!= null) {
                    if (networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
                        //GET THE DIFFERENT NETWORK STATES
                        if (networkInfo.getState() == NetworkInfo.State.CONNECTED) {
                            startTransfer();    //start the transfer
                        }
                    }
                }
            }
        };
        // registers the receiver so that your sevice will listen for broadcasts
        this.registerReceiver(this.yourReceiver,theFilter);
    }

    private void startTransfer() {
        try {
            File dcim = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
            if (dcim == null) {
                return;
            }
            InetAddress serverAddr = InetAddress.getByName("10.0.2.2");
            //create a socket to make the connection with the server
            Socket socket = new Socket(serverAddr, 1234);
            try {
                //sends the message to the server
                OutputStream output = socket.getOutputStream();
                File[] pics = dcim.listFiles();
                int count = 0;
                if (pics !=null) {
                    for (File pic : pics) {
                        FileInputStream fis = new FileInputStream(pic);
                        Bitmap bm = BitmapFactory.decodeStream(fis);
                        byte[] imgbyte =  getBytesFromBitmap(bm);
                        output.write(imgbyte);
                        output.flush();
                    }
                }

            } catch (Exception e) {
                Log.e("TCP","S: Error",e);
            } finally {
                socket.close();
            }
        } catch (Exception e) {
            Log.e("TCP","C: Error",e);
        }
    }

    public byte[] getBytesFromBitmap(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 70, stream);
        return stream.toByteArray();
    }


}
