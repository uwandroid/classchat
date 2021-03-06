# Firebase Tutorial

By Larry Kubin

# What is Firebase?

Firebase is a "backend as a service" owned by Google. It provides a real-time database and API that allows developers to store and sync data across multiple clients.

# What problem does it solve?

Let's say you want to build an application that provides live updates. For instance, you may want to create a Twitter-like activity stream or build an app with live sports scores. You want to view the application on the web and mobile devices, including Android and iOS. When the application's data is updated, you want all of the devices that are displaying that data to see the change reflected on their screen immediately.

With a standard web application or Android app, you would have to do one of two things for end users to see the updated data:

1. The user would have to refresh the webpage or reload the ListView Activity on their Android app
2. You would set a timer or interval to poll a Web API to check if there is new data to fetch, then re-render the screen.

# How does Firebase handle real-time data?

Firebase stores data as JSON objects. When using Firebase, your data is a big collection of nested key-value pairs. Firebase client applications can "listen" on a particular node in the database for changes and update their UI to reflect these changes. For Android applications, Firebase provides a FirebaseListAdapter class that allows you to bind Firebase data to a ListView in your app.

It is probably best to see how this works by actually creating a Firebase account and using its built-in database browser. So let's create an account by visiting the <a href="https://firebase.com">Firebase website</a> and signing in with Google. Once you are signed in you can create a new app where your data will live as shown in the screenshot below:

