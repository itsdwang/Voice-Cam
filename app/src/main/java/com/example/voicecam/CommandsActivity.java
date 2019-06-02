package com.example.voicecam;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.app.AlertDialog;

import java.util.ArrayList;

public class CommandsActivity extends AppCompatActivity {
    private static final String TAG = "CommandsActivity";
    private String m_Text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_commands);

        ListView mListView = (ListView) findViewById(R.id.listView);

        // Load whatever commands are in sharedPreferences (either default or user-changed ones)
        final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);

        ArrayList<Command> cmdList = new ArrayList<>();

        String photoVoiceCmd = preferences.getString(Command.TAKE_PHOTO, "Take a photo");
        Command photoCmd = new Command(photoVoiceCmd, Command.TAKE_PHOTO);

        String galleryVoiceCmd = preferences.getString(Command.OPEN_GALLERY, "Open the gallery");
        Command galleryCmd = new Command(galleryVoiceCmd, Command.OPEN_GALLERY);

        String flashVoiceCmd = preferences.getString(Command.TOGGLE_FLASH, "Toggle the flash");
        Command flashCmd = new Command(flashVoiceCmd, Command.TOGGLE_FLASH);

        String toggleFaceVoiceCmd = preferences.getString(Command.FLIP_CAMERA, "flip the camera");
        Command toggleFaceCmd = new Command(toggleFaceVoiceCmd, Command.FLIP_CAMERA);

        String editCommandVoiceCmd = preferences.getString(Command.EDIT_COMMAND, "edit voice commands");
        Command editCommandCmd = new Command(editCommandVoiceCmd, Command.EDIT_COMMAND);

        cmdList.add(photoCmd);
        cmdList.add(galleryCmd);
        cmdList.add(flashCmd);
        cmdList.add(toggleFaceCmd);
        cmdList.add(editCommandCmd);


        CommandListAdapter adapter = new CommandListAdapter(this, R.layout.cmds_adapter_view_layout, cmdList);
        mListView.setAdapter(adapter);

        LayoutInflater layoutInflater = getLayoutInflater();
        ViewGroup myHeader = (ViewGroup)layoutInflater.inflate(R.layout.header, mListView, false);
        mListView.addHeaderView(myHeader, null, false);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final Command c = (Command) parent.getItemAtPosition(position);
                Log.d("Debug", "Command: " + c.getCommand());
                Log.d("Debug", "Action: " + c.getAction());

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
                        String cmdAction = c.getAction();

                        // Change SharedPreferences
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putString(cmdAction, m_Text);
                        editor.apply();

                        // Refresh the activity without animation
                        finish();
                        overridePendingTransition( 0, 0);
                        startActivity(getIntent());
                        overridePendingTransition( 0, 0);
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
