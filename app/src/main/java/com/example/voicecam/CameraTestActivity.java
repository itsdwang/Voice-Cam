package com.example.voicecam;

import android.Manifest;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.camerakit.CameraKitView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class CameraTestActivity extends AppCompatActivity {
    private CameraKitView cameraKitView;
    private static final int PERMISSION_REQUEST_CODE = 200;
    private static final int WRITE_REQUEST_CODE = 201;
    private static int imgNum = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera_test);
        cameraKitView = findViewById(R.id.camera);
        Button button = findViewById(R.id.takePhotoBtn);
        
        /*need to check if data was received & need to know where to put the wait
            Intent intent = getIntent();
            String message = intent.getStringExtra("message");
            if (message.equals("take a picture")) {
                try {
                    TimeUnit.SECONDS.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                takePhoto(button);
            }
         */

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
        } else {
            Toast.makeText(getApplicationContext(), "Inside onImage, PERMISSION GRANTED FOR EXTERNAL STORAGE", Toast.LENGTH_SHORT).show();
            Log.d("Debug", "Inside onImage, PERMISSION GRANTED FOR EXTERNAL STORAGE");
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        cameraKitView.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        cameraKitView.onResume();
    }

    @Override
    protected void onPause() {
        cameraKitView.onPause();
        super.onPause();
    }

    @Override
    protected void onStop() {
        cameraKitView.onStop();
        super.onStop();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        Log.d("Debug", "Inside onRequestPermissionsResult");

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        cameraKitView.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    public void takePhoto(View view) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted
            Toast.makeText(getApplicationContext(), "Inside onImage, CAMERA PERMISSION NOT GRANTED", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getApplicationContext(), "Inside onImage, CAMERA PERMISSION GRANTED", Toast.LENGTH_SHORT).show();
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted
            Log.d("Debug", "Inside onImage, PERMISSION NOT GRANTED FOR AUDIO");
        } else {
            Log.d("Debug", "Inside onImage, PERMISSION GRANTED FOR AUDIO");
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted
            Toast.makeText(getApplicationContext(), "Inside onImage, PERMISSION NOT GRANTED FOR EXTERNAL STORAGE", Toast.LENGTH_SHORT).show();
            Log.d("Debug", "Inside onImage, PERMISSION NOT GRANTED FOR EXTERNAL STORAGE");

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, WRITE_REQUEST_CODE);
        } else {
            Toast.makeText(getApplicationContext(), "Inside onImage, PERMISSION GRANTED FOR EXTERNAL STORAGE", Toast.LENGTH_SHORT).show();
            Log.d("Debug", "Inside onImage, PERMISSION GRANTED FOR EXTERNAL STORAGE");
        }

        cameraKitView.captureImage(new CameraKitView.ImageCallback() {
            @Override

            public void onImage(CameraKitView cameraKitView, final byte[] capturedImage) {
                Log.d("Debug", "Inside takePhoto method");

                ContextWrapper cw = new ContextWrapper(getApplicationContext());

                // path to /data/data/yourapp/app_data/imageDir

                String currentDateTimeString = DateFormat.getDateTimeInstance().format(new Date());

                File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
                File mypath = new File(directory,"img" + imgNum + "_" + currentDateTimeString + ".jpg");
                imgNum++;

                FileOutputStream fos = null;
                Bitmap bitmapImage = BitmapFactory.decodeByteArray(capturedImage, 0, capturedImage.length);

                try {
                    fos = new FileOutputStream(mypath);
                    bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, fos);
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    try {
                        fos.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }
}
