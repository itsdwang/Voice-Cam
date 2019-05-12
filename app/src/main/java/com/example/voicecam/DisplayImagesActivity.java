package com.example.voicecam;

import android.app.AlertDialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

public class DisplayImagesActivity extends AppCompatActivity {
    GridView gridView;
    ImageAdapter imgAdapter = null;
    ArrayList<ImageItem> list;
    ImageItem[] imgItemArr;
    String[] options = {"Rename", "Delete", "Share"};
    private String m_Text = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_images);

        gridView = (GridView) findViewById(R.id.gridView);
        list = new ArrayList<>();

        ContextWrapper cw = new ContextWrapper(getApplicationContext());
        File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);

        File listFile[] = directory.listFiles();

        // can definitely improve below for better runtime
        Arrays.sort(listFile, new Comparator<File>(){
            public int compare(File f1, File f2)
            {
                return Long.valueOf(f1.lastModified()).compareTo(f2.lastModified());
            } });

        imgItemArr = new ImageItem[listFile.length]; // make it the same len. as listFile

        for (int x = 0; x < listFile.length; x++) {
            File f = listFile[x];
            String filePath = f.getPath();
            Bitmap bitmap = BitmapFactory.decodeFile(filePath);

            imgItemArr[x] = new ImageItem(f.getName(), f, bitmap);
        }

        ArrayList<ImageItem> arrList = new ArrayList<ImageItem>(Arrays.asList(imgItemArr));

        // Reverse ArrayList to show newest pic first
        Collections.reverse(arrList);
        imgAdapter = new ImageAdapter(this, R.layout.grid_item_layout, arrList);
        gridView.setAdapter(imgAdapter);

        // Below not necessary for now - checks if file is image
        /*
        for(int i = 0; i < listFile.length; i++) {
            // Check if the file is an image
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            Bitmap bitmap = BitmapFactory.decodeFile(listFile[i].toString(), options);
            if (options.outWidth != -1 && options.outHeight != -1) {
                Log.d("debug", listFile[i] + " is an image");
            }
            else {
                Log.d("debug", listFile[i] + " is not an image");
            }
        }
        */

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            ImageItem i = (ImageItem) parent.getItemAtPosition(position);
            final File f = i.getFile();

            String title = i.getName();
            Toast.makeText(DisplayImagesActivity.this,"Image Title:" + title, Toast.LENGTH_SHORT).show();

            AlertDialog.Builder builder = new AlertDialog.Builder(DisplayImagesActivity.this);
            builder.setTitle("Options");
            builder.setItems(options, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                if ("Rename".equals(options[which])){
                    Log.d("debug", "rename was clicked");

                    // Create another action dialog
                    AlertDialog.Builder nameBuilder = new AlertDialog.Builder(DisplayImagesActivity.this);
                    nameBuilder.setTitle("Rename Photo");

                    final EditText input = new EditText(DisplayImagesActivity.this);
                    // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
                    input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_NORMAL);
                    nameBuilder.setView(input);

                    nameBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            m_Text = input.getText().toString();
                            Log.d("Debug", "RENAMED File is: " + m_Text + ".jpg");

                            renameFile(f, m_Text + ".jpg");

                            // Refresh activity


                            finish();
                            startActivity(getIntent());
                        }
                    });

                    nameBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });

                    Log.d("Debug", "RENAMED File is: " + m_Text);
                    AlertDialog ad = nameBuilder.show();

                }
                else if ("Delete".equals(options[which])){
                    Log.d("debug", "delete was clicked");
                    f.delete();

                    // Refresh activity
                    finish();
                    startActivity(getIntent());
                }
                else if ("Share".equals(options[which])){
                    Log.d("debug", "grid item was clicked");
                }
                }
            });
            builder.show();
            }
        });
    }

    public void renameFile(File f, String s) {
        ContextWrapper cw = new ContextWrapper(getApplicationContext());
        File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);


        File newFile = new File(directory, s);
        f.renameTo(newFile);
    }
}