![Firebase App Creation](https://docs.google.com/uc?export=download&id=0BzfHZKVI-LraVEJSUWViUjB4ZXM)

In Firebase, you connect to a database by reference a URL, so each application URL must be unique. Initially, everyone in class should create their own database. However, in our activity later on, everyone in class should use the URL above so that we can push chat messages to the same database.

Once you have created an application database in Firebase, you can browse your data using a web interface on the Firebase website. You can use this interface to add new key-value pairs to the database. Experiment with creating new objects in the tree to form the structure below. Click on some of the objects and make note of how the URL changes. 

![Firebase Data Browser](screenshot1.png)

When the users of your application are on a particular screen, your Activity will connect to Firebase on a particular node. In our example, we will connect to "messages". When anyone pushes a new object to "messages", the object will be pushed to all other listeners on that node, and the ListView will automatically update without needing to refresh the screen. Enough talk, let's write some code.

# Connecting an Android Application to Firebase

To get started, let's create a new project in Android Studio. I picked Drawer navigation, but you can start with an empty project or ListView if you prefer. Our application will look like the screenshot below. All of the messages in our Firebase datastore will be displayed in a ListView. Anyone connected to the same datastore will be able to click the Floating Action Button and push a chat message to the datastore. All devices connected to the Firebase datastore will have a ListView that is synchronized to the data in the messages node.

![What Our Application Will Look Like](screenshot2.png)

## Add Dependencies

Once you have created an Android project, you need to add the Firebase dependencies to your gradle build file. Once your grade file syncs, this will allow you to import the necessary Firebase packages into your Java code.

```
dependencies {
    compile fileTree(dir: 'libs', include: ['*.jar'])
    testCompile 'junit:junit:4.12'
    compile 'com.firebase:firebase-client-android:2.4.0'
    compile 'com.firebaseui:firebase-ui:0.2.2'
    compile 'com.android.support:appcompat-v7:23.1.1'
    compile 'com.android.support:design:23.1.1'
}
```

Also, add the following lines inside of the android {} configuration:

```
packagingOptions {
    exclude 'META-INF/LICENSE'
    exclude 'META-INF/NOTICE'
}
```

If you don't do this, you will get the following error:

```
Error:Execution failed for task :app:transformResourcesWithMergeJavaResForDebug.
> com.android.build.api.transform.TransformException: com.android.builder.packaging.DuplicateFileException: Duplicate files copied in APK META-INF/NOTICE
```

## Permissions

Connecting to Firebase requires an Internet connection, so be sure to add the INTERNET permission to your AndroidManifest.xml file. 

```
<uses-permission android:name="android.permission.INTERNET" />
```

## The Code

Our application will consist of the following files:

* __Constants.java__ - Has a constant for the FIREBASE_URL, but could contain other constants in a more complex app.
* __Message.java__ - The message model, a plain Java object. Your Firebase JSON objects will be mapped to this class.
* __message.xml__ - The layout of a single chat message
* __MessageListAdapter.java__ - Binds Firebase data to the Message model and displays each message row in the ListView
* __MainActivity.java__ - Initializes Firebase, connects to the database, instantiates the ListAdapter, and contains the logic for our Floating Action Button. When the Floating Action Button is clicked, we will display an AlertDialog that allows the user to type in a message. When the user clicks OK, we will push the new message to the database.
* __activity_main.xml__ - The overall layout of our app's screen
* __content_main.xml__ - Included by activity_main.xml, contains the ListView xml

The directory structure will look like this:

![What Our Application Will Look Like](screenshot3.png)

## Constants

Although you can hardcode the Firebase URL directly into your Activity class, I like to create a separate class for my constants. You may have many Firebase URLs that reference different nodes in your database and you might have many Activity classes that reference these URLs, so it is nice to be able to define them in one place. In our demo, everyone should use the same FIREBASE_URL below so that we all can see the same messages:

### util/Constants.java

```
package com.androidclass.uwchat.util;

public class Constants {
    public static final String FIREBASE_URL = "https://uw-android.firebaseio.com";
}
```

## Message Model

The message model is a plain Java class. We have declared private Strings and getters that correspond to each key (sender and content) in our Firebase message structure.

### models/Message.java

```
package com.androidclass.uwchat.models;

public class Message {
    private String sender;  // who sent the message
    private String content; // content of the message

    public Message() {
        // this constructor is required even though it doesn't do anything
    }

    public Message(String sender, String content) {
        this.sender = sender;
        this.content = content;
    }

    public String getSender() { return sender; }
    public String getContent() { return content; }
}
```

## Message List Adapter and Message Layout

We create a List Adapter that extends the FirebaseListAdapter class. In the populateView() method, we find the text views in our message.xml layout by ID and set their text equal to the corresponding data in our message model. 

### Message List Adapter - MessageListAdapter.java

```
package com.androidclass.uwchat;

import android.app.Activity;
import android.view.View;
import android.widget.TextView;

import com.androidclass.uwchat.models.Message;
import com.firebase.client.Query;
import com.firebase.ui.FirebaseListAdapter;

public class MessageListAdapter extends FirebaseListAdapter<Message> {

    public MessageListAdapter(Activity activity, Class<Message> modelClass, int modelLayout, Query messageListReference) {
        super(activity, modelClass, modelLayout, messageListReference);
        this.mActivity = activity;
    }

    @Override
    protected void populateView(View view, final Message message, int position) {
        TextView messageSenderView = (TextView) view.findViewById(R.id.message_sender);
        TextView messageContentView = (TextView) view.findViewById(R.id.message_content);

        messageSenderView.setText(message.getSender());
        messageContentView.setText(message.getContent());
    }
}
```

### Message Layout - message.xml

```
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:layout_margin="2dp"
    android:background="@android:color/white"
    android:descendantFocusability="blocksDescendants"
    android:foreground="?selectableItemBackground"
    android:gravity="center_vertical"
    android:orientation="horizontal">

    <TextView
        android:id="@+id/message_sender"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:textStyle="bold"
        android:padding="10dp" />

    <TextView
        android:id="@+id/message_content"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:padding="10dp" />

</LinearLayout>
```

## Main Activity and Layout

The MainActivity class ties everything together. In the onCreate() method, we initialize the Firebase library and create a new Firebase reference to Constants.FIREBASE_URL. We use the .child("messages") to specify we want to listen specifically on the "messages" node in our database. We then set up the message ListView and Adapter and pass our adapter the Message model class, the Message layout, and our Firebase reference. It then handles the magic of syncing this data to our ListView. 

Once we have written the code to display our messages in a list, we add the logic for our Floating Action Button. When the button is clicked, we display an AlertDialog where the user can type their message. When the dialog's OK button is clicked, we push a new HashMap to our Firebase reference, and the message is sent to all connected devices instantly. 

![Message Dialog](screenshot4.png)

### MainActivity

```
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
```

## activity_main.xml

```
<?xml version="1.0" encoding="utf-8"?>
<android.support.design.widget.CoordinatorLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    xmlns:tools="http://schemas.android.com/tools"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:fitsSystemWindows="true"
    tools:context="com.androidclass.uwchat.MainActivity">

    <android.support.design.widget.AppBarLayout
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:theme="@style/AppTheme.AppBarOverlay">

        <android.support.v7.widget.Toolbar
            android:id="@+id/toolbar"
            android:layout_width="match_parent"
            android:layout_height="?attr/actionBarSize"
            android:background="?attr/colorPrimary"
            app:popupTheme="@style/AppTheme.PopupOverlay" />

    </android.support.design.widget.AppBarLayout>

    <include layout="@layout/content_main" />

    <android.support.design.widget.FloatingActionButton
        android:id="@+id/fab"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:layout_gravity="bottom|end"
        android:layout_margin="@dimen/fab_margin"
        android:src="@android:drawable/ic_dialog_email" />

</android.support.design.widget.CoordinatorLayout>
```

## content_main.xml

```
<?xml version="1.0" encoding="utf-8"?>
<RelativeLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    xmlns:tools="http://schemas.android.com/tools"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    app:layout_behavior="@string/appbar_scrolling_view_behavior"
    tools:context="com.androidclass.uwchat.MainActivity"
    tools:showIn="@layout/activity_main">

    <ListView
        android:id="@+id/message_list"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        android:scrollbars="none" />

</RelativeLayout>
```

# Conclusion

As you can see, Firebase gives us a big reward without much effort by taking care of the backend server and datastore. We didn't need to write our own web API, run a server, write any AsyncTasks to hit our web API, or parse any XML or JSON. And since it is run by Google, we can trust that it is a reliable, scalable solution. Unless they kill the product all together :).

## Going Further: Security

That was easy, almost too easy. Surely there is a catch. Well notice that everyone in the class could connect to the database url without any authorization? They can also write back to it. By default, the door is wide open until you add security rules to lock down the database. This is beyond the scope of this lesson, but more information can be found in the Firebase Security Guide linked below.

# Links to more resources

* [Firebase Android Guide](https://www.firebase.com/docs/android/guide/)
* [Firebase Security Guide](https://www.firebase.com/docs/security/guide/)
* [Using Firebase With Google App Engine](https://cloud.google.com/solutions/mobile/firebase-app-engine-android-studio)

