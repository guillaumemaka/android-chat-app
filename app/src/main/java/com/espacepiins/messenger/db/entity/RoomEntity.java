package com.espacepiins.messenger.db.entity;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.arch.persistence.room.TypeConverters;
import android.support.annotation.NonNull;

import com.espacepiins.messenger.db.converter.DateConverter;
import com.espacepiins.messenger.model.Room;

import java.util.Date;

/**
 * Created by guillaume on 18-03-20.
 */

@Entity(tableName = "rooms")
public class RoomEntity implements Comparable<RoomEntity> {
    @PrimaryKey
    @ColumnInfo(name = "room_uid")
    @NonNull
    private String roomUID;
    @ColumnInfo(name = "display_name")
    private String displayName;
    @ColumnInfo(name = "last_message")
    private String lastMessage;
    @ColumnInfo(name = "last_message_timestamp")
    @TypeConverters(DateConverter.class)
    private Date lastMessageDate;
    private boolean deleted;
    private boolean archived;

    public RoomEntity() {
    }

    public String getRoomUID() {
        return roomUID;
    }

    public void setRoomUID(String roomUID) {
        this.roomUID = roomUID;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    public Date getLastMessageDate() {
        return lastMessageDate;
    }

    public void setLastMessageDate(Date lastMessageDate) {
        this.lastMessageDate = lastMessageDate;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    public boolean isArchived() {
        return archived;
    }

    public void setArchived(boolean archived) {
        this.archived = archived;
    }

    public static RoomEntity fromFirebaseObject(Room room){
        final RoomEntity roomEntity = new RoomEntity();
        roomEntity.setRoomUID(room.getRoomUID());
        roomEntity.setDisplayName(room.getToDisplayName());
        roomEntity.setLastMessage(room.getLastMessage());
        roomEntity.setLastMessageDate(new Date(room.getLastMessageTimestamp()));
        return roomEntity;
    }

    @Override
    public int compareTo(@NonNull RoomEntity o) {
        int changed = this.lastMessageDate.compareTo(o.getLastMessageDate());
        return changed;
    }
}
