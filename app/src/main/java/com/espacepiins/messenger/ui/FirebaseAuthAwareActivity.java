package com.espacepiins.messenger.ui;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.espacepiins.messenger.util.FirebaseUtil;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/**
 * Created by guillaume on 18-03-22.
 */

public class FirebaseAuthAwareActivity extends AppCompatActivity implements FirebaseAuth.AuthStateListener {
    private final String TAG = FirebaseAuthAwareActivity.class.getName();

    protected FirebaseUser mCurrentUser;

    @Override
    protected void onStart() {
        listenOnUserSignout();
        super.onStart();
    }

    @Override
    protected void onPause() {
        unListenOnUserSignout();
        super.onPause();
    }

    @Override
    protected void onStop() {
        unListenOnUserSignout();
        super.onStop();
    }

    private void listenOnUserSignout() {
        Log.d(TAG, "listenOnUserDisconnect");
        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
        FirebaseAuth.getInstance().addAuthStateListener(this);
    }

    private void unListenOnUserSignout() {
        Log.d(TAG, "unListenOnUserDisconnect");
        FirebaseAuth.getInstance().removeAuthStateListener(this);
    }

    @Override
    public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
        Log.d(TAG, "onAuthStateChanged");
        mCurrentUser = firebaseAuth.getCurrentUser();
        if (mCurrentUser == null) {
            Intent authIntent = new Intent(this, AuthActivity.class);
            startActivity(authIntent);
            finish();
        } else {
            FirebaseUtil.setConnected(true);
        }
    }
}
