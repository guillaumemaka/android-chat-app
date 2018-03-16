package com.espacepiins.messenger.model;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by guillaume on 18-03-15.
 */

@IgnoreExtraProperties
public class UserStats {
    private long lastSeen;

    public UserStats() {}

    /**
     * Get the last seen user timestamp
     * @return
     */
    public long getLastSeen() {
        return lastSeen;
    }

    /**
     * Set the user last seen timestamp
     * @param lastSeen date timestamp
     */
    public void setLastSeen(long lastSeen) {
        this.lastSeen = lastSeen;
    }

    @Exclude
    public Map<String, Object> toMap(){
        final Map<String, Object> values = new HashMap<>();
        values.put("lastSeen", lastSeen);
        return values;
    }
}
