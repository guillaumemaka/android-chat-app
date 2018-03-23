package com.espacepiins.messenger.model;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Relation;

import com.espacepiins.messenger.db.entity.EmailEntity;
import com.espacepiins.messenger.db.entity.PhoneEntity;

import java.util.List;

/**
 * Created by guillaume on 18-03-20.
 */

public class SearchContactResult  {
    @ColumnInfo(name = "lookup_key")
    private String lookupKey;
    @ColumnInfo(name = "display_name")
    private String displayName;
    @ColumnInfo(name = "firebase_uid")
    private String firebaseUID;
    @ColumnInfo(name = "photo_thumbnail_uri")
    private String photoThumbnailUri;
    @Relation(parentColumn = "lookup_key", entityColumn = "contact_lookup_key")
    private List<EmailEntity> emailAddresses;
    @Relation(parentColumn = "lookup_key", entityColumn = "contact_lookup_key")
    private List<PhoneEntity> phoneNumbers;

    public SearchContactResult() {
    }

    public String getLookupKey() {
        return lookupKey;
    }

    public void setLookupKey(String lookupKey) {
        this.lookupKey = lookupKey;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getFirebaseUID() {
        return firebaseUID;
    }

    public void setFirebaseUID(String firebaseUID) {
        this.firebaseUID = firebaseUID;
    }

    public String getPhotoThumbnailUri() {
        return photoThumbnailUri;
    }

    public void setPhotoThumbnailUri(String photoThumbnailUri) {
        this.photoThumbnailUri = photoThumbnailUri;
    }

    public List<EmailEntity> getEmailAddresses() {
        return emailAddresses;
    }

    public void setEmailAddresses(List<EmailEntity> emailAddresses) {
        this.emailAddresses = emailAddresses;
    }

    public List<PhoneEntity> getPhoneNumbers() {
        return phoneNumbers;
    }

    public void setPhoneNumbers(List<PhoneEntity> phoneNumbers) {
        this.phoneNumbers = phoneNumbers;
    }
}
