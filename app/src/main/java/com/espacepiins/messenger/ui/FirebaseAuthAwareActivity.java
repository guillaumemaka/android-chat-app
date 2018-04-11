package com.espacepiins.messenger.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/**
 * Created by guillaume on 18-03-22.
 */

public class FirebaseAuthAwareActivity extends AppCompatActivity implements FirebaseAuth.AuthStateListener {
    private final String TAG = FirebaseAuthAwareActivity.class.getName();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState, @Nullable PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
        listenOnUserDisconnect();
    }

    @Override
    protected void onPause() {
        unListenOnUserDisconnect();
        super.onPause();
    }

    @Override
    protected void onResume() {
        listenOnUserDisconnect();
        super.onResume();
    }

    private void listenOnUserDisconnect(){
        Log.d(TAG, "listenOnUserDisconnect");
        FirebaseAuth.getInstance().addAuthStateListener(this);
    }

    private void unListenOnUserDisconnect(){
        Log.d(TAG, "unListenOnUserDisconnect");
        FirebaseAuth.getInstance().removeAuthStateListener(this);
    }

    @Override
    public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
        Log.d(TAG, "onAuthStateChanged");
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if(user == null){
            Intent authIntent = new Intent(this, AuthActivity.class);
            startActivity(authIntent);
            finish();
        }
    }
}
