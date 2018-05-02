package com.espacepiins.messenger.db.entity;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import com.espacepiins.messenger.model.Contact;

/**
 * Created by guillaume on 18-03-06.
 */

@Entity(tableName = "contacts")
public class ContactEntity implements Contact, Comparable<ContactEntity> {
    @PrimaryKey
    @NonNull
    private String id;
    @ColumnInfo(name = "lookup_key", index = true)
    private String lookupKey;
    @ColumnInfo(name = "display_name", index = true)
    private String displayName;
    @ColumnInfo(name = "firebase_uid")
    private String firebaseUID;
    @ColumnInfo(name = "photo_thumbnail_uri")
    private String photoThumbnailUri;

    public ContactEntity() {}

    @Ignore
    public ContactEntity(Parcel parcel){
        this.id = parcel.readString();
        this.lookupKey = parcel.readString();
        this.displayName = parcel.readString();
        this.firebaseUID = parcel.readString();
        this.photoThumbnailUri = parcel.readString();
    }

    @Ignore
    public ContactEntity(@NonNull String id, String lookupKey, String displayName, String firebaseUID, String emailAddress, String phoneNumber, String photoThumbnailUri) {
        this.id = id;
        this.lookupKey = lookupKey;
        this.displayName = displayName;
        this.firebaseUID = firebaseUID;
        this.photoThumbnailUri = photoThumbnailUri;
    }

    @NonNull
    @Override
    public String getId() {
        return id;
    }

    @Override
    public void setId(@NonNull String id) {
        this.id = id;
    }

    @Override
    public String getLookupKey() {
        return lookupKey;
    }

    @Override
    public void setLookupKey(String lookupKey) {
        this.lookupKey = lookupKey;
    }

    @Override
    public String getDisplayName() {
        return displayName;
    }

    @Override
    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    @Override
    public String getFirebaseUID() {
        return firebaseUID;
    }

    @Override
    public void setFirebaseUID(String firebaseUID) {
        this.firebaseUID = firebaseUID;
    }

    @Override
    public String getPhotoThumbnailUri() {
        return photoThumbnailUri;
    }

    @Override
    public void setPhotoThumbnailUri(String uri) {
        this.photoThumbnailUri = uri;
    }

    @Override
    public int describeContents() {
        return hashCode();
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(id);
        parcel.writeString(lookupKey);
        parcel.writeString(displayName);
        parcel.writeString(firebaseUID);
        parcel.writeString(photoThumbnailUri);
    }

    public static final Parcelable.Creator<ContactEntity> CREATOR = new Creator<ContactEntity>() {
        @Override
        public ContactEntity createFromParcel(Parcel parcel) {
            return new ContactEntity(parcel);
        }

        @Override
        public ContactEntity[] newArray(int i) {
            return new ContactEntity[0];
        }
    };

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ContactEntity)) return false;

        ContactEntity that = (ContactEntity) o;

        return lookupKey.equals(that.getLookupKey());
    }

    @Override
    public int hashCode() {
        return lookupKey.hashCode();
    }

    @Override
    public int compareTo(@NonNull ContactEntity contactEntity) {
        return displayName.compareTo(contactEntity.getDisplayName());
    }
}
