package com.espacepiins.messenger.application;

import android.app.Application;

import com.espacepiins.messenger.db.AppDatabase;
import com.facebook.stetho.Stetho;


/**
 * Created by guillaume on 18-02-21.
 */

public class MessengerApplicationContext extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        Stetho.initialize(Stetho.newInitializerBuilder(this)
        .enableWebKitInspector(Stetho.defaultInspectorModulesProvider(this))
        .build());
    }

    public AppDatabase getAppDatabaseInstance(){
        return AppDatabase.getInstance(this);
    }
}
