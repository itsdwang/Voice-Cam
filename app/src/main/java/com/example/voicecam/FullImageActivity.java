package com.example.voicecam;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import uk.co.senab.photoview.PhotoViewAttacher;

public class FullImageActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_image);

        Bundle extras = getIntent().getExtras();
        try {
            byte[] byteArray = extras.getByteArray("picture");

            Bitmap bmp = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
            ImageView image = (ImageView) findViewById(R.id.fullImageView);
            image.setImageBitmap(bmp);

            PhotoViewAttacher pAttacher = new PhotoViewAttacher(image);
            pAttacher.update();
        } catch(NullPointerException n) {
            n.printStackTrace();
        }
    }
}
