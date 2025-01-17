package com.csaralameda.agrotrueque.ui.chat;


import java.util.Date;

public class Message {
    private String messageId;
    private String senderId;
    private String receiverId;
    private String content;
    private Date timestamp;
    private boolean read;

    public Message() {}

    public Message(String senderId, String receiverId, String content) {
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.content = content;
        this.timestamp = new Date();
        this.read = false;
    }


    public String getMessageId() { return messageId; }
    public void setMessageId(String messageId) { this.messageId = messageId; }
    public String getSenderId() { return senderId; }
    public void setSenderId(String senderId) { this.senderId = senderId; }
    public String getReceiverId() { return receiverId; }
    public void setReceiverId(String receiverId) { this.receiverId = receiverId; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public Date getTimestamp() { return timestamp; }
    public void setTimestamp(Date timestamp) { this.timestamp = timestamp; }
    public boolean isRead() { return read; }
    public void setRead(boolean read) { this.read = read; }
}
