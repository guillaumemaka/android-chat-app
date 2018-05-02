package com.espacepiins.messenger.ui.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.MutableLiveData;
import android.content.Context;
import android.support.annotation.NonNull;

import com.espacepiins.messenger.R;
import com.espacepiins.messenger.application.Constants;

/**
 * Created by guillaume on 18-03-17.
 */
public class AppViewModel extends AndroidViewModel {
    private static AppViewModel sInstance;
    private MutableLiveData<Boolean> mContactLoaded;

    protected AppViewModel(@NonNull Application application) {
        super(application);

        mContactLoaded = new MutableLiveData<>();
        mContactLoaded.setValue(false);

        if (application.getApplicationContext().getSharedPreferences(application.getApplicationContext().getString(R.string.preference_key), Context.MODE_PRIVATE)
                .getLong(Constants.LAST_CONTACT_IMPORTED, 0) != 0) {
            mContactLoaded.setValue(true);
        }
    }

    public static AppViewModel getInstance(Application application){
        if(sInstance == null){
            sInstance = new AppViewModel(application);
        }

        return sInstance;
    }

    public MutableLiveData<Boolean> isContactLoaded() {
        return mContactLoaded;
    }
}
