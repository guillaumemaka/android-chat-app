package com.espacepiins.messenger.model;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Relation;

import com.espacepiins.messenger.db.entity.EmailEntity;

import java.util.ArrayList;
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
//    @Relation(parentColumn = "lookup_key", entityColumn = "contact_lookup_key")
//    private List<PhoneEntity> phoneNumbers;

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

//    public List<PhoneEntity> getPhoneNumbers() {
//        return phoneNumbers;
//    }
//
//    public void setPhoneNumbers(List<PhoneEntity> phoneNumbers) {
//        this.phoneNumbers = phoneNumbers;
//    }

    public List<String> getEmails(){
        final List<String> emails = new ArrayList<>();
        for(EmailEntity emailEntity : getEmailAddresses()){
            emails.add(emailEntity.getEmailAddress());
        }
        return emails;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SearchContactResult)) return false;

        SearchContactResult that = (SearchContactResult) o;

        if (lookupKey != null ? !lookupKey.equals(that.lookupKey) : that.lookupKey != null)
            return false;
        if (displayName != null ? !displayName.equals(that.displayName) : that.displayName != null)
            return false;
        if (firebaseUID != null ? !firebaseUID.equals(that.firebaseUID) : that.firebaseUID != null)
            return false;
        return photoThumbnailUri != null ? photoThumbnailUri.equals(that.photoThumbnailUri) : that.photoThumbnailUri == null;
    }

    @Override
    public int hashCode() {
        int result = lookupKey != null ? lookupKey.hashCode() : 0;
        result = 31 * result + (displayName != null ? displayName.hashCode() : 0);
        result = 31 * result + (firebaseUID != null ? firebaseUID.hashCode() : 0);
        result = 31 * result + (photoThumbnailUri != null ? photoThumbnailUri.hashCode() : 0);
        return result;
    }
}
