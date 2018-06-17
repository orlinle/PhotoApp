package com.example.leah.photoapp;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Environment;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;

public class ImageServiceService extends Service {

    protected BroadcastReceiver yourReceiver;

    public ImageServiceService() { }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        setFilter();
    }

    public int onStartCommand(Intent intent, int flag, int startId) {
        Toast.makeText(this, "Starting service...", Toast.LENGTH_SHORT).show();
        return START_STICKY;
    }

    public void onDestroy() {
        Toast.makeText(this, "Ending service...", Toast.LENGTH_SHORT).show();
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
