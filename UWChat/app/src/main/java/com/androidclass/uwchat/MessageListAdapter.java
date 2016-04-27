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