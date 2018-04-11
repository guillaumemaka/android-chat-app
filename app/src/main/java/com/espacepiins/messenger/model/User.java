package com.espacepiins.messenger.model;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by guillaume on 18-03-09.
 */

@IgnoreExtraProperties
public class User  {
    private String uid;
    private String email;
    private String phone;
    private String displayName;
    private String avatarUrl;
    private String registeredAt;

    public User() {
    }

    public User(String uid, String email, String phone, String displayName, String registeredAt) {
        this.uid = uid;
        this.email = email;
        this.phone = phone;
        this.displayName = displayName;
        this.registeredAt = registeredAt;
    }

    public String getUid() {
        return uid;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getRegisteredAt() {
        return registeredAt;
    }

    public void setRegisteredAt(String registeredAt) {
        this.registeredAt = registeredAt;
    }

    @Override
    public String toString() {
        return "Email: " + email + ", Phone: " + phone + ", Registered at: " + registeredAt.toString();
    }

    @Exclude
    public Map<String, Object> toMap(){
        final Map<String, Object> values = new HashMap<>();
        values.put("uid", uid);
        values.put("phone", phone);
        values.put("email", email);
        values.put("registeredAt", registeredAt);
        values.put("avatarUrl", avatarUrl);
        return values;
    }

    public static User fromFireBaseUser(final FirebaseUser user) {
        return Builder.createBuilder()
                .setUid(user.getUid())
                .setAvatarUri(user.getPhotoUrl())
                .setDisplayName(user.getDisplayName())
                .setEmail(user.getEmail())
                .setPhone(user.getPhoneNumber())
                .getUser();
    }

    /**
     * Builder for the {@link User} class
     *
     * Use the {@link Builder#createBuilder()} to create a new builder instance.
     */
    public static class Builder {
        private final User mUser;

        public static Builder createBuilder(){
            return new Builder();
        }

        protected Builder(FirebaseUser user){
            mUser = User.fromFireBaseUser(user);
        }

        protected Builder(){
            mUser = new User();
        }

        public Builder setAvatarUri(@Nullable Uri avatarUri){
            if(avatarUri != null)
                return this.setAvatarUrl(avatarUri.toString());
            return this;
        }

        public Builder setAvatarUrl(@NonNull String avatarUrl){
            mUser.setAvatarUrl(avatarUrl);
            return this;
        }

        public Builder setUid(@NonNull String uid) {
            mUser.setUid(uid);
            return this;
        }

        public Builder setEmail(@NonNull String email) {
            mUser.setEmail(email);
            return this;
        }

        public Builder setPhone(@NonNull String phone) {
            mUser.setPhone(phone);
            return this;
        }

        public Builder setDisplayName(@NonNull String displayName) {
            mUser.setDisplayName(displayName);
            return this;
        }

        public Builder setRegisteredAt(@NonNull String registeredAt) {
            mUser.setRegisteredAt(registeredAt);
            return this;
        }

        public Builder fromFireBaseUser(@NonNull final FirebaseUser user){
            final Builder builder = new Builder(user);
            builder.setRegisteredAt(new Date().toString());
            return builder;
        }

        public User getUser(){
            return mUser;
        }
    }
}
