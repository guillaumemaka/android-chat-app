package com.espacepiins.messenger.model;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by guillaume on 18-03-09.
 */

@IgnoreExtraProperties
public class User {
    private String uid;
    private String email;
    private String phone;
    private String registeredAt;

    public User() {
    }

    public User(String uid, String email, String phone, String registeredAt) {
        this.uid = uid;
        this.email = email;
        this.phone = phone;
        this.registeredAt = registeredAt;
    }

    public String getUid() {
        return uid;
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
        Map<String, Object> values = new HashMap<>();
        values.put("uid", uid);
        values.put("phone", phone);
        values.put("email", email);
        values.put("registeredAt", registeredAt);
        return values;
    }
}
