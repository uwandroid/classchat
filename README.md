# What is Firebase?

Firebase is a "backend as a service" owned by Google. It provides a real-time database and API that allows developers to store and sync data across multiple clients.

# What problem does it solve?

Let's say you want to build an application that provides live updates. For instance, you may want to create a Twitter-like activity stream or build an app with live sports scores. You want to view the application on the web and mobile devices, including Android and iOS. When the application's data is updated, you want all of the devices that are displaying that data to see the change reflected on their screen immediately.

With a standard web application or Android app, you would have to do one of two things for end users to see the updated data:

1. The user would have to refresh the webpage or reload the ListView Activity on their Android app
2. You would set a timer or interval to poll a Web API to check if there is new data to fetch, then re-render the screen.

# How does Firebase handle real-time data?

Firebase stores data as JSON objects. When using Firebase, your data is a big collection of nested key-value pairs. It is probably best to learn how this works by actually creating a Firebase account and using its built-in database browser. So let's create an account by visiting the <a href="https://firebase.com">Firebase website</a> and signing in with Google. Once you are signed in you can create a new app where your data will live as shown in the screenshot below:

![Firebase App Creation](https://docs.google.com/uc?export=download&id=0BzfHZKVI-LraVEJSUWViUjB4ZXM)

Once you have created your application database, you will be able to view your data via a web interface on the Firebase website. You can use this interface to add new key-value pairs to the database. Experiment with creating new objects in the tree to form the structure below. Click on some of the objects and make note of how the URL changes. 

![Firebase Data Browser](screenshot1.png)

All of your users would connect to the key value store on a particular node. It provides a set of "listeners" for your list views so that you can listen for updates on that node. When anyone pushes new data to that node, this information is pushed to all other listeners of that node, and the ListView automatically updates without needing to refresh the page. Let's try this out.


# Creating a Firebase Account

[link to firebase web page]

To create a Firebase database, you first have to create an account. Once you have an account, create a new database. I am calling this one uw-android. Everyone in class make note of the URL for the database so we can all hit the same one. Put some data in it. Browse the database. Explain key values. Typically denormalized, need to do some of your own data management vs. a relational database in order to keep things fast.

# Create a new Android Project

I picked the Drawer navigation, but you can start with an empty project or ListView.

Or clone this skeleton project.

[clone button] <-- clones skeleton project, students adds code from gists throughout the lesson. skeleton project would need to be public? or private and students have access

# Add the Firebase Client to your Project

Add the dependency to your gradle build file. Once your grade file syncs, this will allow you to import the necessary Firebase packages into your Java code.

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

Also, add the following to avoid a gotcha

```
packagingOptions {
    exclude 'META-INF/LICENSE'
    exclude 'META-INF/NOTICE'
}
```

If you don't do this

```
Error:Execution failed for task :app:transformResourcesWithMergeJavaResForDebug.
> com.android.build.api.transform.TransformException: com.android.builder.packaging.DuplicateFileException: Duplicate files copied in APK META-INF/NOTICE
```

Add a reference to the node. Demo to everyone in the class who is connected. We should be able to push messages to each other.

Obviously you need permission to access the internet by adding this to your andorid manifest file:

<uses-permission android:name="android.permission.INTERNET" />

    Firebase.setAndroidContext(this);

Reading data

myFirebaseRef.child("message").addValueEventListener(new ValueEventListener() {
  @Override
  public void onDataChange(DataSnapshot snapshot) {
    System.out.println(snapshot.getValue());  //prints "Do you have data? You'll love Firebase."
  }
  @Override public void onCancelled(FirebaseError error) { }
});

http://g.recordit.co/AVc2y2TrjI.gif

Writing data

send a message using the FAB button

    myFirebaseRef.child("message").setValue("Do you have data? You'll love Firebase.");

As you can see, Firebase gives us a big reward for not much effort by taking care of the backend. We didn't need to write our own web backend, run a server, write any AsyncTasks to hit our web backend, or parse any XML or JSON. And since it is run by Google, we can trust that it is a reliable, scalable solution. Unless they kill the product all together :).

# Locking it down.

That was easy, almost too easy. Surely there is a catch. Well notice that everyone in the class could connect to the database url without any authorization? They can also write back to it. By default, the door is wide open.

What to do? Firebase provides a set of rules for locking down these URLs. That is left as an exercise for the student :). (unless I have time to write this out today).

# Web Access

React + ReactFire sample code + demo.

# Links to more resources

* link 1
* link 2
* link 3

# Exercises

[ include one or more assignments ]
