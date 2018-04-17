package com.espacepiins.messenger.util;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;

public final class FirebaseUtil {
    public static void setConnected(boolean connected) {
        final FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            String userConnectionRef = String.format("/users/%s/connected", currentUser.getUid());
            String lastOnlineRef = String.format("/users/%s/lastOnline", currentUser.getUid());
            database.getReference(userConnectionRef).setValue(connected);
            if (!connected)
                database.getReference(lastOnlineRef).setValue(ServerValue.TIMESTAMP);
        }
    }
}
