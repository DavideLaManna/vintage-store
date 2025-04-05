package com.example.vintagestore.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.ColumnInfo;
import androidx.room.Ignore;

import java.util.Date;

@Entity(tableName = "messages")
public class Message {
    
    @PrimaryKey(autoGenerate = true)
    private int id;
    
    @ColumnInfo(name = "sender_id")
    private int senderId;
    
    @ColumnInfo(name = "receiver_id")
    private int receiverId;
    
    @ColumnInfo(name = "content")
    private String content;
    
    @ColumnInfo(name = "timestamp")
    private Date timestamp;
    
    @ColumnInfo(name = "is_read")
    private boolean isRead;
    
    @ColumnInfo(name = "item_id")
    private int itemId;  // Optional, if the message is about a specific item

    public Message() {
        this.timestamp = new Date();
        this.isRead = false;
    }

    @Ignore
    public Message(int senderId, int receiverId, String content) {
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.content = content;
        this.timestamp = new Date();
        this.isRead = false;
    }

    @Ignore
    public Message(int senderId, int receiverId, String content, int itemId) {
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.content = content;
        this.itemId = itemId;
        this.timestamp = new Date();
        this.isRead = false;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getSenderId() {
        return senderId;
    }

    public void setSenderId(int senderId) {
        this.senderId = senderId;
    }

    public int getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(int receiverId) {
        this.receiverId = receiverId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public boolean isRead() {
        return isRead;
    }

    public void setRead(boolean read) {
        isRead = read;
    }

    public int getItemId() {
        return itemId;
    }

    public void setItemId(int itemId) {
        this.itemId = itemId;
    }
}