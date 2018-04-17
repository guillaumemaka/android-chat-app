package com.espacepiins.messenger.application;

import android.app.Application;
import android.arch.lifecycle.ProcessLifecycleOwner;

import com.crashlytics.android.Crashlytics;
import com.espacepiins.messenger.db.AppDatabase;
import com.espacepiins.messenger.ui.viewmodel.AppViewModel;
import com.espacepiins.messsenger.BuildConfig;
import com.facebook.stetho.Stetho;
import com.google.firebase.database.FirebaseDatabase;

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

        ProcessLifecycleOwner.get().getLifecycle().addObserver(new ApplicationLifecycleOwner());
    }

    public AppDatabase getAppDatabaseInstance(){
        return AppDatabase.getInstance(this);
    }

    public AppViewModel getAppViewModel(){
        return AppViewModel.getInstance(this);
    }
}
