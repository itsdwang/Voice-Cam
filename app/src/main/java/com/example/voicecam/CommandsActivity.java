package com.example.voicecam;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.app.AlertDialog;


import java.util.ArrayList;
import java.util.Map;

public class CommandsActivity extends AppCompatActivity {
    private static final String TAG = "CommandsActivity";
    String m_Text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_commands);

        ListView mListView = (ListView) findViewById(R.id.listView);

        // Let's see all sharedpreference vals

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);

        Map<String, ?> allEntries = preferences.getAll();
        for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
            Log.d("map values", entry.getKey() + ": " + entry.getValue().toString());
        }

        Command c1 = new Command("Take a picture", Command.TAKE_PHOTO);
        Command c2 = new Command("Open the gallery", Command.OPEN_GALLERY);
        Command c3 = new Command("Toggle the flash", Command.TOGGLE_FLASH);

        ArrayList<Command> cmdList = new ArrayList<>();
        cmdList.add(c1);
        cmdList.add(c2);
        cmdList.add(c3);

        CommandListAdapter adapter = new CommandListAdapter(this, R.layout.cmds_adapter_view_layout, cmdList);
        mListView.setAdapter(adapter);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                AlertDialog.Builder builder = new AlertDialog.Builder(CommandsActivity.this);
                builder.setTitle("Edit voice command");

                // Set up the input
                final EditText input = new EditText(CommandsActivity.this);

                // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
                input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_NORMAL);
                builder.setView(input);

                // Set up the buttons
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        m_Text = input.getText().toString();
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                builder.show();

            }
        });
    }
}
