package com.espacepiins.messenger.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.espacepiins.messenger.util.FirebaseUtil;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/**
 * Created by guillaume on 18-03-22.
 */

public abstract class FirebaseAuthAwareActivity extends AppCompatActivity implements FirebaseAuth.AuthStateListener {
    private final String TAG = FirebaseAuthAwareActivity.class.getName();

    protected FirebaseUser mCurrentUser;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (mCurrentUser == null)
            goToAuth();
    }

    @Override
    protected void onStart() {
        super.onStart();
        listenOnUserSignout();
    }

    @Override
    protected void onPause() {
        super.onPause();
        unListenOnUserSignout();
    }

    @Override
    protected void onStop() {
        super.onStop();
        unListenOnUserSignout();
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
            goToAuth();
        } else {
            FirebaseUtil.setConnected(true);
        }
    }

    protected void goToAuth() {
        Intent authIntent = new Intent(this, AuthActivity.class);
        startActivity(authIntent);
        finish();
    }
}
