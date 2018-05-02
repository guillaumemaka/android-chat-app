package com.espacepiins.messenger.model;

import android.support.annotation.NonNull;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Created by guillaume on 18-02-22.
 */

@IgnoreExtraProperties
public class Room implements Comparable<Room> {
    private String roomUID;
    private String from;
    private String fromDisplayName;
    private String to;
    private String toDisplayName;
    private String lastMessageUID;
    private String lastMessage;
    private boolean read;
    private Long lastMessageTimestamp;
    private Long createdAt;

    public Room(){
        this.createdAt = new Date().getTime();
        this.read = false;
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

    public Long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Long createdAt) {
        this.createdAt = createdAt;
    }

    public boolean isRead() {
        return read;
    }

    public void setRead(boolean read) {
        this.read = read;
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
        values.put("createdAt", this.getCreatedAt());
        values.put("read", this.isRead());
        return values;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Room)) return false;
        Room room = (Room) o;
        return read == room.read &&
                Objects.equals(roomUID, room.roomUID) &&
                Objects.equals(from, room.from) &&
                Objects.equals(fromDisplayName, room.fromDisplayName) &&
                Objects.equals(to, room.to) &&
                Objects.equals(toDisplayName, room.toDisplayName) &&
                Objects.equals(lastMessageUID, room.lastMessageUID) &&
                Objects.equals(lastMessage, room.lastMessage) &&
                Objects.equals(lastMessageTimestamp, room.lastMessageTimestamp) &&
                Objects.equals(createdAt, room.createdAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(roomUID, from, fromDisplayName, to, toDisplayName, lastMessageUID, lastMessage, read, lastMessageTimestamp, createdAt);
    }

    @Override
    public int compareTo(@NonNull Room o) {
        if (o.lastMessageTimestamp != null) {
            if (lastMessageTimestamp > o.lastMessageTimestamp)
                return 1;

            if (lastMessageTimestamp < o.lastMessageTimestamp)
                return -1;
        }

        if (createdAt > o.createdAt)
            return 1;

        if (createdAt < o.createdAt)
            return -1;

        return 0;
    }
}
