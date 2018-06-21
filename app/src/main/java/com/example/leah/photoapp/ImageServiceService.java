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
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;

public class ImageServiceService extends Service {

    private BroadcastReceiver yourReceiver = null;

    public ImageServiceService() {
    }

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

    /**
     * start intent
     * @param intent intent
     * @param flag falg
     * @param startId start id
     * @return unique start_sticky constant int
     */
    public int onStartCommand(Intent intent, int flag, int startId) {
        return START_STICKY;
    }

    /**
     * on service destroy
     */
    public void onDestroy() {
        if (yourReceiver != null) {
            this.unregisterReceiver(yourReceiver);
        }
    }

    /**
     * set the IntentFilter to listen for wifi
     */
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
                if (networkInfo != null) {
                    if (networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
                        //GET THE DIFFERENT NETWORK STATES
                        if (networkInfo.getState() == NetworkInfo.State.CONNECTED) {
                            startTransfer();    //start the transfer
                        }
                    }
                }
            }
        };
        // registers the receiver so that your service will listen for broadcasts
        this.registerReceiver(this.yourReceiver, theFilter);
    }

    /**
     * when wifi is connected, transfer photos from camera
     */
    private void startTransfer() {
        Log.e("startTransfer", "in start transfer");
        File dcim = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);

        if (dcim == null) {
            return;
        }
        final List<File> pics = new ArrayList<File>();
        getPics(dcim, pics);
        if (pics.size() == 0)
            return;
        final TcpClient client = TcpClient.GetInstance();
        client.openCommunication();
        // progress bar
        final NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        final NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "default");
        final int notify_id = 1;
        builder.setSmallIcon(android.R.drawable.ic_menu_camera);
        builder.setContentTitle("Picture Transfer").setContentText("Transfer in progress")
                .setPriority(NotificationCompat.PRIORITY_LOW);
        final int max = pics.size();
        //thread for sending pictures
        Thread t = new Thread() {
            public void run() {
                try {
                    int progress = 0;
                    int i = 0;
                    for (File pic : pics) {
                        FileInputStream fis = new FileInputStream(pic);
                        Bitmap bm = BitmapFactory.decodeStream(fis);
                        byte[] imgbyte = getBytesFromBitmap(bm);
                        int imgSize = imgbyte.length;
                        int index = pic.getName().lastIndexOf('\\');
                        String imgName = pic.getName().substring(index + 1);
                        client.SendImage(imgSize, imgbyte, imgName);
                        //notify progress
                        Log.e("progress", String.valueOf(progress));
                        progress = Math.round((i / (float) max) * 100);
                        builder.setProgress(100, progress, false);
                        notificationManager.notify(notify_id, builder.build());
                        i++;
                    }
                    builder.setContentText("Download complete").setProgress(0, 0, false);
                    notificationManager.notify(notify_id, builder.build());
                } catch (Exception e) {
                    Log.e("file", "Error", e);
                }
            }
        };
        t.start();
        client.closeCommunication();
        Log.e("on transfer", "finished");
    }

    /**
     * search for pictures recursively in all subfolders
     * @param directory main directory
     * @param files found files
     */
    private void getPics(File directory, List<File> files) {
        // Get all the files from a directory.
        File[] fList = directory.listFiles();
        for (File file : fList) {
            if (file.isFile() && isImage(file.getName())) {
                files.add(file);
            } else if (file.isDirectory()) {
                getPics(file, files);
            }
        }
    }

    /**
     * check if file is an image
     * @param img file name
     * @return true if file is and image, false otherwise
     */
    private boolean isImage(String img) {
        List<String> imageSuffixes = new ArrayList<String>();
        imageSuffixes.add(".jpg");
        imageSuffixes.add(".png");
        imageSuffixes.add(".gif");
        imageSuffixes.add(".bmp");
        for (String suffix : imageSuffixes) {
            if (img.endsWith(suffix))
                return true;
        }
        return false;
    }

    /**
     * get bytes from Bitmap
     * @param bitmap bitmap
     * @return bytes array
     *
     */
    public byte[] getBytesFromBitmap(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 70, stream);
        return stream.toByteArray();
    }

}
