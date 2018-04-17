package com.espacepiins.messenger.ui;

import android.Manifest;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.espacepiins.messenger.job.UserFetcherJob;
import com.espacepiins.messenger.model.Profile;
import com.espacepiins.messenger.model.Room;
import com.espacepiins.messenger.service.ContactImportReceiver;
import com.espacepiins.messenger.service.ContactImportService;
import com.espacepiins.messenger.ui.viewmodel.AppViewModel;
import com.espacepiins.messenger.ui.viewmodel.ProfileViewModel;
import com.espacepiins.messenger.ui.viewmodel.RoomListViewModel;
import com.espacepiins.messsenger.R;
import com.espacepiins.messsenger.databinding.ActivityRoomBinding;
import com.firebase.jobdispatcher.FirebaseJobDispatcher;

import butterknife.BindBool;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class RoomActivity extends FirebaseAuthAwareActivity implements RoomListFragment.OnRoomInteractionListener {
    private final String TAG = RoomActivity.class.getName();
    private static final int SETUP_READ_CONTACTS_REQUEST_CODE = 0x001;
    private static final int NEW_CONVERSATION_READ_CONTACTS_REQUEST_CODE = 0x002;
    private static final int REQUEST_CONTACT_NEW_CONVERSATION_CODE = 0x002;
    private AppViewModel mAppViewModel;
    private RoomListViewModel mRoomListViewModel;
    private Profile mProfile;

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

    private ContactImportReceiver mContactImportReceiver;
    private FriendsMapFragment mMapFragment;
    private Fragment mRoomList;

    private FirebaseJobDispatcher mFirebaseJobDispatcher;
    private ActivityRoomBinding mActivityRoomBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room);

        mActivityRoomBinding = DataBindingUtil.setContentView(this, R.layout.activity_room);

        ButterKnife.bind(this, mActivityRoomBinding.getRoot());

        setSupportActionBar(mToolbar);

        final ActionBar actionBar = getSupportActionBar();

        actionBar.setDisplayHomeAsUpEnabled(false);
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayShowCustomEnabled(true);

        mMapFragment = new FriendsMapFragment();
        mRoomList = new RoomListFragment();

        mViewPager.setAdapter(new ViewPagerAdapter(getSupportFragmentManager()));
        mTabLayout.setupWithViewPager(mViewPager);
        mContactImportReceiver = new ContactImportReceiver();

        initViewModels();

        mFirebaseJobDispatcher = UserFetcherJob.createJob(this);
    }

    private void initViewModels() {
        mAppViewModel = ViewModelProviders.of(this).get(AppViewModel.class);
        mRoomListViewModel = ViewModelProviders.of(this).get(RoomListViewModel.class);

        final ProfileViewModel profileViewModel = ViewModelProviders.of(this).get(ProfileViewModel.class);
        profileViewModel.getProfileData().observe(this, new Observer<Profile>() {
            @Override
            public void onChanged(@Nullable Profile profile) {
                mProfile = profile;
                mActivityRoomBinding.setProfile(profile);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(mContactImportReceiver != null){
            IntentFilter filter = new IntentFilter(ContactImportReceiver.CONTACT_IMPORTED_ACTION);
            this.registerReceiver(mContactImportReceiver, filter);
            Log.d(TAG, "onResume() - ContactImportReceiver registered!");
        }

//        if(mQuery != null){
//            initRoomsListener();
//        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        if(mContactImportReceiver != null){
            this.unregisterReceiver(mContactImportReceiver);
            Log.d(TAG, "onPause() - ContactImportReceiver unregistered!");
        }

//        if(mQuery != null){
//            mQuery.removeEventListener(mRoomsListQueryListener);
//        }

    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mFirebaseJobDispatcher == null) {
            mFirebaseJobDispatcher = UserFetcherJob.createJob(this);
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();

        if (mFirebaseJobDispatcher == null) {
            mFirebaseJobDispatcher = UserFetcherJob.createJob(this);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mFirebaseJobDispatcher != null) {
            mFirebaseJobDispatcher.cancel(UserFetcherJob.JOB_TAG);
        }
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

                switch (action) {
                    case ContactActivity.CONTACT_SELECTED:
                        String firebaseUID = data.getStringExtra(ContactActivity.EXTRA_FIREBASE_UID);
                        // 1: Create a new room
                        createConversation(firebaseUID);
                        break;
                    case ContactActivity.INVITE_CONTACT:
                        String contactEmail = data.getStringExtra(ContactActivity.EXTRA_EMAIL);
                        String contactDisplayname = data.getStringExtra("displayName");
                        // 1: Invite contact to download the application
                        inviteContact(contactDisplayname, contactEmail);
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

    private void inviteContact(String contactDisplayname, String contactEmail) {
        Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts("mailto", contactEmail, null));
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Viens discuter su PiinsMessenger!");
        emailIntent.putExtra(Intent.EXTRA_TEXT, "%s vous invites à rejoindre PiinsMessenger\nCopiez ce lien dans votre navigateur pour installer l'application %s");
        emailIntent.putExtra(Intent.EXTRA_HTML_TEXT, Html.fromHtml(new StringBuilder()
                .append("<p><b>").append(contactDisplayname).append("</b>").append(" vous invites à rejoindre PiinsMessenger.").append("</p>")
                .append("<p>").append("Cliquez sur ce lien pour installer l'application")
                .append("<a>").append(getString(R.string.internal_test_dynamic_link)).append("</a>")
                .append("</p>")
                .toString()));

        startActivity(Intent.createChooser(emailIntent, "Envoyer une invitation"));
    }

    public void createConversation(String contactUID) {
        mRoomListViewModel.createRoom(mProfile, contactUID, new RoomListViewModel.OnRoomCreated() {
            @Override
            public void onSuccess(String roomKey) {

            }

            @Override
            public void onFailure(Exception exception) {
                Log.w(TAG, "createConversation() onFailure: " + exception.getMessage(), exception);
            }
        });

    }

    @OnClick(R.id.searchView)
    public void onSearchViewClick(View view){
        Log.d(TAG, "onSearchViewClick clicked!");
        Intent intent = new Intent(this, ContactSearchResultsActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.profife_avatar_button)
    public void onProfileAvatarImageButtonClick(View imageButton) {
        Intent intent = new Intent(this, ProfileActivity.class);
        startActivity(intent);
        Log.d(TAG, "onProfileAvatarImageButtonClick clicked!");
    }

    @OnClick(R.id.floatingActionButton)
    public void onFloatingActionButtonClick(View button) {
        Log.d(TAG, "onFloatingActionButtonClick clicked!");
        requestContactPermission(NEW_CONVERSATION_READ_CONTACTS_REQUEST_CODE);
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

    private void requestContactPermission(int requestCode) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if(shouldShowRequestPermissionRationale(Manifest.permission.READ_CONTACTS))
                    Toast.makeText(this, R.string.read_contacts_permission_rational_message_hint, Toast.LENGTH_LONG)
                            .show();

            requestPermissions(new String[]{Manifest.permission.READ_CONTACTS}, requestCode);
            return;
        }

        dispatchActionForPermissionRequestCode(requestCode);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == SETUP_READ_CONTACTS_REQUEST_CODE || requestCode == NEW_CONVERSATION_READ_CONTACTS_REQUEST_CODE) {
            if(grantResults.length > 0){
                if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    dispatchActionForPermissionRequestCode(requestCode);
                }
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    private void dispatchActionForPermissionRequestCode(int requestCode) {
        ContactImportService.startActionContactImport(this);
        switch (requestCode) {
            case NEW_CONVERSATION_READ_CONTACTS_REQUEST_CODE:
                newConversation();
                break;
            default:
                break;
        }
    }

    @Override
    public void onRoomSelected(Room room) {
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
}
