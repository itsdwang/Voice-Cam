package com.example.voicecam;

import android.Manifest;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaActionSound;
import android.net.Uri;
import android.os.Build;
import android.os.CountDownTimer;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import android.os.Handler;

import com.camerakit.CameraKit;
import com.camerakit.CameraKitView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class CameraTestActivity extends AppCompatActivity {
    private CameraKitView cameraKitView;
    private static final int PERMISSION_REQUEST_CODE = 200;
    private static final int WRITE_REQUEST_CODE = 201;
    private static int imgNum = 1;
    private TextView countdownTextView;
    SpeechRecognizer mSpeechRecognizer;
    Intent mSpeechRecognizerIntent;
    EditText editText;
    private static int countdownLen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera_test);
        cameraKitView = findViewById(R.id.camera);
        countdownTextView = findViewById(R.id.countdownTextView);

        Intent intent = getIntent();
        checkPermission();

        mSpeechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
        mSpeechRecognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        mSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);

        mSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        mSpeechRecognizer.setRecognitionListener(new RecognitionListener() {
            @Override
            public void onReadyForSpeech(Bundle params) {

            }

            @Override
            public void onBeginningOfSpeech() {

            }

            @Override
            public void onRmsChanged(float rmsdB) {

            }

            @Override
            public void onBufferReceived(byte[] buffer) {

            }

            @Override
            public void onEndOfSpeech() {

            }

            @Override
            public void onError(int error) {

            }

            @Override
            public void onResults(Bundle results) {
                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(CameraTestActivity.this);
                String timer = sharedPreferences.getString("example_text", "3.0");
                countdownLen = Integer.parseInt(timer);

                ArrayList<String> matches = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);

                if(matches != null) {
                    // editText.setText(matches.get(0));

                    if (matches.get(0).equals("take a picture")) {
                        Log.d("Debug", "Take a picture was said");

                        final CountDownTimer countDownTimer = new CountDownTimer(countdownLen * 1000, 1000) {
                            @Override
                            public void onTick(long millisUntilFinished) {
                                countdownTextView.setText("" + (int) (millisUntilFinished / 1000));
                            }

                            @Override
                            public void onFinish() {
                                countdownTextView.setText("");
                                takePhoto(cameraKitView);

                            }
                        }.start();
                    }
                }
            }

            @Override
            public void onPartialResults(Bundle partialResults) {

            }

            @Override
            public void onEvent(int eventType, Bundle params) {

            }
        });

        findViewById(R.id.voiceCmdBtn).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch(motionEvent.getAction()) {
                    case MotionEvent.ACTION_UP:
                        mSpeechRecognizer.stopListening();
                        // editText.setHint("Voice input will be seen here");
                        break;
                    case MotionEvent.ACTION_DOWN:
                        // editText.setText("");
                        // editText.setHint("Listening...");
                        mSpeechRecognizer.startListening(mSpeechRecognizerIntent);
                        break;
                }

                return false;
            }
        });

        /*
        try {
            String message = intent.getStringExtra("message");
            Log.d("Debug", "Voice command is: " + message);

            if (message.equals("take a picture")) {
                Log.d("Debug", "Take a picture was said");

                final CountDownTimer countDownTimer = new CountDownTimer(4000, 1000) {
                    @Override
                    public void onTick(long millisUntilFinished) {
                        countdownTextView.setText("" + (int) (millisUntilFinished / 1000));
                    }

                    @Override
                    public void onFinish() {
                        countdownTextView.setText("");
                        takePhoto(cameraKitView);

                    }
                }.start();
            }
        } catch(NullPointerException e) {
            e.printStackTrace();
        }
        */

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
        } else {
            Log.d("Debug", "Permission granted for external storage");
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
        Log.d("Debug", "Inside takePhoto method");

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            Log.d("Debug", "Camera permission not granted");
        } else {
            Log.d("Debug", "Camera permission granted");
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {
            Log.d("Debug", "Audio permission not granted");
        } else {
            Log.d("Debug", "Audio permission granted");
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            Log.d("Debug", "Permission not granted for external storage");

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, WRITE_REQUEST_CODE);
        } else {
            Log.d("Debug", "Permission granted for external storage");
        }

        // MediaActionSound sound = new MediaActionSound();
        // sound.play(MediaActionSound.SHUTTER_CLICK);

        cameraKitView.captureImage(new CameraKitView.ImageCallback() {
            @Override
            public void onImage(CameraKitView cameraKitView, final byte[] capturedImage) {
                Log.d("Debug", "Inside takePhoto method, captureImage");
                Toast.makeText(getApplicationContext(), "Photo taken", Toast.LENGTH_SHORT).show();

                // Play shutter sound
                MediaActionSound sound = new MediaActionSound();
                sound.play(MediaActionSound.SHUTTER_CLICK);

                ContextWrapper cw = new ContextWrapper(getApplicationContext());
                String currentDateTimeString = DateFormat.getDateTimeInstance().format(new Date());

                // path to /data/data/VoiceCam/app_data/imageDir
                File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
                File myPath = new File(directory,currentDateTimeString + ".jpg");
                imgNum++;

                FileOutputStream fos = null;
                Bitmap bitmapImage = BitmapFactory.decodeByteArray(capturedImage, 0, capturedImage.length);

                try {
                    fos = new FileOutputStream(myPath);
                    bitmapImage.compress(Bitmap.CompressFormat.JPEG, 60, fos);
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


    public void flipCamera(View view) {
        cameraKitView.toggleFacing();
    }

    public void setFlash(View view) {
        if(cameraKitView.getFlash() == CameraKit.FLASH_OFF){
            cameraKitView.setFlash(CameraKit.FLASH_ON);
            Toast.makeText(getApplicationContext(), "Flash on", Toast.LENGTH_SHORT).show();
        } else{
            cameraKitView.setFlash(CameraKit.FLASH_OFF);
            Toast.makeText(getApplicationContext(), "Flash off", Toast.LENGTH_SHORT).show();
        }
    }

    public void accessSettings(View view) {
        Intent settingsIntent = new Intent(this, SettingsActivity.class);
        startActivity(settingsIntent);
    }

    public void getSettings(View view) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        // float timer = sharedPreferences.getFloat("example_text", ((float)(3.0)));
        String timer = sharedPreferences.getString("example_text", "3.0");
        countdownLen = Integer.parseInt(timer);
        Log.d("Debug", "Camera timer is " + timer + "seconds");
    }

    public void accessGallery(View view) {
        Intent intent = new Intent(CameraTestActivity.this, DisplayImagesActivity.class);
        startActivity(intent);
    }

    private void checkPermission() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if(!(ContextCompat
                    .checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED)) {
                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                        Uri.parse("package:" + getPackageName()));
                startActivity(intent);
                finish();
            }
        }
    }
}
