package com.espacepiins.messenger.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;

import com.crashlytics.android.Crashlytics;
import com.espacepiins.messsenger.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
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

                    if(user != null){
                        user.reload().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){
                                    toRoomActivity();
                                }else {
                                    toAuthActivity();
                                }
                            }
                        });
                    }else{
                        toAuthActivity();
                    }
                } catch (InterruptedException e) {
                    Crashlytics.logException(e);
                }
            }
        };
        myThread.start();
    }

    private void toAuthActivity() {
        Intent intent = new Intent(SplashActivity.this, AuthActivity.class);
        startActivity(intent);
        finish();
    }

    private void toRoomActivity() {
        Intent intent = new Intent(SplashActivity.this, RoomActivity.class);
        startActivity(intent);
        finish();
    }
}
