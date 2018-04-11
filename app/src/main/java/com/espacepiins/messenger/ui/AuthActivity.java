package com.espacepiins.messenger.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import com.espacepiins.messenger.ui.callback.OnAuthFragmentReplaceListener;
import com.espacepiins.messsenger.R;
import com.google.firebase.auth.FirebaseUser;

/**
 * A screen that offer login/register capabilities
 */
public class AuthActivity extends FragmentActivity implements OnAuthFragmentReplaceListener, LoginFragment.OnSigninListener, RegisterFragment.OnRegistrationListener
{
    public enum AuthFragment {
        SIGNIN_FRAGMENT, SIGNUP_FRAGMENT
    }

    private static final String TAG = AuthActivity.class.getName() ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.auth_layout);
        // Set up the login form.

        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.add(R.id.auth_container, new LoginFragment());
        ft.commit();
    }

    @Override
    public void onReplaceFragment(AuthFragment fragment) {
        switch (fragment) {
            case SIGNIN_FRAGMENT:
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.auth_container, new LoginFragment())
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                        .commit();
                break;
            case SIGNUP_FRAGMENT:
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.auth_container, new RegisterFragment())
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                        .commit();
                break;
        }
    }

    @Override
    public void onSigninSuccess(FirebaseUser user) {
        goToRoom();
    }

    @Override
    public void onRegisterSuccess(FirebaseUser user) {
        goToRoom();
    }

    public void goToRoom(){
        Intent intent = new Intent(this, RoomActivity.class);
        startActivity(intent);
        finish();
    }
}


