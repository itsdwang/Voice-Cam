package com.example.voicecam;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class CommandListAdapter extends ArrayAdapter<Command> {
    private static final String TAG = "CommandListAdapter";

    private Context mContext;
    int mResource;

    // private ListView listView;

    public CommandListAdapter(Context context, int resource, ArrayList<Command> objects ){
        super(context, resource, objects);
        mContext = context;
        mResource = resource;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        String command = getItem(position).getCommand();
        String action = getItem(position).getAction();

        Command c = new Command(command, action);

        LayoutInflater inflater = LayoutInflater.from(mContext);
        convertView = inflater.inflate(mResource, parent, false);

        // View header = (View)convertView.getLayoutInflater().inflate(R.layout.header, null);
        // ViewGroup header = (ViewGroup) inflater.inflate(R.layout.header, convertView, false);

        TextView commandTextView = convertView.findViewById(R.id.commandTextView);
        TextView actionTextView = convertView.findViewById(R.id.actionTextView);

        commandTextView.setText(command);
        actionTextView.setText(action);

        return convertView;
    }

}
