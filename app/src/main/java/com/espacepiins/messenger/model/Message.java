package com.espacepiins.messenger.model;

import android.support.annotation.NonNull;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by guillaume on 18-03-24.
 */
@IgnoreExtraProperties
public class Message implements Comparable<Message> {
    private String roomId;
    private String sender;
    private String to;
    private String content;
    private Long timestamp;

    public Message() {
        this.timestamp = new Date().getTime();
    }

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    @Exclude
    public Map<String, Object> toMap(){
        final Map<String, Object> values = new HashMap<>();
        values.put("roomId", getRoomId());
        values.put("sender", getSender());
        values.put("to", getTo());
        values.put("content", getContent());
        values.put("timestamp", getTimestamp());
        return values;
    }

    @Override
    public String toString() {
        return "Message{" +
                "roomId='" + roomId + '\'' +
                ", sender='" + sender + '\'' +
                ", to='" + to + '\'' +
                ", content='" + content + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }

    @Override
    public int compareTo(@NonNull Message o) {
        return timestamp.compareTo(o.timestamp);
    }
}
