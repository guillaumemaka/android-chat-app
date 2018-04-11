package com.espacepiins.messenger.ui;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.espacepiins.messenger.ui.callback.OnAuthFragmentReplaceListener;
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
    private static final String TAG = LoginFragment.class.getName();
    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */

    private EditText emailField;
    private EditText passwordField;
    private Button signUp;
    private Button buttonLog;
    private ProgressBar mLoginProgress;

    private OnAuthFragmentReplaceListener mOnAuthFragmentReplaceListener;
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
        mLoginProgress = view.findViewById(R.id.login_progress);

        buttonLog.setOnClickListener(this);
        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOnAuthFragmentReplaceListener.onReplaceFragment(AuthActivity.AuthFragment.SIGNUP_FRAGMENT);
            }
        });
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        // The fragment require the host activity to implement
        // the OnRegistrationListener
        if(context instanceof OnSigninListener){
            mOnSigninListener = (OnSigninListener) context;
        }else {
            throw new RuntimeException("The host activity must implement OnRegistrationListener");
        }

        // The fragment require the host activity to implement
        // the OnAuthFragmentReplaceListener to handle navigation between
        // login/register view
        if(context instanceof OnAuthFragmentReplaceListener){
            mOnAuthFragmentReplaceListener = (OnAuthFragmentReplaceListener) context;
        }else {
            throw new RuntimeException("The host activity must implement OnAuthFragmentReplaceListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mOnAuthFragmentReplaceListener = null;
        mOnSigninListener = null;
    }

    @Override
    public void onClick(View v) {
        boolean valid = true;
        //register
        final String email = emailField.getText().toString().trim();

        // Email is required
        if(email.isEmpty()){
            emailField.setError(getString(R.string.email_require_message));
            valid = false;
        }

        // Email must be a valid email address
        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            emailField.setError(getString(R.string.invalid_email_message));
            valid = false;
        }

        final String password = passwordField.getText().toString();

        // Password is required
        if(password.isEmpty()){
            passwordField.setError(getString(R.string.password_require_message));
            valid = false;
        }

        if(valid){
            login(email, password);
        }
    }

    /**
     * Logged in a user
     * @param email the user email address
     * @param password the user password
     */
    public void login(String email, String password) {
        mLoginProgress.setVisibility(View.VISIBLE);
        FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                mLoginProgress.setVisibility(View.GONE);
                if (task.isSuccessful()) {
                    // User successfully logged in
                    Log.d(TAG, "signInWithEmail:success");
                    FirebaseUser user = task.getResult().getUser();
                    mOnSigninListener.onSigninSuccess(user);
                    Toast.makeText(getActivity(), "Connected !",
                            Toast.LENGTH_SHORT).show();
                } else {
                    // Something went wrong
                    Log.w(TAG, "signInWithEmail:failure", task.getException());
//                    Toast.makeText(getActivity(), getString(R.string.login_failed_message) + task.getException().getLocalizedMessage(),
//                            Toast.LENGTH_SHORT).show();
                    Snackbar.make(LoginFragment.this.getView(),
                            getString(R.string.login_failed_message) + task.getException().getLocalizedMessage(),
                            Snackbar.LENGTH_LONG).show();
                }
            }
        });
    }




}
