package com.espacepiins.messenger.model;

import android.os.Parcelable;
import android.support.annotation.NonNull;

/**
 * Created by guillaume on 18-03-07.
 */

public interface Contact extends Parcelable {
    @NonNull
    String getId();

    void setId(@NonNull String id);

    String getLookupKey();

    void setLookupKey(String lookupKey);

    String getDisplayName();

    void setDisplayName(String displayName);

    String getFirebaseUID();

    void setFirebaseUID(String firbaseUID);

    String getPhotoThumbnailUri();

    void setPhotoThumbnailUri(String uri);
}
