package com.espacepiins.messenger.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;

import com.espacepiins.messenger.R;
import com.espacepiins.messenger.db.entity.EmailEntity;
import com.espacepiins.messenger.model.SearchContactResult;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ContactActivity extends FirebaseAuthAwareActivity implements ContactListFragment.OnListFragmentInteractionListener {
    private final String TAG = ContactActivity.class.getName();
    public static final int NO_CONTACT_SELECTED = 0x100;
    public static final int CONTACT_SELECTED = 0x200;
    public static final int INVITE_CONTACT = 0x300;
    public static final String EXTRA_DISPLAY_NAME = "displayName";
    public static final String EXTRA_EMAIL = "emailAddress";
    public static final String EXTRA_FIREBASE_UID = "firebase_uid";

    @BindView(R.id.contact_toolbar)
    Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);

        ButterKnife.bind(this);

        setSupportActionBar(mToolbar);

        final ActionBar actionBar = getSupportActionBar();

        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayShowCustomEnabled(true);
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.fragment_container, new ContactListFragment())
                .commit();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onListFragmentInteraction(SearchContactResult item) {
        Log.d(TAG, "onListFragmentInteraction() " + item.getDisplayName());
        Intent resultIntent = new Intent();

        int action = CONTACT_SELECTED;

        if (item.getFirebaseUID() == null) {
            action = INVITE_CONTACT;
        }

        resultIntent.putExtra("action", action);

        if (action == CONTACT_SELECTED) {
            resultIntent.putExtra(EXTRA_FIREBASE_UID, item.getFirebaseUID());
            setResult(Activity.RESULT_OK, resultIntent);
            finish();
        } else {
            final List<String> emails = new ArrayList<>();

            for (int i = 0; i < item.getEmailAddresses().size(); i++) {
                final EmailEntity emailEntity = item.getEmailAddresses().get(i);
                emails.add(emailEntity.getEmailAddress());
            }

//            for (int i = 0; i < item.getPhoneNumbers().size(); i++) {
//                final PhoneEntity phoneEntity = item.getPhoneNumbers().get(i);
//                emails.add(String.format("%s (%s)", phoneEntity.getPhoneNumber(), phoneEntity.getPhoneType()));
//            }

            final AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
            alertDialog.setTitle("Inviter");
            alertDialog.setIcon(R.drawable.ic_men);
            alertDialog.setItems(emails.toArray(new String[emails.size()]), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    resultIntent.putExtra(EXTRA_EMAIL, emails.get(i));
                    resultIntent.putExtra(EXTRA_DISPLAY_NAME, item.getDisplayName());

                    setResult(Activity.RESULT_OK, resultIntent);
                    finish();
                }
            });
            alertDialog.create().show();
        }

    }
}
