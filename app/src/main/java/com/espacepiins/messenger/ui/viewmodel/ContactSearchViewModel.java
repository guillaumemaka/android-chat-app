package com.espacepiins.messenger.ui.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.MutableLiveData;
import android.os.AsyncTask;
import android.support.annotation.NonNull;

import com.espacepiins.messenger.db.AppDatabase;
import com.espacepiins.messenger.model.SearchContactResult;

import java.util.List;

/**
 * Created by guillaume on 18-03-24.
 */

public class ContactSearchViewModel extends AndroidViewModel {
    private MutableLiveData<List<SearchContactResult>> mContactSearchResults;

    public ContactSearchViewModel(@NonNull Application application) {
        super(application);
        mContactSearchResults = new MutableLiveData<>();
    }

    public MutableLiveData<List<SearchContactResult>> getContactSearchResults() {
        return mContactSearchResults;
    }

    public void search(@NonNull String term){
        new searchContactAsyncTask(AppDatabase.getInstance(this.getApplication())).execute(term);
    }

    public class searchContactAsyncTask extends AsyncTask<String, Void, List<SearchContactResult>>{
        private AppDatabase mDb;

        public searchContactAsyncTask(AppDatabase db) {
            mDb = db;
        }

        @Override
        protected List<SearchContactResult> doInBackground(String... terms) {
            if(terms == null){
                return mDb.contactDao().search("");
            }

            if(terms.length == 0){
                return mDb.contactDao().search("");
            }

            return mDb.contactDao().search(terms[0]);
        }

        @Override
        protected void onPostExecute(List<SearchContactResult> searchContactResults) {
            mContactSearchResults.postValue(searchContactResults);
        }
    }
}
