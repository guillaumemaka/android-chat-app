package com.espacepiins.messenger.ui;

import android.Manifest;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.espacepiins.messenger.R;
import com.espacepiins.messenger.application.Constants;
import com.espacepiins.messenger.application.FirebaseRefs;
import com.espacepiins.messenger.databinding.ActivityRoomBinding;
import com.espacepiins.messenger.job.UserFetcherJob;
import com.espacepiins.messenger.model.Profile;
import com.espacepiins.messenger.model.Room;
import com.espacepiins.messenger.service.ContactImportReceiver;
import com.espacepiins.messenger.service.ContactImportService;
import com.espacepiins.messenger.ui.viewmodel.AppViewModel;
import com.espacepiins.messenger.ui.viewmodel.ProfileViewModel;
import com.espacepiins.messenger.ui.viewmodel.RoomListViewModel;
import com.espacepiins.messenger.util.FirebaseUtil;
import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.google.firebase.database.FirebaseDatabase;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class RoomActivity extends FirebaseAuthAwareActivity implements RoomListFragment.OnRoomInteractionListener {
    /**
     * TAT for logcat filtering
     */
    private final String TAG = RoomActivity.class.getName();

    private static final int NEW_CONVERSATION_PERMISSION_REQUEST_CODE = 0x002;
    private static final int REQUEST_CONTACT_NEW_CONVERSATION_CODE = 0x002;

    /**
     * A View Model holding some global app settings/variables
     */
    private AppViewModel mAppViewModel;

    /**
     * A view model holding a list of room for the current user and actions.
     */
    private RoomListViewModel mRoomListViewModel;

    /**
     * The current logged in user profile.
     */
    private Profile mProfile;

    /**
     * A receiver handling response from the ContactImportService,
     * called when the service successfully/failed to import contacts
     */
    private ContactImportReceiver mContactImportReceiver;

    /**
     * The Map fragment.
     */
    private FriendsMapFragment mMapFragment;

    /**
     * The room list fragment
     */
    private Fragment mRoomList;

    /**
     * The job dispatcher
     */
    private FirebaseJobDispatcher mFirebaseJobDispatcher;

    /**
     * DataBinding for this activity
     */
    private ActivityRoomBinding mActivityRoomBinding;

    // START ui binding declaration

    @BindView(R.id.pager)
    ViewPager mViewPager;

    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    @BindView(R.id.tablayout)
    TabLayout mTabLayout;

    @BindView(R.id.profife_avatar_button)
    ImageButton mProfileAvatarImageButton;

    // END ui binding declaration

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room);

        mActivityRoomBinding = DataBindingUtil.setContentView(this, R.layout.activity_room);

        ButterKnife.bind(this, mActivityRoomBinding.getRoot());

        setSupportActionBar(mToolbar);

        final ActionBar actionBar = getSupportActionBar();

        actionBar.setDisplayHomeAsUpEnabled(false);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setTitle(R.string.app_name);

        mMapFragment = new FriendsMapFragment();
        mRoomList = new RoomListFragment();

        mViewPager.setAdapter(new ViewPagerAdapter(getSupportFragmentManager()));
        mTabLayout.setupWithViewPager(mViewPager);
        mContactImportReceiver = new ContactImportReceiver();

