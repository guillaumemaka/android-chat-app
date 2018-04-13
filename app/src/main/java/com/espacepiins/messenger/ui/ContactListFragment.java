package com.espacepiins.messenger.ui;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.espacepiins.messenger.model.SearchContactResult;
import com.espacepiins.messenger.ui.viewmodel.ContactSearchViewModel;
import com.espacepiins.messsenger.R;

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

    private final String TAG = ContactListFragment.class.getName();
    private ContactAdapter mAdapter;
    private String mSearchTerm;
    private ContactSearchViewModel mContactSearchViewModel;

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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_contact_list, container, false);

        ButterKnife.bind(this, view);

        mProgressBar.setVisibility(View.GONE);

        mAdapter = new ContactAdapter();
        mAdapter.setListener(mListener);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.setAdapter(mAdapter);

        mContactSearchViewModel.getContactSearchResults().observe(this, new Observer<List<SearchContactResult>>() {
            @Override
            public void onChanged(@Nullable List<SearchContactResult> searchContactResults) {
                mProgressBar.setVisibility(View.GONE);
                mRecyclerView.setVisibility(View.VISIBLE);
                mAdapter.setContacts(searchContactResults);
            }
        });

        mContactSearchViewModel.search("");

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

        mContactSearchViewModel = ViewModelProviders.of(this).get(ContactSearchViewModel.class);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if(savedInstanceState != null){
            mSearchTerm = savedInstanceState.getString(SEARCH_TERM_KEY, "");
            mContactSearchViewModel.search(mSearchTerm);
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(SEARCH_TERM_KEY, mSearchTerm);
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
                mProgressBar.setVisibility(View.VISIBLE);
                mContactSearchViewModel.search(query);

                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
//                mSearchContactAsyncTask.execute(newText);
                mSearchTerm = newText;
                if(newText.isEmpty()){
                    mContactSearchViewModel.search("");
                }
                return true;
            }
        });

        if(mSearchTerm != null){
            searchView.setQuery(mSearchTerm, true);
        }

        super.onCreateOptionsMenu(menu, inflater);
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
        void onListFragmentInteraction(SearchContactResult item);
    }
}
