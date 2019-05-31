package com.example.voicecam;

import android.app.AlertDialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
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

import com.uvstudio.him.photofilterlibrary.PhotoFilter;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;

public class DisplayImagesActivity extends AppCompatActivity {
    GridView gridView;
    ImageAdapter imgAdapter = null;
    ImageItem[] imgItemArr;
    String[] options = {"Rename", "Delete", "Share", "Filters"};
    String[] filterOptions = {"One", "Two", "Three", "Four", "Five", "Six", "Seven", "Eight", "Nine", "Ten", "Eleven", "Twelve", "Thirteen", "Fourteen", "Fifteen", "Sixteen"};
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
                final ImageItem i = (ImageItem) parent.getItemAtPosition(position);
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

                            // Refresh activity without animation
                            finish();
                            overridePendingTransition( 0, 0);
                            startActivity(getIntent());
                            overridePendingTransition( 0, 0);
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

                        // Refresh activity without animation
                        finish();
                        overridePendingTransition( 0, 0);
                        startActivity(getIntent());
                        overridePendingTransition( 0, 0);
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
                    }
                    else if ("Filters".equals(options[which])) {
                        final String oldFileName = f.getName();
                        final AlertDialog.Builder filterBuilder = new AlertDialog.Builder(DisplayImagesActivity.this);
                        final PhotoFilter photoFilter = new PhotoFilter();


                        filterBuilder.setTitle("Filters");
                        filterBuilder.setItems(filterOptions, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if ("One".equals(filterOptions[which])){
                                    Bitmap newPhoto = photoFilter.one(getApplicationContext(), i.getImage());
                                    saveFile(oldFileName + "_one", newPhoto);
                                } else if ("Two".equals(filterOptions[which])) {
                                    Bitmap newPhoto = photoFilter.two(getApplicationContext(), i.getImage());
                                    saveFile(oldFileName + "_two", newPhoto);
                                } else if ("Three".equals(filterOptions[which])) {
                                    Bitmap newPhoto = photoFilter.three(getApplicationContext(), i.getImage());
                                    saveFile(oldFileName + "_three", newPhoto);
                                } else if ("Four".equals(filterOptions[which])) {
                                    Bitmap newPhoto = photoFilter.four(getApplicationContext(), i.getImage());
                                    saveFile(oldFileName + "_four", newPhoto);
                                } else if ("Five".equals(filterOptions[which])) {
                                    Bitmap newPhoto = photoFilter.five(getApplicationContext(), i.getImage());
                                    saveFile(oldFileName + "_five", newPhoto);
                                } else if ("Six".equals(filterOptions[which])) {
                                    Bitmap newPhoto = photoFilter.six(getApplicationContext(), i.getImage());
                                    saveFile(oldFileName + "_six", newPhoto);
                                } else if ("Seven".equals(filterOptions[which])) {
                                    Bitmap newPhoto = photoFilter.seven(getApplicationContext(), i.getImage());
                                    saveFile(oldFileName + "_seven", newPhoto);
                                } else if ("Eight".equals(filterOptions[which])) {
                                    Bitmap newPhoto = photoFilter.eight(getApplicationContext(), i.getImage());
                                    saveFile(oldFileName + "_eight", newPhoto);
                                } else if ("Nine".equals(filterOptions[which])) {
                                    Bitmap newPhoto = photoFilter.nine(getApplicationContext(), i.getImage());
                                    saveFile(oldFileName + "_nine", newPhoto);
                                } else if ("Ten".equals(filterOptions[which])) {
                                    Bitmap newPhoto = photoFilter.ten(getApplicationContext(), i.getImage());
                                    saveFile(oldFileName + "_ten", newPhoto);
                                } else if ("Eleven".equals(filterOptions[which])) {
                                    Bitmap newPhoto = photoFilter.eleven(getApplicationContext(), i.getImage());
                                    saveFile(oldFileName + "_eleven", newPhoto);
                                } else if ("Twelve".equals(filterOptions[which])) {
                                    Bitmap newPhoto = photoFilter.twelve(getApplicationContext(), i.getImage());
                                    saveFile(oldFileName + "_twelve", newPhoto);
                                } else if ("Thirteen".equals(filterOptions[which])) {
                                    Bitmap newPhoto = photoFilter.thirteen(getApplicationContext(), i.getImage());
                                    saveFile(oldFileName + "_thirteen", newPhoto);
                                } else if ("Fourteen".equals(filterOptions[which])) {
                                    Bitmap newPhoto = photoFilter.fourteen(getApplicationContext(), i.getImage());
                                    saveFile(oldFileName + "_fourteen", newPhoto);
                                } else if ("Fifteen".equals(filterOptions[which])) {
                                    Bitmap newPhoto = photoFilter.fifteen(getApplicationContext(), i.getImage());
                                    saveFile(oldFileName + "_fifteen", newPhoto);
                                } else if ("Sixteen".equals(filterOptions[which])) {
                                    Bitmap newPhoto = photoFilter.sixteen(getApplicationContext(), i.getImage());
                                    saveFile(oldFileName + "_sixteen", newPhoto);
                                }

                                finish();
                                overridePendingTransition( 0, 0);
                                startActivity(getIntent());
                                overridePendingTransition( 0, 0);
                            }
                        });

                        filterBuilder.show();

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

    public void saveFile(String fileName, Bitmap bm) {
        ContextWrapper cw = new ContextWrapper(getApplicationContext());
        // path to /data/data/yourapp/app_data/imageDir
        File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
        // Create imageDir
        File mypath = new File(directory,fileName);

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(mypath);
            // Use the compress method on the BitMap object to write image to the OutputStream
            bm.compress(Bitmap.CompressFormat.JPEG, 60, fos);
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
}
