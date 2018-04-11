package com.espacepiins.messenger.db.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Transaction;
import android.arch.persistence.room.Update;

import com.espacepiins.messenger.db.entity.ContactEntity;
import com.espacepiins.messenger.db.entity.EmailEntity;
import com.espacepiins.messenger.db.entity.PhoneEntity;
import com.espacepiins.messenger.model.SearchContactResult;

import java.util.List;

/**
 * Created by guillaume on 18-03-06.
 */

@Dao
public interface ContactDao {
    @Query("SELECT * FROM contacts WHERE id = :id")
    ContactEntity get(String id);

    @Query("SELECT * FROM contacts WHERE lookup_key = :lookupKey")
    ContactEntity getByLookupKey(String lookupKey);

    @Query("SELECT * from contacts")
    List<ContactEntity> getAll();

    @Query("SELECT c.lookup_key, display_name, firebase_uid, photo_thumbnail_uri " +
            "FROM contacts c " +
            "INNER JOIN phone_numbers p ON p.contact_lookup_key = c.lookup_key " +
            "INNER JOIN email_addresses e ON e.contact_lookup_key = c.lookup_key " +
            "WHERE e.email_address LIKE '%' || :term || '%' " +
            "OR p.phone_number LIKE '%' || :term || '%' " +
            "OR c.display_name LIKE '%' || :term || '%' " +
            "GROUP BY c.lookup_key " +
            "ORDER BY display_name")
    @Transaction
    List<SearchContactResult> search(String term);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertContacts(ContactEntity... contacts);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertContacts(List<ContactEntity> contacts);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertPhones(PhoneEntity... contacts);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertPhones(List<PhoneEntity> contacts);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertEmails(EmailEntity... contacts);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertEmails(List<EmailEntity> contacts);


    @Update
    void updateAll(List<ContactEntity> contacts);

    @Update
    void update(ContactEntity... contacts);

    @Query("DELETE FROM contacts")
    void deleteAllContacts();

    @Query("DELETE FROM email_addresses")
    void deleteAllEmails();

    @Query("DELETE FROM phone_numbers")
    void deleteAllPhones();
}
