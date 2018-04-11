package com.espacepiins.messenger.model;

import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.util.Log;

import com.espacepiins.messsenger.BR;
import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.HashMap;
import java.util.Map;

@IgnoreExtraProperties
public class Profile extends BaseObservable {
    private String displayName;
    private String emailAddress;
    private String username;
    private String avatarUrl;

    public Profile() {
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
}
