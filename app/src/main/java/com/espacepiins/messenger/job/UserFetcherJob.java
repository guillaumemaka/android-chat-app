package com.espacepiins.messenger.job;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.espacepiins.messenger.application.MessengerApplicationContext;
import com.espacepiins.messenger.db.AppDatabase;
import com.espacepiins.messenger.db.entity.ContactEntity;
import com.espacepiins.messenger.db.entity.EmailEntity;
import com.espacepiins.messenger.model.Profile;
import com.espacepiins.messenger.model.SearchContactResult;
import com.firebase.jobdispatcher.Constraint;
import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.firebase.jobdispatcher.Job;
import com.firebase.jobdispatcher.JobParameters;
import com.firebase.jobdispatcher.JobService;
import com.firebase.jobdispatcher.Lifetime;
import com.firebase.jobdispatcher.RetryStrategy;
import com.firebase.jobdispatcher.Trigger;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class UserFetcherJob extends JobService {
    public static final String JOB_TAG = UserFetcherJob.class.getName();

    public static FirebaseJobDispatcher createJob(Context context) {
        final FirebaseJobDispatcher dispatcher = new FirebaseJobDispatcher(new GooglePlayDriver(context));
        Job job = dispatcher.newJobBuilder()
                .setService(UserFetcherJob.class)
                .setTag(JOB_TAG)
                .setTrigger(Trigger.executionWindow(0, 120))
                .setRecurring(true)
                .setLifetime(Lifetime.FOREVER)
                .setRetryStrategy(RetryStrategy.DEFAULT_EXPONENTIAL)
                .setReplaceCurrent(true)
                .setConstraints(
                        Constraint.ON_UNMETERED_NETWORK
                )
                .build();
        dispatcher.mustSchedule(job);
        return dispatcher;
    }

    @Override
    public boolean onStartJob(JobParameters job) {
        Log.i(JOB_TAG, "Job Started!");

        if (FirebaseAuth.getInstance().getCurrentUser() == null)
            return true;

        new FetchUserAsyncTask().execute();

        return false;
    }

    @Override
    public boolean onStopJob(JobParameters job) {
        return false;
    }

    class FetchUserAsyncTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            final FirebaseDatabase database = FirebaseDatabase.getInstance();
            final AppDatabase db = ((MessengerApplicationContext) getApplication()).getAppDatabaseInstance();
            final List<SearchContactResult> unregisteredContacts = db.contactDao().getUnregisteredContacts();
            final DatabaseReference registeredUsersRef = database.getReference("profiles");

            for (SearchContactResult searchContactResult : unregisteredContacts) {
                for (EmailEntity emailEntity : searchContactResult.getEmailAddresses())
                    registeredUsersRef.equalTo(emailEntity.getEmailAddress());
            }

            registeredUsersRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    final AsyncTask<DataSnapshot, Void, Void> task = new UpdateContactAsyncTask(db);
                    task.execute(dataSnapshot);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.w(JOB_TAG, databaseError.getMessage(), databaseError.toException());
                }
            });

            return null;
        }

        private class UpdateContactAsyncTask extends AsyncTask<DataSnapshot, Void, Void> {
            private final AppDatabase mDb;

            public UpdateContactAsyncTask(AppDatabase db) {
                mDb = db;
            }

            @Override
            protected Void doInBackground(DataSnapshot... dataSnapshots) {
                for (DataSnapshot snapshot : dataSnapshots[0].getChildren()) {
                    final Profile profile = snapshot.getValue(Profile.class);
                    final List<SearchContactResult> results = mDb.contactDao().search(profile.getEmailAddress());
                    if (results.size() > 0) {
                        Log.i(JOB_TAG, "Found a registered user in contact " + profile);
                        final ContactEntity contact = mDb.contactDao().getByLookupKey(results.get(0).getLookupKey());
                        contact.setFirebaseUID(snapshot.getKey());
                        mDb.contactDao().update(contact);
                    }
                }
                return null;
            }
        }
    }
}
