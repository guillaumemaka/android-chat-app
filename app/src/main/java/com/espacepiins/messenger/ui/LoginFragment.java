package com.espacepiins.messenger.ui;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.espacepiins.messenger.ui.callback.OnNavigationChange;
import com.espacepiins.messsenger.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/**
 * A simple {@link Fragment} subclass.
 */


public class LoginFragment extends Fragment implements View.OnClickListener {

    interface OnSigninListener {
        void onSigninSuccess(FirebaseUser user);
    }

    /**
     * Id to identity READ_CONTACTS permission request.
     */
    private static final int REQUEST_READ_CONTACTS = 0;

    /**
     * A dummy authentication store containing known user names and passwords.
     * TODO: remove after connecting to a real authentication system.
     */
    private static final String[] DUMMY_CREDENTIALS = new String[]{
            "foo@example.com:hello", "bar@example.com:world"
    };
    private static final String TAG = null;
    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */

    private FirebaseAuth mAuth;
    private EditText emailField;
    private EditText passwordField;
    private Button signUp;
    private Button buttonLog;
    private OnNavigationChange mOnNavigationChange;
    private OnSigninListener mOnSigninListener;

    public LoginFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.login_fragment_layout, container, false);


        buttonLog = view.findViewById(R.id.Logbtn);
        emailField =  view.findViewById(R.id.emailField);
        passwordField = view.findViewById(R.id.passwordField);
        signUp = view.findViewById(R.id.signupBtn);

        buttonLog.setOnClickListener(this);
        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOnNavigationChange.navigateTo(AuthActivity.AuthPage.SIGNUP);
            }
        });
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if(context instanceof OnSigninListener){
            mOnSigninListener = (OnSigninListener) context;
        }else {
            throw new RuntimeException("The host activity must implement OnRegistrationListener");
        }

        if(context instanceof OnNavigationChange){
            mOnNavigationChange = (OnNavigationChange) context;
        }else {
            throw new RuntimeException("The host activity must implement OnNavigationChange");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mOnNavigationChange = null;
        mOnSigninListener = null;
    }

    @Override
    public void onClick(View v) {

        //register
        final String email = emailField.getText().toString().trim();

        if(email.isEmpty()){
            emailField.setError(getString(R.string.email_require_message));
            return;
        }

        if(Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            emailField.setError(getString(R.string.invalid_email_message));
            return;
        }

        final String password = passwordField.getText().toString();

        if(password.isEmpty()){
            passwordField.setError(getString(R.string.password_require_message));
            return;
        }

        if(password.length() < 8){
            passwordField.setError(getString(R.string.password_too_short_message));
            return;
        }

        login(email, password);
    }


    public void login(String email, String password) {


        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {


                if (task.isSuccessful()) {
                    // Yes, vous êtes connecter
                    Log.d(TAG, "signInWithEmail:success");
                    FirebaseUser user = mAuth.getCurrentUser();
                    mOnSigninListener.onSigninSuccess(user);
                    Toast.makeText(getActivity(), "Connected !",
                            Toast.LENGTH_SHORT).show();
                } else {
                    // Nope
                    Log.w(TAG, "signInWithEmail:failure", task.getException());
                    Toast.makeText(getActivity(), "Authentication failed.",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }




}
