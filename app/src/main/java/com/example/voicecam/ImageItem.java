package com.example.voicecam;

import android.graphics.Bitmap;

import java.io.File;

public class ImageItem {
    private String name;
    private File f;
    private Bitmap image;

    public ImageItem(String name, File f,  Bitmap image) {
        this.name = name;
        this.image = image;
        this.f = f;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Bitmap getImage() {
        return image;
    }

    public void setImage(Bitmap image) {
        this.image = image;
    }

    public File getFile() {
        return f;
    }

    public void setFile(File f) {
        this.f = f;
    }
}