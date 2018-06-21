package com.example.leah.photoapp;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import static java.lang.Thread.sleep;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void stopService(View view) {
        //if(!MainActivity.this.isFinishing()) {
//        Thread t = new Thread(){
//            public void run() {
//                //runOnUiThread(new Runnable() {
//                   // @Override
//                   // public void run() {
//                        Toast.makeText(context, "Ending service...", Toast.LENGTH_SHORT).show();
//                  //  }
//                //});
//            }
//        };
//        t.start();
//        //}
        Toast.makeText(this, "Stopping service...", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this, ImageServiceService.class);
        stopService(intent);
    }

    public void startService(View view) {
//        Thread t = new Thread(){
//            public void run(final Activity context) {
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        Toast.makeText(context, "Starting service...", Toast.LENGTH_SHORT).show();
//                    }
//                });
//            }
//        };
        //if(!MainActivity.this.isFinishing()) {
        //}
        Intent intent = new Intent(this, ImageServiceService.class);
        try {
            sleep(50);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Toast.makeText(this, "Starting service...", Toast.LENGTH_SHORT).show();
        startService(intent);

    }

}
