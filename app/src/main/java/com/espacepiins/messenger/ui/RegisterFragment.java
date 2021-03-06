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
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.espacepiins.messenger.R;
import com.espacepiins.messenger.application.FirebaseRefs;
import com.espacepiins.messenger.model.Profile;
import com.espacepiins.messenger.ui.callback.OnAuthFragmentReplaceListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

/**
 * A simple {@link Fragment} subclass.
 */
public class RegisterFragment extends Fragment implements View.OnClickListener {
    private final String TAG = RegisterFragment.class.getName();
    private Button mButtonReg;

    /**
     * Registration listener
     */
    interface OnRegistrationListener {
        /**
         * Call on succesfull firebase user registration
         * @param user the registered firebase user
         */
        void onRegisterSuccess(FirebaseUser user);
    }

    private OnRegistrationListener mRegistrationListener;
    private OnAuthFragmentReplaceListener mOnAuthFragmentReplaceListener;
    private EditText emailField;
    private EditText passwordField;
    private EditText passwordConfirmField;
    private CheckBox tosCk;
    private ProgressBar mRegisterProgress;
    private Button mSignin;

    public RegisterFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        // The fragment require the host activity to implement
        // the OnRegistrationListener
        if (context instanceof OnRegistrationListener) {
            mRegistrationListener = (OnRegistrationListener) context;
        } else {
            throw new RuntimeException("The host activity must implement OnRegistrationListener");
        }

        // The fragment require the host activity to implement
        // the OnAuthFragmentReplaceListener to handle navigation between
        // login/register view
        if (context instanceof OnAuthFragmentReplaceListener) {
            mOnAuthFragmentReplaceListener = (OnAuthFragmentReplaceListener) context;
        } else {
            throw new RuntimeException("The host activity must implement OnAuthFragmentReplaceListener");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.register_fragment_layout, container, false);

        mButtonReg = view.findViewById(R.id.register);
        emailField = view.findViewById(R.id.emailField);
        passwordField = view.findViewById(R.id.password);
        passwordConfirmField = view.findViewById(R.id.password_confirm);
        tosCk = view.findViewById(R.id.tos_check);
        mSignin = view.findViewById(R.id.signinBtn);
        mRegisterProgress = view.findViewById(R.id.register_progress);

        mButtonReg.setOnClickListener(this);

        mSignin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOnAuthFragmentReplaceListener.onReplaceFragment(AuthActivity.AuthFragment.SIGNIN_FRAGMENT);
            }
        });

        return view;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mOnAuthFragmentReplaceListener = null;
        mRegistrationListener = null;
    }

    @Override
    public void onClick(View v) {
        boolean valid = true;

        final String email = emailField.getText().toString().trim();

        // Email is required
        if (email.isEmpty()) {
            emailField.setError(getString(R.string.email_require_message));
            valid = false;
        }
        // Email should be a valid email address
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailField.setError(getString(R.string.invalid_email_message));
            valid = false;
        }

        final String password = passwordField.getText().toString();
        final String passwordConfirm = passwordConfirmField.getText().toString();

        // Password is required
        if (password.isEmpty()) {
            passwordField.setError(getString(R.string.password_require_message));
            valid = false;
        }

        // Password length must be > 8 characters long
        // TODO: maybe add more rules
        if (password.length() < 8) {
            passwordField.setError(getString(R.string.password_too_short_message));
            valid = false;
        }

        // Both password and password confirm must match
        if (passwordConfirm.compareTo(password) != 0) {
            passwordConfirmField.setError(getString(R.string.password_no_match_message));
            valid = false;
        }

        // User have to accept terms of use
        if (!tosCk.isChecked()) {
            tosCk.setError(getString(R.string.tos_error_message));
            valid = false;
        }

        if (valid) {
            register(email, password);
        }
    }

    /**
     * Register a new user
     * @param email the user email address
     * @param password the user password
     */
    public void register(String email, String password) {
        mRegisterProgress.setVisibility(View.VISIBLE);
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        mRegisterProgress.setVisibility(View.GONE);
                        // Registration success
                        if (task.isSuccessful()) {
                            Log.d(TAG, "createUserWithEmail:success");
                            final FirebaseUser user = task.getResult().getUser();
                            createProfile(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    registerFCMToken(user);
                                    mRegistrationListener.onRegisterSuccess(user);
                                }
                            });
                        }

                        // Registration failed
                        else {
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
//                            Toast.makeText(getActivity(), getString(R.string.registration_failed_message) + task.getException().getLocalizedMessage(),
//                                    Toast.LENGTH_SHORT).show();

                            Snackbar.make(RegisterFragment.this.getView(),
                                    getString(R.string.registration_failed_message) + task.getException().getLocalizedMessage(),
                                    Snackbar.LENGTH_LONG).show();
                        }
                    }
                });
    }

//    private Task<Void> registerUser(FirebaseUser user){
//        return FirebaseDatabase.getInstance()
//                .getReference(FirebaseRefs.REGISTERED_USERS_REF)
//                .child(user.getUid())
//                .setValue(User.Builder
//                        .createBuilder()
//                        .fromFireBaseUser(user)
//                        .getUser()
//                        .toMap());
//    }

    private Task<Void> createProfile(FirebaseUser user) {
        return FirebaseDatabase.getInstance()
                .getReference(FirebaseRefs.USER_PROFILES_REF(user.getUid()))
                .setValue(Profile.Builder
                        .createBuilder()
                        .fromFireBaseUser(user)
                        .getProfile()
                        .toMap());
    }

    private void registerFCMToken(FirebaseUser user) {
        FirebaseDatabase
                .getInstance()
                .getReference(FirebaseRefs
                        .USER_NOTIFICATION_TOKEN_REF(
                                user.getUid(),
                                FirebaseInstanceId.getInstance().getToken()
                        )).setValue(true);
    }
}
