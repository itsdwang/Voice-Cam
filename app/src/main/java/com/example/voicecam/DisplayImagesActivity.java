package com.example.voicecam;

import android.app.AlertDialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.net.Uri;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;

public class DisplayImagesActivity extends AppCompatActivity {
    GridView gridView;
    ImageAdapter imgAdapter = null;
    ImageItem[] imgItemArr;
    String[] options = {"Rename", "Delete", "Share"};
    String renamedFile = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_images);

        gridView = (GridView) findViewById(R.id.gridView);

        ContextWrapper cw = new ContextWrapper(getApplicationContext());
        File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);

        File listFile[] = directory.listFiles();

        // can definitely improve below for better runtime
        Arrays.sort(listFile, new Comparator<File>(){
            public int compare(File f1, File f2)
            {
                return Long.valueOf(f1.lastModified()).compareTo(f2.lastModified());
            } });

        imgItemArr = new ImageItem[listFile.length];

        for (int x = 0; x < listFile.length; x++) {
            File f = listFile[x];
            String filePath = f.getPath();
            Bitmap bitmap = BitmapFactory.decodeFile(filePath);

            imgItemArr[x] = new ImageItem(f.getName(), f, bitmap);
        }

        ArrayList<ImageItem> arrList = new ArrayList<ImageItem>(Arrays.asList(imgItemArr));

        // Reverse ArrayList to show newest image first
        Collections.reverse(arrList);
        imgAdapter = new ImageAdapter(this, R.layout.grid_item_layout, arrList);
        gridView.setAdapter(imgAdapter);

        gridView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            // ImageView imageView = (ImageView) findViewById(R.id.rowImage);

            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View v, int position, long id) {
                ImageItem i = (ImageItem) parent.getItemAtPosition(position);
                Bitmap bmp = i.getImage();

                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bmp.compress(Bitmap.CompressFormat.JPEG, 60, stream);
                byte[] byteArray = stream.toByteArray();
                Intent intent = new Intent(DisplayImagesActivity.this, FullImageActivity.class);

                intent.putExtra("picture", byteArray);
                startActivity(intent);

                return true;
            }
        });


        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ImageItem i = (ImageItem) parent.getItemAtPosition(position);
                final File f = i.getFile();

                AlertDialog.Builder builder = new AlertDialog.Builder(DisplayImagesActivity.this);
                builder.setTitle("Options");
                builder.setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if ("Rename".equals(options[which])){
                            Log.d(this.getClass().getSimpleName(), "Rename clicked");

                            // Create another action dialog
                            AlertDialog.Builder nameBuilder = new AlertDialog.Builder(DisplayImagesActivity.this);
                            nameBuilder.setTitle("Rename Photo");

                            final EditText input = new EditText(DisplayImagesActivity.this);
                            input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_NORMAL);
                            nameBuilder.setView(input);

                            nameBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                renamedFile = input.getText().toString();
                                Log.d("Debug", "Renamed File: " + renamedFile + ".jpg");

                                renameFile(f, renamedFile + ".jpg");

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

                            AlertDialog ad = nameBuilder.show();
                        }
                        else if ("Delete".equals(options[which])){
                            Log.d("Debug", "Delete clicked");

                            f.delete();

                            // Refresh activity
                            finish();
                            startActivity(getIntent());
                        }
                        else if ("Share".equals(options[which])){
                            Log.d("Debug", "Share clicked");

                            // Remove the strict mode policies
                            StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
                            StrictMode.setVmPolicy(builder.build());

                            Log.d(this.getClass().getSimpleName(), "Grid item clicked");
                            Uri u = Uri.fromFile(f);

                            Intent shareIntent = new Intent(Intent.ACTION_SEND);
                            shareIntent.setType("Image/*");
                            shareIntent.putExtra(Intent.EXTRA_STREAM, u);

                            startActivityForResult(shareIntent, 1);
                            // startActivity(shareIntent);
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
