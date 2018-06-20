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
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.Socket;

public class ImageServiceService extends Service {

    private BroadcastReceiver yourReceiver;

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
        Log.e("setFilter","in set filter");
        final IntentFilter theFilter = new IntentFilter();
        theFilter.addAction("android.net.wifi.supplicant.CONNECTION_CHANGE");
        theFilter.addAction("android.net.wifi.STATE_CHANGE");
        this.yourReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.e("receive","in onReceive");
                WifiManager wifiManager = (WifiManager) context
                        .getSystemService(Context.WIFI_SERVICE);
                NetworkInfo networkInfo = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
                if (networkInfo!= null) {
                    Log.e("receive","network is not null");
                    if (networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
                        Log.e("receive","network typ eis wifi");
                        //GET THE DIFFERENT NETWORK STATES
                        if (networkInfo.getState() == NetworkInfo.State.CONNECTED) {
                            Log.e("receive","state is connected. gonna call start transfer");
                            startTransfer();    //start the transfer
                        }
                    }
                }
            }
        };
        // registers the receiver so that your sevice will listen for broadcasts
        this.registerReceiver(this.yourReceiver,theFilter);
        //LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(this.yourReceiver,theFilter);
    }

    private void startTransfer() {
        Log.e("startTransfer","in start transfer");
        try {
            File dcim = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
            if (dcim == null) {
                return;
            }
            try {
                File[] pics = dcim.listFiles();
                //final List<File> pics = new ArrayList<File>();
                //recursively get pics with (dcim.getPath(), pics)
                if (pics != null) {
                    TcpClient client = TcpClient.GetInstance();
                    for (File pic : pics) {
                        FileInputStream fis = new FileInputStream(pic);
                        Bitmap bm = BitmapFactory.decodeStream(fis);
                        byte[] imgbyte = getBytesFromBitmap(bm);
                        int imgSize = imgbyte.length;
                        int index = pic.getName().lastIndexOf('\\');
                        String imgName = pic.getName().substring(index + 1);
                        client.SendImage(imgSize, imgbyte, imgName);
                    }
                }
            } catch (Exception e) {
                Log.e("file","Error", e);
            }
        } catch (Exception e ) {
            Log.e("DCIM","Error", e);
        }
    }

    public byte[] getBytesFromBitmap(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 70, stream);
        return stream.toByteArray();
    }

}
