package com.espacepiins.messenger.util;

import com.espacepiins.messenger.application.FirebaseRefs;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.Date;

public final class FirebaseUtil {
    public static void setConnected(boolean connected) {
        final FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            String userConnectionRef = FirebaseRefs.USERS_PRESENCE_REF(currentUser.getUid());
            String lastOnlineRef = FirebaseRefs.USER_LAST_ONLINE_REF(currentUser.getUid());
            database.getReference(userConnectionRef).setValue(connected);
            if (!connected)
                database.getReference(lastOnlineRef).setValue(new Date().getTime());
        }
    }

    public static void registerFcmIfNeeded() {
        final FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            FirebaseMessaging.getInstance().setAutoInitEnabled(true);
            final String token = FirebaseInstanceId.getInstance().getToken();

            FirebaseDatabase.getInstance().getReference(FirebaseRefs.USER_NOTIFICATION_TOKEN_REF(currentUser.getUid(), token))
                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (!dataSnapshot.exists()) {
                                dataSnapshot.getRef().setValue(true);
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
        }
    }
}
