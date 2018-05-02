package com.espacepiins.messenger.db.entity;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;
import android.util.Patterns;

/**
 * Created by guillaume on 18-03-18.
 */
@Entity(tableName = "phone_numbers")
public class PhoneEntity {
    @PrimaryKey
    @NonNull
    private String id;

    @ColumnInfo(name = "phone_type")
    private String phoneType;

    @ColumnInfo(name = "phone_number")
    private String phoneNumber;

    @ColumnInfo(name = "contact_lookup_key")
    private String contactLookupKey;

    public PhoneEntity() {}

    public String getId() {
        return id;
    }

    public void setId(@NonNull String id) {
        this.id = id;
    }

    public String getPhoneType() {
        return phoneType;
    }

    public void setPhoneType(@NonNull String phoneType) {
        this.phoneType = phoneType;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(@NonNull String phoneNumber) {
        if(phoneNumber == null && !Patterns.PHONE.matcher(phoneNumber).matches())
            return;
        this.phoneNumber = phoneNumber;
    }

    public String getContactLookupKey() {
        return contactLookupKey;
    }

    public void setContactLookupKey(String contactLookupKey) {
        this.contactLookupKey = contactLookupKey;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PhoneEntity)) return false;

        PhoneEntity that = (PhoneEntity) o;

        if (!phoneNumber.equals(that.phoneNumber)) return false;
        return contactLookupKey.equals(that.contactLookupKey);
    }

    @Override
    public int hashCode() {
        int result = phoneNumber.hashCode();
        result = 31 * result + contactLookupKey.hashCode();
        return result;
    }
}
