package com.espacepiins.messenger.ui;


import android.app.Activity;
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

import static android.content.ContentValues.TAG;

/**
 * A simple {@link Fragment} subclass.
 */
public class RegisterFragment extends Fragment implements View.OnClickListener{


    private FirebaseAuth mAuth;
    private EditText pseudoField;
    private EditText emailField;
    private EditText passwordField;



    public RegisterFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.register_fragment_layout, container, false);

        Button buttonReg = (Button) view.findViewById(R.id.register);
        emailField = (EditText) view.findViewById(R.id.emailField);
        passwordField = (EditText) view.findViewById(R.id.passwordField);
        pseudoField = (EditText) view.findViewById(R.id.pseudoField);

        buttonReg.setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View v) {

        //register
        String email = emailField.getText().toString();
        String password = passwordField.getText().toString();
        String pseudo = pseudoField.getText().toString();
        Register(email, password);
    }

    public void Register(String email, String password) {

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {


                        //Yes, we found you!
                        if (task.isSuccessful()) {
                            Log.d(TAG, "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
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
