package com.espacepiins.messenger.ui;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.espacepiins.messenger.db.AppDatabase;
import com.espacepiins.messenger.model.Contact;
import com.espacepiins.messenger.R;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class ContactListFragment extends Fragment {

    public static final String SEARCH_TERM_KEY = "search_term";
    private final int READ_CONTACTS_REQUEST_CODE = 04400;

    private final String TAG = ContactListFragment.class.getName();
    private ContactAdapter mAdapter;
    private AppDatabase mAppDatabase;
    private String mSearchTerm;

    @BindView(R.id.list)
    RecyclerView mRecyclerView;

    @BindView(R.id.progress)
    ProgressBar mProgressBar;

    private OnListFragmentInteractionListener mListener;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ContactListFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(savedInstanceState != null){
            mSearchTerm = savedInstanceState.getString(SEARCH_TERM_KEY,null);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_contact_list, container, false);

        ButterKnife.bind(this, view);

        mAdapter = new ContactAdapter();
        mAdapter.setListener(mListener);

        mProgressBar.setVisibility(View.GONE);
        mRecyclerView.setVisibility(View.GONE);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.setAdapter(mAdapter);

        setHasOptionsMenu(true);

        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof OnListFragmentInteractionListener) {
            mListener = (OnListFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
        }

        mAppDatabase = AppDatabase.getInstance(getContext());
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        new SearchContactAsyncTask().execute();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putString(SEARCH_TERM_KEY, mSearchTerm);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.search_menu, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        final SearchView searchView = (SearchView) searchItem.getActionView();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchView.clearFocus();

                new SearchContactAsyncTask().execute(query);

                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
//                mSearchContactAsyncTask.execute(newText);
                mSearchTerm = newText;
                return true;
            }
        });

        if(mSearchTerm != null){
            searchView.setQuery(mSearchTerm, true);
        }

        super.onCreateOptionsMenu(menu, inflater);
    }

    /**
     * Task responsible of searching a contact
     */
    public class SearchContactAsyncTask extends AsyncTask<String, Void, List<? extends Contact>> {
        @Override
        protected List<? extends Contact> doInBackground(String... terms) {

            if(terms.length == 0){
                return mAppDatabase.contactDao().getAll();
            }

            if(terms[0].isEmpty()){
                return mAppDatabase.contactDao().getAll();
            }

            Log.d(SearchContactAsyncTask.class.getName(), "Search term: " + terms[0]);
            return mAppDatabase.contactDao().search(terms[0]);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mProgressBar.setVisibility(View.VISIBLE);
            mRecyclerView.setVisibility(View.GONE);
        }

        @Override
        protected void onPostExecute(List<? extends Contact> contacts) {
            super.onPostExecute(contacts);

            Log.d(SearchContactAsyncTask.class.getName(), "Contacts length: " + contacts.size());

            mAdapter.setContacts(contacts);
            mProgressBar.setVisibility(View.GONE);
            mRecyclerView.setVisibility(View.VISIBLE);
        }
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnListFragmentInteractionListener {
        // TODO: Update argument type and name
        void onListFragmentInteraction(Contact item);
    }
}
