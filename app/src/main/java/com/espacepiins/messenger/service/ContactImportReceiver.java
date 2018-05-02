package com.espacepiins.messenger.service;

import android.arch.lifecycle.ViewModelProviders;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;

import com.espacepiins.messenger.R;
import com.espacepiins.messenger.application.Constants;
import com.espacepiins.messenger.ui.viewmodel.AppViewModel;

import java.util.Date;

public class ContactImportReceiver extends BroadcastReceiver {
    public static final String CONTACT_IMPORTED_ACTION = "com.espacepiins.messenger.CONTACT_IMPORTED";
    @Override
    public void onReceive(Context context, Intent intent) {
        ViewModelProviders.of((AppCompatActivity) context)
                .get(AppViewModel.class)
                .isContactLoaded().postValue(true);
        SharedPreferences prefs = context.getSharedPreferences(context.getString(R.string.preference_key), Context.MODE_PRIVATE);
        prefs.edit()
                .putLong(Constants.LAST_CONTACT_IMPORTED, new Date().getTime())
                .apply();
    }
}
