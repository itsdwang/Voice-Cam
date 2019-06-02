package com.example.voicecam;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.content.DialogInterface;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;

import java.util.ArrayList;

public class ImageAdapter extends ArrayAdapter<ImageItem> {
    private Context context;
    private int layout;
    private ArrayList<ImageItem> dbList;

    public ImageAdapter(Context context, int layout, ArrayList<ImageItem> images) {
        super(context, 0, images);
        this.context = context;
        this.layout = layout;
        this.dbList = images;
    }

    @Override
    public int getCount() {
        return dbList.size();
    }

    @Override
    public ImageItem getItem(int position) {
        return dbList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    private class ViewHolder{
        ImageView imageView;
        TextView title;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        ViewHolder holder = new ViewHolder();

        if (row == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(layout, null);

            // holder.title = (TextView) row.findViewById(R.id.rowText);
            holder.imageView = (ImageView) row.findViewById(R.id.rowImage);
            row.setTag(holder);
        } else {
            holder = (ViewHolder) row.getTag();
        }

        ImageItem i = dbList.get(position);
        // holder.title.setText(i.getName());
        holder.imageView.setImageBitmap(i.getImage());

        return row;
    }
}
