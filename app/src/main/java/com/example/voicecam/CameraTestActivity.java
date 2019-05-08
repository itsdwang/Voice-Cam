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

public class CameraTestActivity extends AppCompatActivity {
    private CameraKitView cameraKitView;
    private static final int PERMISSION_REQUEST_CODE = 200;
    private static final int WRITE_REQUEST_CODE = 201;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera_test);
        cameraKitView = findViewById(R.id.camera);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
        } else {
            Toast.makeText(getApplicationContext(), "Inside onImage, PERMISSION GRANTED FOR EXTERNAL STORAGE", Toast.LENGTH_SHORT).show();
            Log.d("Debug", "Inside onImage, PERMISSION GRANTED FOR EXTERNAL STORAGE");
        }

        /*
        if (!checkPermission()) {
            Toast.makeText(getApplicationContext(), "Need to check permission", Toast.LENGTH_SHORT).show();
            requestPermission();
        } else {
            Toast.makeText(getApplicationContext(), "No need to check permission", Toast.LENGTH_SHORT).show();
        }
        */
        // requestPermission();
    }

    /*
    private boolean checkPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted
            return false;
        }
        return true;
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.CAMERA},
                PERMISSION_REQUEST_CODE);
    }
    */

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

    /*
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        Log.d("Debug", "Inside onRequestPermissionsResult");

        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(getApplicationContext(), "Permission Granted", Toast.LENGTH_SHORT).show();

                    // main logic
                } else {
                    Toast.makeText(getApplicationContext(), "Permission Denied", Toast.LENGTH_SHORT).show();
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                                != PackageManager.PERMISSION_GRANTED) {
                                Toast.makeText(getApplicationContext(), "shiiiet", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
                break;
        }
    }
    */

    public void takePhoto(View view) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted
            Toast.makeText(getApplicationContext(), "Inside onImage, PERMISSION NOT GRANTED", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getApplicationContext(), "Inside onImage, PERMISSION GRANTED", Toast.LENGTH_SHORT).show();
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted
            // Toast.makeText(getApplicationContext(), "Inside onImage, PERMISSION NOT GRANTED FOR AUDIO", Toast.LENGTH_SHORT).show();
            Log.d("Debug", "Inside onImage, PERMISSION NOT GRANTED FOR AUDIO");
        } else {
            // Toast.makeText(getApplicationContext(), "Inside onImage, PERMISSION GRANTED FOR AUDIO", Toast.LENGTH_SHORT).show();
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

                /*
                File savedPhoto = new File(Environment.getExternalStorageDirectory(), "photo.jpg");
                try {
                    Log.d("Debug", "Path is " + savedPhoto.getPath());
                    FileOutputStream outputStream = new FileOutputStream(savedPhoto.getPath());
                    outputStream.write(capturedImage);
                    outputStream.close();
                } catch (java.io.IOException e) {
                    e.printStackTrace();
                }
                */

                // FileOutputStream fOut = openFileOutput("photo.jpg", MODE_WORLD_READABLE);
                ContextWrapper cw = new ContextWrapper(getApplicationContext());
                // path to /data/data/yourapp/app_data/imageDir
                File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
                // Create imageDir
                File mypath = new File(directory,"8_07_02.jpg");

                FileOutputStream fos = null;
                Bitmap bitmapImage = BitmapFactory.decodeByteArray(capturedImage, 0, capturedImage.length);

                try {
                    fos = new FileOutputStream(mypath);
                    // Use the compress method on the BitMap object to write image to the OutputStream
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
