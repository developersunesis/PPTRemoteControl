package com.solvworthcorporation.pptremotecontrol;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

public class Splash extends AppCompatActivity {

    static final int MY_PERMISSIONS_REQUEST_WRITE_STATE= 1002;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_splash);
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            // Here, thisActivity is the current activity
            // Permission is not granted
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        MY_PERMISSIONS_REQUEST_WRITE_STATE);
            } else {
                // sees the explanation, try again to request the permission.
                // No explanation needed; request the permission
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        MY_PERMISSIONS_REQUEST_WRITE_STATE);
            }
        } else {
            sleep();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_WRITE_STATE:
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    sleep();
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(getApplicationContext(), "Tenue Remote Control needs your permission to proceed", Toast.LENGTH_LONG).show();
                    finish();
                }
                break;
        }
    }

    public void sleep() {
        new Thread() {

            public void run() {

                try {
                    // This is just a tmp sleep so that we can emulate something loading
                    Thread.sleep(5000);
                    // Use this handler so than you can update the UI from a thread
                    Refresh.sendEmptyMessage(0);
                } catch (Exception ignored) {
                }
            }
        }.start();
    }

    // Refresh handler, necessary for updating the UI in a/the thread
    @SuppressLint("HandlerLeak")
    Handler Refresh = new Handler() {
        public void handleMessage(Message msg) {

            switch (msg.what) {
                case 0:
                    startActivity(new Intent(getApplicationContext(), EnterIP.class));
                    finish();
                    break;
            }
        }
    };
}