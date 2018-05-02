package com.espacepiins.messenger.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;

import com.crashlytics.android.Crashlytics;
import com.espacepiins.messenger.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SplashActivity extends Activity {
    public static final String ACTION_NEW_MESSAGE = "com.espacepiins.messenger.action.NEW_MESSAGE";
    public static final String EXTRA_ROOM_ID = "roomId";
    public Bundle extras;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        extras = getIntent().getExtras();

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
        if (getIntent().getAction().equals(ACTION_NEW_MESSAGE)) {
            intent.putExtra(EXTRA_ROOM_ID, getIntent().getStringExtra(EXTRA_ROOM_ID));
        }
        startActivity(intent);
        finish();
    }

    private void toRoomActivity() {
        Intent intent = new Intent(SplashActivity.this, RoomActivity.class);
        if (getIntent().getAction().equals(ACTION_NEW_MESSAGE)) {
            intent.putExtra(EXTRA_ROOM_ID, getIntent().getStringExtra(EXTRA_ROOM_ID));
        }
        startActivity(intent);
        finish();
    }
}
