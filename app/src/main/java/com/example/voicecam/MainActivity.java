package com.example.voicecam;

import android.content.Intent;
import android.speech.RecognizerIntent;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_SPEECH_INPUT = 1000;
    TextView textView;
    ImageButton imgBtn;
    Button cameraTestBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textView = findViewById(R.id.textView);
        imgBtn = findViewById(R.id.imageBtn);
        cameraTestBtn = findViewById(R.id.cameraTestBtn);

        imgBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                speak();
            }
        });

        cameraTestBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, CameraTestActivity.class);
                startActivity(intent);
            }
        });
    }

    private void speak() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());

        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Say something");

        // Start intent
        try {
            startActivityForResult(intent, REQUEST_CODE_SPEECH_INPUT);

        } catch(Exception e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    // receive voice input and handle


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch(requestCode) {
            case REQUEST_CODE_SPEECH_INPUT: {
                if (resultCode == RESULT_OK && null != data) {
                    // get text array from voice intent
                    ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);

                    // set to text view
                    textView.setText(result.get(0));
                }
                break;
            }
        }
    }

}
