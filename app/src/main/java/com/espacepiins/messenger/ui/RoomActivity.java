package com.espacepiins.messenger.ui;

import android.Manifest;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.espacepiins.messenger.R;
import com.espacepiins.messenger.model.Contact;
import com.espacepiins.messenger.model.Room;
import com.espacepiins.messenger.service.ContactImportService;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;

import butterknife.BindBool;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class RoomActivity extends AppCompatActivity implements OnMapReadyCallback, RoomListFragment.OnRoomInteractionListener {
    private final String TAG = RoomActivity.class.getName();
    private static final int READ_CONTACTS_REQUEST_CODE = 0x001;
    private static final int REQUEST_CONTACT_NEW_CONVERSATION_CODE = 0x002;

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

    SupportMapFragment mMapFragment;
    Fragment mRoomList;

    GoogleMap mMap;

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

        if(!hasPermission(Manifest.permission.READ_CONTACTS)){
            requestContactPermission();
        }

//        ContactImportService.startActionContactImport(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search_menu, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));

        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == REQUEST_CONTACT_NEW_CONVERSATION_CODE){
            if(resultCode == RESULT_OK){
                int action = data.getIntExtra("action", 0);
                Contact selectedContact = data.getParcelableExtra("contact");
                switch (action){
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

    public void inviteContact(Contact contact){
        Log.i(TAG, "inviteContact() " + contact);
    }

    public void createConversation(Contact contact){
        Log.i(TAG, "createConversation() " + contact);
    }

    @Override
    public void onMapReady(final GoogleMap googleMap) {
        mMap = googleMap;
    }


    @OnClick(R.id.profife_avatar_button)
    public void onProfileAvatarImageButtonClick(View imageButton){
        Log.d(TAG, "onProfileAvatarImageButtonClick clicked!");
    }

    @OnClick(R.id.floatingActionButton)
    public void onFloatingActionButtonClick(View button){
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

        if(checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED){
            return true;
        }

        return false;
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
    public void onRoomSelected(Room room) {
        Toast.makeText(this, "Implementation dans le livrable 3!!!", Toast.LENGTH_LONG)
                .show();
    }

    public class ViewPagerAdapter extends FragmentPagerAdapter {
        final int ROOM_LIST_FRAGMENT = 0;
        final int ROOM_LIST_MAP_FRAGMENT = 1;
        private FragmentManager mFragmentManager;

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
