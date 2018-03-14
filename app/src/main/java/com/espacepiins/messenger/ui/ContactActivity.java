package com.espacepiins.messenger.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;

import com.espacepiins.messenger.model.Contact;
import com.espacepiins.messsenger.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ContactActivity extends AppCompatActivity implements ContactListFragment.OnListFragmentInteractionListener{
    private final String TAG = ContactActivity.class.getName();
    public static final int NO_CONTACT_SELECTED = 0x100;
    public static final int CONTACT_SELECTED = 0x200;
    public static final int INVITE_CONTACT = 0x300;

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
    public void onListFragmentInteraction(Contact item) {
        Log.d(TAG, "onListFragmentInteraction() " + item.getDisplayName());
        Intent resultIntent = new Intent();
        int action = CONTACT_SELECTED;

        if(item.getFirebaseUID() == null){
            action = INVITE_CONTACT;
        }

        resultIntent.putExtra("contact", item);

        resultIntent.putExtra("action", action);
        setResult(Activity.RESULT_OK, resultIntent);
    }
}
