package com.androidclass.uwchat;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ListView;

import com.androidclass.uwchat.models.Message;
import com.androidclass.uwchat.util.Constants;
import com.firebase.client.Firebase;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private Firebase uwChatReference;
    private MessageListAdapter messageListAdapter;
    private ListView messageListView;
    private String messageContentText = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        /**
         * Initialize the Firebase library
         * Create Firebase object referencing the database URL, connect to messages child node
         */
        Firebase.setAndroidContext(this);
        uwChatReference = new Firebase(Constants.FIREBASE_URL).child("messages");

        /**
         * Get a reference to the list view in the android layout
         */
        messageListView = (ListView) findViewById(R.id.message_list);

        /**
         * Set up the list adapter, controls how messages are displayed in the message list
         * note we pass in the reference to the firebase messages, this list adapter extends a
         * special FirebaseListAdapter that accepts a POJO model class that maps Firebase key values
         * to a Java object for display in your Android layouts
         */
        messageListAdapter = new MessageListAdapter(this, Message.class, R.layout.message, uwChatReference);
        messageListView.setAdapter(messageListAdapter);

        /**
         * Our activity came with a floating action button, let's use this to add a message
         */
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // let's use an alert dialog and some text inputs for our message form
                AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                builder.setTitle("Send a Message");

                // Set up the inputs
                final EditText messageContentView = new EditText(view.getContext());
                builder.setView(messageContentView);

                // Set up the buttons
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // when the user submits, let's get the text from the input
                        messageContentText = messageContentView.getText().toString();

                        // let's push this input into Firebase
                        Map<String, String> message = new HashMap<String, String>();
                        message.put("content", messageContentText);
                        message.put("sender", "The class");
                        uwChatReference.push().setValue(message);
                    }
                });

                // user clicks cancel, don't send anything
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        // getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}