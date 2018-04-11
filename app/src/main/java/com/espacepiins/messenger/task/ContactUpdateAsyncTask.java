package com.espacepiins.messenger.task;

import android.os.AsyncTask;

import com.espacepiins.messenger.db.AppDatabase;
import com.espacepiins.messenger.db.entity.ContactEntity;

/**
 * Created by guillaume on 18-03-23.
 */

public class ContactUpdateAsyncTask extends AsyncTask<ContactEntity, Void, Void> {
    private AppDatabase mDb;

    public ContactUpdateAsyncTask(AppDatabase db) {
        mDb = db;
    }

    @Override
    protected Void doInBackground(ContactEntity... contactEntities) {
        if(contactEntities.length > 0){
            mDb.contactDao().update(contactEntities);
        }
        return null;
    }
}
