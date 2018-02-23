package com.espacepiins.messenger.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageButton;

import com.espacepiins.messsenger.R;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RoomActivity extends AppCompatActivity implements OnMapReadyCallback{
    @BindView(R.id.pager)
    ViewPager mViewPager;

    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    @BindView(R.id.profife_avatar_button)
    ImageButton mProfileAvatarImageButton;

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
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.room_activity_menu, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) searchItem.getActionView();
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
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
