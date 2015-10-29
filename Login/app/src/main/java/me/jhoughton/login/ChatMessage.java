package me.jhoughton.login;

import org.jivesoftware.smack.chat.Chat;

/**
 * Created by james on 10/23/2015.
 */
public class ChatMessage {
    private long id;
    private boolean isMe;
    private String message;
    private String name;
    private String dateTime;

    public ChatMessage(String name, String message, String nick) {
        this.name = name;
        this.message = message;
        this.isMe = name.equals(nick);
    }
    public long getId() {
        return id;
    }
    public void setId(long id) {
        this.id = id;
    }
    public boolean getIsme() {
        return isMe;
    }
    public void setMe(boolean isMe) {
        this.isMe = isMe;
    }
    public String getMessage() {
        return message;
    }
    public void setMessage(String message) {
        this.message = message;
    }
    public void setName(String name) {this.name = name;}
    public String getName() {return this.name;}
    public String getDate() {
        return dateTime;
    }

    public void setDate(String dateTime) {
        this.dateTime = dateTime;
    }
}