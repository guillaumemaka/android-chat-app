package com.espacepiins.messenger.db.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.TypeConverters;
import android.arch.persistence.room.Update;

import com.espacepiins.messenger.db.converter.DateConverter;
import com.espacepiins.messenger.db.entity.RoomEntity;

import java.util.List;

/**
 * Created by guillaume on 18-03-20.
 */

@Dao
@TypeConverters(DateConverter.class)
public interface RoomDao {
    @Query("SELECT * FROM rooms WHERE deleted = 0 AND archived = 0")
    LiveData<List<RoomEntity>> getRooms();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(RoomEntity... rooms);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(List<RoomEntity> rooms);

    @Update
    void update(RoomEntity room);

    @Query("UPDATE rooms SET archived = 1 WHERE room_uid = :roomUID")
    void archived(String roomUID);

    @Query("UPDATE rooms SET deleted = 1 WHERE room_uid = :roomUID")
    void delete(String roomUID);

    @Query("UPDATE rooms SET archived = 0 WHERE room_uid = :roomUID")
    void unArchived(String roomUID);
}
