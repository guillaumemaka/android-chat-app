package com.espacepiins.messenger.application;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.arch.lifecycle.ProcessLifecycleOwner;
import android.content.Context;
import android.os.Build;

import com.crashlytics.android.Crashlytics;
import com.espacepiins.messenger.BuildConfig;
import com.espacepiins.messenger.R;
import com.espacepiins.messenger.db.AppDatabase;
import com.espacepiins.messenger.ui.viewmodel.AppViewModel;
import com.facebook.stetho.Stetho;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;

import io.fabric.sdk.android.Fabric;


/**
 * Created by guillaume on 18-02-21.
 */

public class MessengerApplicationContext extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        if(BuildConfig.DEBUG){
            Stetho.initialize(Stetho.newInitializerBuilder(this)
            .enableWebKitInspector(Stetho.defaultInspectorModulesProvider(this))
            .build());
        }

        if(!BuildConfig.DEBUG){
            FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        }

        if(BuildConfig.ENABLE_CRASHLYTICS){
            Fabric.with(this, new Crashlytics());
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Create the NotificationChannel, but only on API 26+ because
            // the NotificationChannel class is new and not in the support library
            String channelId = getString(R.string.default_notification_channel_id);
            CharSequence channelName = getString(R.string.default_notification_channel_name);
            String channelDescription = getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel(channelId, channelName, importance);
            channel.setDescription(channelDescription);
//            // Register the channel with the system
            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.createNotificationChannel(channel);
        }

        ProcessLifecycleOwner.get().getLifecycle().addObserver(new ApplicationLifecycleOwner());
        FirebaseMessaging.getInstance().setAutoInitEnabled(false);
    }

    public AppDatabase getAppDatabaseInstance(){
        return AppDatabase.getInstance(this);
    }

    public AppViewModel getAppViewModel(){
        return AppViewModel.getInstance(this);
    }
}
