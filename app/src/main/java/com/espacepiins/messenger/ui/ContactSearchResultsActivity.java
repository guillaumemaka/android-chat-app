package com.espacepiins.messenger.ui;

import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Adapter;
import android.widget.ListView;

import com.espacepiins.messsenger.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ContactSearchResultsActivity extends AppCompatActivity {


    @BindView(R.id.contact_list_view)
    ListView mContactListView;
    Adapter mContactAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_search_results);
        ButterKnife.bind(this);
//        mContactAdapter = new ContactAdapter();
//        mContactListView.setAdapter(mContactAdapter);
        handleIntent(getIntent());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        handleIntent(intent);
    }

    private void handleIntent(Intent intent){
        if(isSearchIntent(intent)){
            String query = intent.getStringExtra(SearchManager.QUERY);
            searchContacts(query);
        }

    }

    private void searchContacts(String query){

    }

    private boolean isSearchIntent(Intent intent){
        return Intent.ACTION_SEARCH.equals(intent.getAction());
    }
}
