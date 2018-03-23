package com.espacepiins.messenger.db.entity;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;
import android.util.Patterns;

/**
 * Created by guillaume on 18-03-18.
 */

@Entity(tableName = "email_addresses")
public class EmailEntity {
    @PrimaryKey
    @NonNull
    private String id;

    @ColumnInfo(name = "email_address")
    private String emailAddress;

    @ColumnInfo(name = "email_type")
    private String emailType;

    @ColumnInfo(name = "contact_lookup_key")
    private String contactLookupKey;

    public EmailEntity() {
    }

    public void setId(@NonNull String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(@NonNull String emailAddress) {
        if(emailAddress == null && !Patterns.EMAIL_ADDRESS.matcher(emailAddress).matches())
            return;
        this.emailAddress = emailAddress;
    }

    public String getEmailType() {
        return emailType;
    }

    public void setEmailType(@NonNull String emailType) {
        this.emailType = emailType;
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
        if (!(o instanceof EmailEntity)) return false;

        EmailEntity that = (EmailEntity) o;

        if (!emailAddress.equals(that.emailAddress)) return false;
        return emailType.equals(that.emailType);
    }

    @Override
    public int hashCode() {
        int result = emailAddress.hashCode();
        result = 31 * result + emailType.hashCode();
        return result;
    }
}
