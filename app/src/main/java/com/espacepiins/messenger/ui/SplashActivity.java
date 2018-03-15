package com.espacepiins.messenger.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.espacepiins.messenger.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SplashActivity extends Activity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        Thread myThread = new Thread() {
            @Override
            public void run() {
                try {
                    sleep(2000);

                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                    if(user == null){
                        Intent intent = new Intent(SplashActivity.this, AuthActivity.class);
                        startActivity(intent);
                    }else{
                        Intent intent = new Intent(SplashActivity.this, RoomActivity.class);
                        startActivity(intent);
                    }

                    finish();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
        myThread.start();
    }
}