//        FirebaseUtil.setConnected(true);
        FirebaseUtil.registerFcmIfNeeded();
        initViewModels();
        initJobs();
    }


    /**
     * Initialize the required view models used in this activity
     */
    private void initViewModels() {
        mAppViewModel = ViewModelProviders.of(this).get(AppViewModel.class);
        mRoomListViewModel = ViewModelProviders.of(this).get(RoomListViewModel.class);

        final ProfileViewModel profileViewModel = ViewModelProviders.of(this).get(ProfileViewModel.class);
        profileViewModel.getProfileData(mCurrentUser.getUid()).observe(this, profile -> {
            mProfile = profile;
            mActivityRoomBinding.setProfile(profile);
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
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(mContactImportReceiver != null){
            this.unregisterReceiver(mContactImportReceiver);
            Log.d(TAG, "onPause() - ContactImportReceiver unregistered!");
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        initJobs();
    }

    private void initJobs() {
        if (mFirebaseJobDispatcher == null) {
            mFirebaseJobDispatcher = UserFetcherJob.createJob(this);
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        initJobs();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mFirebaseJobDispatcher != null) {
            mFirebaseJobDispatcher.cancel(UserFetcherJob.JOB_TAG);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CONTACT_NEW_CONVERSATION_CODE) {
            if (resultCode == RESULT_OK) {
                int action = data.getIntExtra("action", 0);

                switch (action) {
                    // Selecting a contact using the app
                    case ContactActivity.CONTACT_SELECTED:
                        String firebaseUID = data.getStringExtra(ContactActivity.EXTRA_FIREBASE_UID);
                        // 1: Create a new room
                        createConversation(firebaseUID);
                        break;
                    // Contact not using the app, send invite.
                    case ContactActivity.INVITE_CONTACT:
                        String contactEmail = data.getStringExtra(ContactActivity.EXTRA_EMAIL);
                        String contactDisplayname = data.getStringExtra(ContactActivity.EXTRA_DISPLAY_NAME);
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
    }

    /**
     * Send invitation via email to a contact
     *
     * @param contactDisplayname the contact display name.
     * @param contactEmail       the contact email address
     */
    private void inviteContact(String contactDisplayname, String contactEmail) {
        Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts("mailto", contactEmail, null));
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Viens discuter su PiinsMessenger!");
        emailIntent.putExtra(Intent.EXTRA_TEXT, String.format("%s vous invites à rejoindre PiinsMessenger\nCopiez ce lien dans votre navigateur pour installer l'application %s", contactDisplayname, getString(R.string.app_name)));
        emailIntent.putExtra(Intent.EXTRA_HTML_TEXT, Html.fromHtml(new StringBuilder()
                .append("<p><b>").append(contactDisplayname).append("</b>").append(" vous invites à rejoindre ").append(getString(R.string.app_name)).append(".</p>")
                .append("<p>").append("Cliquez sur ce lien pour installer l'application")
                .append("<a>").append(getString(R.string.internal_test_dynamic_link)).append("</a>")
                .append("</p>")
                .toString()));

        startActivity(Intent.createChooser(emailIntent, "Envoyer une invitation"));
    }


    /**
     * Create a new room and start the chat activity
     * @param contactUID
     */
    public void createConversation(String contactUID) {
        mRoomListViewModel.createOrRetrieveRoom(mProfile, contactUID, new RoomListViewModel.OnRoomCreated() {
            @Override
            public void onSuccess(String roomKey) {
                startChatActivity(roomKey);
            }

            @Override
            public void onFailure(Exception exception) {
                Log.w(TAG, "createConversation() onFailure: " + exception.getMessage(), exception);
            }
        });

    }

    protected void startChatActivity(String roomKey) {
        Intent chatIntent = new Intent(RoomActivity.this, ChatActivity.class);
        chatIntent.putExtra(ChatActivity.EXTRA_ROOM_ID, roomKey);
        startActivity(chatIntent);
    }

    /**
     * Show the profile activity
     * @param imageButton
     */
    @OnClick(R.id.profife_avatar_button)
    public void onProfileAvatarImageButtonClick(View imageButton) {
        Intent intent = new Intent(this, ProfileActivity.class);
        intent.putExtra(ProfileActivity.EXTRA_READ_ONLY, false);
        intent.putExtra(ProfileActivity.EXTRA_DISABLE_SIGNOUT, false);
        intent.putExtra(ProfileActivity.EXTRA_USER_PROFILE_ID, mCurrentUser.getUid());
        startActivity(intent);
        Log.d(TAG, "onProfileAvatarImageButtonClick clicked!");
    }

    /**
     * Show the contact activity
     * @param button
     */
    @OnClick(R.id.floatingActionButton)
    public void onFloatingActionButtonClick(View button) {
        Log.d(TAG, "onFloatingActionButtonClick clicked!");
        requestContactPermission(NEW_CONVERSATION_PERMISSION_REQUEST_CODE);
    }

    /**
     * Start the contact activity
     */
    private void startContactActivity(){
        Intent contactIntent = new Intent(this, ContactActivity.class);
        startActivityForResult(contactIntent, REQUEST_CONTACT_NEW_CONVERSATION_CODE);
    }

    /**
     * Helper for check a permission status.
     * @param permission the permission to check
     * @return true if granted otherwise false
     */
    public boolean hasPermission(String permission){
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.M){
            return true;
        }

        return checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestContactPermission(int requestCode) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!hasPermission(Manifest.permission.READ_CONTACTS)) {
                if (shouldShowRequestPermissionRationale(Manifest.permission.READ_CONTACTS))
                    Toast.makeText(this, R.string.read_contacts_permission_rational_message_hint, Toast.LENGTH_LONG)
                            .show();

                requestPermissions(new String[]{Manifest.permission.READ_CONTACTS}, requestCode);
                return;
            }
        }

        dispatchActionForPermissionRequestCode(requestCode);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == NEW_CONVERSATION_PERMISSION_REQUEST_CODE) {
            if(grantResults.length > 0){
                if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    dispatchActionForPermissionRequestCode(requestCode);
                }
            }
        }
    }

    /**
     * Helper method for dispatch action for permission request code.
     * @param requestCode a permission request code.
     */
    private void dispatchActionForPermissionRequestCode(int requestCode) {
        switch (requestCode) {
            case NEW_CONVERSATION_PERMISSION_REQUEST_CODE:
                SharedPreferences prefs = getSharedPreferences(getString(R.string.preference_key), MODE_PRIVATE);
                long lastImportedTime = prefs.getLong(Constants.LAST_CONTACT_IMPORTED, 0);
                if (lastImportedTime == 0) {
                    ContactImportService.startActionContactImport(this);
                }

                startContactActivity();
                break;
            default:
                break;
        }
    }

    @Override
    public void onRoomSelected(Room room) {
        if (!room.isRead()) {
            FirebaseDatabase
                    .getInstance()
                    .getReference(FirebaseRefs.USER_ROOMS_REF(mCurrentUser.getUid()) + "/" + room.getRoomUID())
                    .child("read").setValue(true);
        }
        startChatActivity(room.getRoomUID());
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
