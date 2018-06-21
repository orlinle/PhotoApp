package com.example.leah.photoapp;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    /**
     * stop the service
     * @param view view
     */
    public void stopService(View view) {
        Toast.makeText(this, "Stopping service...", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this, ImageServiceService.class);
        stopService(intent);
    }

    /**
     * start the service
     * @param view view
     */
    public void startService(View view) {
        Toast.makeText(this, "Starting service...", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this, ImageServiceService.class);
        startService(intent);
    }
}
