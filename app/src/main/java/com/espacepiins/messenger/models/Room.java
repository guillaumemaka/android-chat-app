package com.espacepiins.messenger.models;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by guillaume on 18-02-22.
 */

@IgnoreExtraProperties
public class Room {
    private String from;
    private String fromDisplayName;
    private Map<String, String> members;
    private String lastMessage;
    private Date sentAt;

    public Room(){
        this.members = new HashMap<String, String>();
        this.sentAt = new Date();
    }

    public Room(String from, String fromDisplayName, Map<String,String> members, String lastMessage, Date sentAt) {
        this();
        this.from = from;
        this.fromDisplayName = fromDisplayName;
        this.members = members;
        this.lastMessage = lastMessage;
        this.sentAt = sentAt;
    }

    public String getFrom() {
        return from;
    }

    public String getFromDisplayName() {
        return fromDisplayName;
    }

    public void setFromDisplayName(String fromDisplayName) {
        this.fromDisplayName = fromDisplayName;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public Map<String, String> getMembers() {
        return members;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    public Date getSentAt() {
        return sentAt;
    }

    public void setSentAt(Date sentAt) {
        this.sentAt = sentAt;
    }



    public void addMember(String username, String displayName){
        if(!members.containsKey(username)){
            this.members.put(username,  displayName);
        }
    }

    @Exclude
    public Map<String, Object> toMap(){
        HashMap<String, Object> values = new HashMap<>();
        values.put("from", this.getFrom());
        values.put("fromDisplayName", this.getFromDisplayName());
        values.put("members", this.getMembers());
        values.put("lastMessage", this.getLastMessage());
        values.put("sentAt", this.getSentAt().getTime());
        return values;
    }
}
