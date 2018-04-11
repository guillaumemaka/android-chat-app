package com.espacepiins.messenger.model;

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
    private String roomUID;
    private String from;
    private String fromDisplayName;
    private String to;
    private String toDisplayName;
    private String lastMessageUID;
    private String lastMessage;
    private Long lastMessageTimestamp;
    private Long sentAt;

    public Room(){
        this.sentAt = new Date().getTime();
    }

    public String getRoomUID() {
        return roomUID;
    }

    public void setRoomUID(String roomUID) {
        this.roomUID = roomUID;
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

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getToDisplayName() {
        return toDisplayName;
    }

    public void setToDisplayName(String toDisplayName) {
        this.toDisplayName = toDisplayName;
    }

    public String getLastMessageUID() {
        return lastMessageUID;
    }

    public void setLastMessageUID(String lastMessageUID) {
        this.lastMessageUID = lastMessageUID;
    }

    public Long getLastMessageTimestamp() {
        return lastMessageTimestamp;
    }

    public void setLastMessageTimestamp(Long lastMessageTimestamp) {
        this.lastMessageTimestamp = lastMessageTimestamp;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    public Long getSentAt() {
        return sentAt;
    }

    public void setSentAt(Long sentAt) {
        this.sentAt = sentAt;
    }

    @Exclude
    public Map<String, Object> toMap(){
        HashMap<String, Object> values = new HashMap<>();
        values.put("roomUID", this.getRoomUID());
        values.put("from", this.getFrom());
        values.put("fromDisplayName", this.getFromDisplayName());
        values.put("lastMessageUID", this.getLastMessageUID());
        values.put("lastMessageTimestamp", this.getLastMessageTimestamp());
        values.put("to", this.getTo());
        values.put("toDisplayName", this.getToDisplayName());
        values.put("lastMessage", this.getLastMessage());
        return values;
    }
}
