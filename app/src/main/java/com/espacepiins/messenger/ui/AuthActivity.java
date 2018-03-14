package com.espacepiins.messenger.ui;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;

import com.espacepiins.messenger.ui.callback.OnNavigationChange;
import com.espacepiins.messsenger.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/**
 * A login screen that offers login via email/password.
 */
public class AuthActivity extends FragmentActivity implements OnNavigationChange, LoginFragment.OnSigninListener, RegisterFragment.OnRegistrationListener
{
    public enum AuthPage {
        SIGNIN, SIGNUP
    }

    private static final String TAG = null ;
    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.auth_layout);
        // Set up the login form.

        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.add(R.id.auth_container, new LoginFragment());
        ft.commit();

        mAuth = FirebaseAuth.getInstance();

        //Already connect or not ?
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    Log.d(TAG, "OnAuthStatChanged:sign_in" + user.getUid());
                } else {
                    Log.d(TAG, "OnAuthStatChanged:sign_out");
                }
            }
        };


    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    @Override
    public void navigateTo(AuthPage page) {
        switch (page) {
            case SIGNIN:
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.auth_container, new LoginFragment())
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                        .commit();
                break;
            case SIGNUP:
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.auth_container, new RegisterFragment())
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                        .commit();
                break;
        }
    }

    @Override
    public void onSigninSuccess(FirebaseUser user) {

    }

    @Override
    public void onRegisterSuccess(FirebaseUser user) {

    }
}


