package com.espacepiins.messenger.model.contract;

/**
 * Created by guillaume on 18-03-15.
 */

public interface UserProfileContract {
    void setUid(String uid);

    void setEmail(String email);

    void setPhone(String phone);

    void setDisplayName(String displayName);

    void setRegisteredAt(String registeredAt);
}
