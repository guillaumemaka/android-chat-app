package com.espacepiins.messenger.model;

import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.espacepiins.messsenger.BR;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.HashMap;
import java.util.Map;

@IgnoreExtraProperties
public class Profile extends BaseObservable {
    private String userUID;
    private String displayName;
    private String emailAddress;
    private String username;
    private String avatarUrl;

    public Profile() {
    }

    public String getUserUID() {
        return userUID;
    }

    public void setUserUID(String userUID) {
        this.userUID = userUID;
    }

    @Bindable
    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
        Log.i("Profile", "Profile displayName changed to: " + this.displayName);
        notifyPropertyChanged(BR.displayName);
    }

    @Bindable
    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
        notifyPropertyChanged(BR.emailAddress);
    }

    @Bindable
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
        notifyPropertyChanged(BR.username);
    }

    @Bindable
    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
        notifyPropertyChanged(BR.avatarUrl);
    }

    @Exclude
    public Map<String, Object> toMap() {
        final Map<String, Object> values = new HashMap<>();
        values.put("userUID", this.userUID);
        values.put("displayName", this.displayName);
        values.put("emailAddress", this.emailAddress);
        values.put("username", this.username);
        values.put("avatarUrl", this.avatarUrl);
        return values;
    }

    @Override
    public String toString() {
        return "Profile{" +
                "displayName='" + displayName + '\'' +
                ", emailAddress='" + emailAddress + '\'' +
                ", username='" + username + '\'' +
                ", avatarUrl='" + avatarUrl + '\'' +
                '}';
    }

    private static Profile fromFireBaseUser(FirebaseUser user) {
        return Profile.Builder.createBuilder()
                .setUid(user.getUid())
                .setAvatarUri(user.getPhotoUrl())
                .setDisplayName(user.getDisplayName())
                .setEmailAddress(user.getEmail())
                .getProfile();
    }

    /**
     * Builder for the {@link Profile} class
     * <p>
     * Use the {@link Profile.Builder#createBuilder()} to create a new builder instance.
     */
    public static class Builder {
        private final Profile mProfile;

        public static Profile.Builder createBuilder() {
            return new Profile.Builder();
        }

        protected Builder(FirebaseUser profile) {
            mProfile = Profile.fromFireBaseUser(profile);
        }

        protected Builder() {
            mProfile = new Profile();
        }

        public Profile.Builder setAvatarUri(@Nullable Uri avatarUri) {
            if (avatarUri != null)
                return this.setAvatarUrl(avatarUri.toString());
            return this;
        }

        public Profile.Builder setAvatarUrl(@NonNull String avatarUrl) {
            mProfile.setAvatarUrl(avatarUrl);
            return this;
        }

        public Profile.Builder setUid(@NonNull String uid) {
            mProfile.setUserUID(uid);
            return this;
        }

        public Profile.Builder setEmailAddress(@NonNull String email) {
            mProfile.setEmailAddress(email);
            return this;
        }

        public Profile.Builder setUsername(@NonNull String username) {
            mProfile.setUsername(username);
            return this;
        }

        public Profile.Builder setDisplayName(@NonNull String displayName) {
            mProfile.setDisplayName(displayName);
            return this;
        }

        public Profile.Builder fromFireBaseUser(@NonNull final FirebaseUser user) {
            final Profile.Builder builder = new Profile.Builder(user);
            return builder;
        }

        public Profile getProfile() {
            return mProfile;
        }
    }
}
