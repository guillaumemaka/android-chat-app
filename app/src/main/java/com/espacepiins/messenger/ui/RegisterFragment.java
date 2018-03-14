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
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.espacepiins.messenger.ui.callback.OnNavigationChange;
import com.espacepiins.messsenger.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import static android.content.ContentValues.TAG;

/**
 * A simple {@link Fragment} subclass.
 */
public class RegisterFragment extends Fragment implements View.OnClickListener{

    private Button mButtonReg;

    interface OnRegistrationListener {
        void onRegisterSuccess(FirebaseUser user);
    }

    private OnRegistrationListener mRegistrationListener;
    private OnNavigationChange mOnNavigationChange;
    private FirebaseAuth mAuth;
    private EditText emailField;
    private EditText passwordField;
    private EditText passwordConfirmField;
    private CheckBox tosCk;
    private Button mSignin;

    public RegisterFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if(context instanceof OnRegistrationListener){
            mRegistrationListener = (OnRegistrationListener) context;
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.register_fragment_layout, container, false);

        mButtonReg = view.findViewById(R.id.register);
        emailField = view.findViewById(R.id.emailField);
        passwordField =  view.findViewById(R.id.password);
        passwordConfirmField = view.findViewById(R.id.password_confirm);
        tosCk = view.findViewById(R.id.tos_check);
        mSignin = view.findViewById(R.id.signinBtn);

        mButtonReg.setOnClickListener(this);

        mSignin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOnNavigationChange.navigateTo(AuthActivity.AuthPage.SIGNIN);
            }
        });
        return view;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mOnNavigationChange = null;
        mRegistrationListener = null;
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
        final String passwordConfirm = passwordConfirmField.getText().toString();

        if(password.isEmpty()){
            passwordField.setError(getString(R.string.password_require_message));
            return;
        }

        if(password.length() < 8){
            passwordField.setError(getString(R.string.password_too_short_message));
            return;
        }

        if(passwordConfirm.compareTo(password) != 0){
            passwordConfirmField.setError(getString(R.string.password_no_match_message));
            return;
        }

        Query dbRef = FirebaseDatabase.getInstance().getReference("registered-users").child("email");

        dbRef.equalTo(email)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        register(email, password);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    public void register(String email, String password) {

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            mRegistrationListener.onRegisterSuccess(user);
                        }

                        //Nooooo! You failed!
                        else {
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(getActivity(), "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }




}
