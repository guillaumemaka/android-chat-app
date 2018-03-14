package com.espacepiins.messenger.db.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.espacepiins.messenger.db.entity.ContactEntity;

import java.util.List;

/**
 * Created by guillaume on 18-03-06.
 */

@Dao
public interface ContactDao {
    @Query("SELECT * from contacts")
    List<ContactEntity> getAll();

    @Query("SELECT * FROM contacts WHERE email_address LIKE '%' || :term || '%' " +
            "OR phone_number LIKE '%' || :term || '%' " +
            "OR display_name LIKE '%' || :term || '%'")
    List<ContactEntity> search(String term);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(ContactEntity... contacts);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<ContactEntity> contacts);

    @Update
    void updateAll(List<ContactEntity> contacts);

    @Update
    void update(ContactEntity... contacts);

    @Query("DELETE FROM contacts")
    void deleteAll();
}
