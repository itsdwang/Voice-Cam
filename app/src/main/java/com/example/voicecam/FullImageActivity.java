package com.example.voicecam;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.jsibbold.zoomage.ZoomageView;

public class FullImageActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_image);


        TextView imageTitle = findViewById(R.id.imageTitleTextView);
        View root = imageTitle.getRootView();

        // Set the activity background color to black
        root.setBackgroundColor(getResources().getColor(android.R.color.black));

        Bundle extras = getIntent().getExtras();
        try {
            byte[] byteArray = extras.getByteArray("image");
            String imageName = extras.getString("imageName");

            Bitmap bmp = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
            ZoomageView image = (ZoomageView) findViewById(R.id.fullImageView);
            image.setImageBitmap(bmp);

            imageTitle.setText(imageName);
        } catch(NullPointerException n) {
            n.printStackTrace();
        }
    }

    public void moreInfo(View view) {
        displayPopupWindow(view);
    }

    private void displayPopupWindow(View anchorView) {
        PopupWindow popup = new PopupWindow(FullImageActivity.this);
        View layout = getLayoutInflater().inflate(R.layout.popup_content, null);

        Bundle extras = getIntent().getExtras();
        String imageName = extras.getString("imageName");
        Log.d("debug","image name is " + imageName);
        TextView imgNameTextView = (TextView)layout.findViewById(R.id.imgNameTextView);

        imgNameTextView.setText(imageName);

        popup.setContentView(layout);
        // Set content width and height
        popup.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
        popup.setWidth(WindowManager.LayoutParams.WRAP_CONTENT);
        // Closes the popup window when touch outside of it - when looses focus
        popup.setOutsideTouchable(true);
        popup.setFocusable(true);
        // Show anchored to button
        popup.showAsDropDown(anchorView);
    }
}
