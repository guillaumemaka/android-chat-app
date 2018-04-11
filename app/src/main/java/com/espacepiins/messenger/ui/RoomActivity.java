package com.espacepiins.messenger.ui;

import android.Manifest;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.espacepiins.messenger.R;
import com.espacepiins.messenger.db.AppDatabase;
import com.espacepiins.messenger.db.entity.RoomEntity;
import com.espacepiins.messenger.model.Contact;
import com.espacepiins.messenger.model.Room;
import com.espacepiins.messenger.service.ContactImportReceiver;
import com.espacepiins.messenger.service.ContactImportService;
import com.espacepiins.messenger.ui.viewmodel.AppViewModel;
import com.espacepiins.messenger.ui.viewmodel.RoomListViewModel;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import butterknife.BindBool;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class RoomActivity extends FirebaseAuthAwareActivity implements OnMapReadyCallback, RoomListFragment.OnRoomInteractionListener {
    private final String TAG = RoomActivity.class.getName();
    private static final int READ_CONTACTS_REQUEST_CODE = 0x001;
    private static final int REQUEST_CONTACT_NEW_CONVERSATION_CODE = 0x002;
    private AppViewModel mAppViewModel;
    private RoomListViewModel mRoomListViewModel;
    private RoomsListQueryListener mRoomsListQueryListener;

    @BindView(R.id.pager)
    ViewPager mViewPager;

    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    @BindView(R.id.tablayout)
    TabLayout mTabLayout;

    @BindView(R.id.profife_avatar_button)
    ImageButton mProfileAvatarImageButton;

    @BindBool(R.bool.isTwoPane)
    boolean isTwoPane;

    @BindView(R.id.searchView)
    TextView mSearchView;

    ContactImportReceiver mContactImportReceiver;
    SupportMapFragment mMapFragment;
    Fragment mRoomList;


    GoogleMap mMap;
    private Query mQuery;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room);
        ButterKnife.bind(this);

        setSupportActionBar(mToolbar);

        final ActionBar actionBar = getSupportActionBar();

        actionBar.setDisplayHomeAsUpEnabled(false);
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayShowCustomEnabled(true);

        mMapFragment = new SupportMapFragment();
        mRoomList = new RoomListFragment();

        mMapFragment.getMapAsync(this);
        mViewPager.setAdapter(new ViewPagerAdapter(getSupportFragmentManager()));
        mTabLayout.setupWithViewPager(mViewPager);
        mContactImportReceiver = new ContactImportReceiver();

        if (!hasPermission(Manifest.permission.READ_CONTACTS)) {
            requestContactPermission();
        }

        initViewModels();
    }

    private void initViewModels() {
        mAppViewModel = ViewModelProviders.of(this).get(AppViewModel.class);
        mRoomListViewModel = ViewModelProviders.of(this).get(RoomListViewModel.class);
        mAppViewModel.isContactLoaded().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean loaded) {
                if(loaded){
                    Snackbar.make(mViewPager,
                            R.string.snackbar_contact_import_done_message,
                            Snackbar.LENGTH_SHORT
                    ).show();

                    Log.i(TAG, "Contacts imported !!!");
                }

            }
        });


        if(mAppViewModel.isContactLoaded().getValue() == false){
            final Snackbar snackbar = Snackbar.make(mViewPager,
                    R.string.snackbar_contact_import_messasge,
                        Snackbar.LENGTH_INDEFINITE
                    );
            snackbar.setAction(R.string.string_snackbar_import_action, new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ContactImportService.startActionContactImport(RoomActivity.this);
                    snackbar.dismiss();
                }
            });
        }
    }

    private void initRoomsListener(){
        mQuery = FirebaseDatabase.getInstance().getReference(String.format("users/%s/rooms", FirebaseAuth.getInstance().getCurrentUser().getUid()));
        mRoomsListQueryListener = new RoomsListQueryListener();
    }

    @Override
    protected void onResume() {
        if(mContactImportReceiver != null){
            IntentFilter filter = new IntentFilter(ContactImportReceiver.CONTACT_IMPORTED_ACTION);
            this.registerReceiver(mContactImportReceiver, filter);
            Log.d(TAG, "onResume() - ContactImportReceiver registered!");
        }

//        if(mQuery != null){
//            initRoomsListener();
//        }

        super.onResume();
    }

    @Override
    protected void onPause() {
        if(mContactImportReceiver != null){
            this.unregisterReceiver(mContactImportReceiver);
            Log.d(TAG, "onPause() - ContactImportReceiver unregistered!");
        }

//        if(mQuery != null){
//            mQuery.removeEventListener(mRoomsListQueryListener);
//        }

        super.onPause();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.search_menu, menu);
