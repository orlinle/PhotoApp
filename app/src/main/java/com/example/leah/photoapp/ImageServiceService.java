package com.example.leah.photoapp;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.widget.Toast;

public class ImageServiceService extends Service {

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
        //add code of service
    }

    public int onStartCommand(Intent intent, int flag, int startId) {
        Toast.makeText(this, "Starting service...", Toast.LENGTH_SHORT).show();
        return START_STICKY;
    }

    public void onDestroy() {
        Toast.makeText(this, "Ending service...", Toast.LENGTH_SHORT).show();
    }
}
