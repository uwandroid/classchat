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