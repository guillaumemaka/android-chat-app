package com.espacepiins.messenger.service;

import com.espacepiins.messenger.application.FirebaseRefs;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

public class FCMInstanceIdService extends FirebaseInstanceIdService {
    @Override
    public void onTokenRefresh() {
        super.onTokenRefresh();
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String refreshToken = FirebaseInstanceId.getInstance().getToken();
            FirebaseDatabase
                    .getInstance()
                    .getReference(FirebaseRefs
                            .USER_NOTIFICATION_TOKEN_REF(currentUser.getUid(), refreshToken))
                    .setValue(true);
        }
    }
}
