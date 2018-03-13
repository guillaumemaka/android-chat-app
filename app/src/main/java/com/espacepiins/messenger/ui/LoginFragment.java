package com.espacepiins.messenger.ui;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.espacepiins.messsenger.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import android.os.*;

/**
 * A simple {@link Fragment} subclass.
 */


public class LoginFragment extends Fragment implements View.OnClickListener {

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


    public LoginFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.login_fragment_layout, container, false);


        Button buttonlog = (Button) view.findViewById(R.id.register);
        emailField = (EditText) view.findViewById(R.id.emailField);
        passwordField = (EditText) view.findViewById(R.id.passwordField);



        buttonlog.setOnClickListener(this);

        return view;

    }


    @Override
    public void onClick(View v) {

        //register
        String email = emailField.getText().toString();
        String password = passwordField.getText().toString();
        Login(email, password);
    }


    public void Login(String email, String password) {

        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {


                if (task.isSuccessful()) {
                    // Yes, vous êtes connecter
                    Log.d(TAG, "signInWithEmail:success");
                    FirebaseUser user = mAuth.getCurrentUser();
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