//
//        MenuItem searchItem = menu.findItem(R.id.action_search);
//        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
//        SearchView searchView = (SearchView) searchItem.getActionView();
//        searchView.setIconifiedByDefault(false);
//        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));

        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CONTACT_NEW_CONVERSATION_CODE) {
            if (resultCode == RESULT_OK) {
                int action = data.getIntExtra("action", 0);
                Contact selectedContact = data.getParcelableExtra("contact");
                switch (action) {
                    case ContactActivity.CONTACT_SELECTED:
                        // 1: Create a new room
                        createConversation(selectedContact);
                        break;
                    case ContactActivity.INVITE_CONTACT:
                        // 1: Invite contact to download the application
                        inviteContact(selectedContact);
                        break;
                    case ContactActivity.NO_CONTACT_SELECTED:
                        break;
                    default:
                        break;
                }
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    public void inviteContact(Contact contact) {
        Log.i(TAG, "inviteContact() " + contact);
    }

    public void createConversation(Contact contact) {
        String roomKey = mRoomListViewModel.createRoom(FirebaseAuth.getInstance().getCurrentUser().getUid(), contact.getFirebaseUID());
        Log.i(TAG, "createConversation() " + contact);
    }

    @Override
    public void onMapReady(final GoogleMap googleMap) {
        mMap = googleMap;
    }


    @OnClick(R.id.searchView)
    public void onSearchViewClick(View view){
        Log.d(TAG, "onSearchViewClick clicked!");
        Intent intent = new Intent(this, ContactSearchResultsActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.profife_avatar_button)
    public void onProfileAvatarImageButtonClick(View imageButton) {
        Log.d(TAG, "onProfileAvatarImageButtonClick clicked!");
    }

    @OnClick(R.id.floatingActionButton)
    public void onFloatingActionButtonClick(View button) {
        Log.d(TAG, "onFloatingActionButtonClick clicked!");
        newConversation();
    }

    private void newConversation(){
        Intent contactIntent = new Intent(this, ContactActivity.class);
        startActivityForResult(contactIntent, REQUEST_CONTACT_NEW_CONVERSATION_CODE);
    }

    public boolean hasPermission(String permission){
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.M){
            return true;
        }

        return checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestContactPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if(shouldShowRequestPermissionRationale(Manifest.permission.READ_CONTACTS))
                    Toast.makeText(this, R.string.read_contacts_permission_rational_message_hint, Toast.LENGTH_LONG)
                            .show();

            requestPermissions(new String[]{Manifest.permission.READ_CONTACTS}, READ_CONTACTS_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == READ_CONTACTS_REQUEST_CODE){
            if(grantResults.length > 0){
                if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    ContactImportService.startActionContactImport(this);
                }
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    public void onRoomSelected(RoomEntity room) {
        Toast.makeText(this, "Implementation dans le livrable 3!!!", Toast.LENGTH_LONG)
                .show();
    }

    public class ViewPagerAdapter extends FragmentPagerAdapter {
        final int ROOM_LIST_FRAGMENT = 0;
        final int ROOM_LIST_MAP_FRAGMENT = 1;

        public ViewPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position){
                case ROOM_LIST_FRAGMENT:
                    return mRoomList;
                case ROOM_LIST_MAP_FRAGMENT:
//                    SupportMapFragment mapFragment = new SupportMapFragment();
//                    mapFragment.getMapAsync(RoomActivity.this);
                    return mMapFragment;
            }
            return null;
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position){
                case ROOM_LIST_FRAGMENT:
                    return getString(R.string.tablayout_tab_recent_title);
                case ROOM_LIST_MAP_FRAGMENT:
                    return getString(R.string.tablayout_tab_discover_title);
            }
            return "";
        }
    }

    public class RoomsListQueryListener implements ChildEventListener{
        @Override
        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
            final Room room = dataSnapshot.getValue(Room.class);
            final RoomEntity roomEntity = RoomEntity.fromFirebaseObject(room);
            new AsyncTask<RoomEntity, Void, Void>(){
                @Override
                protected Void doInBackground(RoomEntity... roomEntities) {
                    AppDatabase.getInstance(getApplication())
                            .roomDao().insert(roomEntities[0]);
                    return null;
                }
            }.execute(roomEntity);
        }

        @Override
        public void onChildChanged(DataSnapshot dataSnapshot, String s) {
            final Room room = dataSnapshot.getValue(Room.class);
            final RoomEntity roomEntity = RoomEntity.fromFirebaseObject(room);
            new AsyncTask<RoomEntity, Void, Void>(){
                @Override
                protected Void doInBackground(RoomEntity... roomEntities) {
                    AppDatabase.getInstance(getApplication())
                            .roomDao().update(roomEntities[0]);
                    return null;
                }
            }.execute(roomEntity);
        }

        @Override
        public void onChildRemoved(DataSnapshot dataSnapshot) {
            final Room room = dataSnapshot.getValue(Room.class);
            new AsyncTask<String, Void, Void>(){
                @Override
                protected Void doInBackground(String... roomEntities) {
                    AppDatabase.getInstance(getApplication())
                            .roomDao().delete(roomEntities[0]);
                    return null;
                }
            }.execute(room.getRoomUID());
        }

        @Override
        public void onChildMoved(DataSnapshot dataSnapshot, String s) {
            Log.d(TAG, "onChildMoved: " + dataSnapshot.getKey());
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {
            Log.w(TAG, "onCancelled", databaseError.toException());
        }
    }
}
