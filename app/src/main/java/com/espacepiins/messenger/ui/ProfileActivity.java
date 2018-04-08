package com.espacepiins.messenger.ui;

import android.arch.lifecycle.ViewModelProviders;
import android.databinding.BindingMethod;
import android.databinding.BindingMethods;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.espacepiins.messenger.application.GlideApp;
import com.espacepiins.messenger.model.Profile;
import com.espacepiins.messenger.ui.viewmodel.ProfileViewModel;
import com.espacepiins.messsenger.R;
import com.espacepiins.messsenger.databinding.ActivityProfileBinding;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Wilfrield on 2018-03-06.
 */
@BindingMethods({
        @BindingMethod(type = android.support.design.widget.FloatingActionButton.class,
                attribute = "app:srcCompat",
                method = "setImageDrawable"),
        @BindingMethod(type = android.support.design.widget.FloatingActionButton.class,
                attribute = "app:backgroundTint",
                method = "setBackgroundTintList")})
public class ProfileActivity extends AppCompatActivity {
    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.avatarImageView)
    ImageView mAvatarImageView;
    private ActivityProfileBinding mActivityProfileBinding;
    private ProfileViewModel mProfileViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mActivityProfileBinding = DataBindingUtil.setContentView(this, R.layout.activity_profile);

        ButterKnife.bind(this, mActivityProfileBinding.getRoot());
        mProfileViewModel = ViewModelProviders.of(this).get(ProfileViewModel.class);

        mProfileViewModel.getProfileData().observe(this, this::updateUI);

        mActivityProfileBinding.setIsEditing(false);

        setSupportActionBar(mToolbar);
        ActionBar actionBar = getSupportActionBar();

        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("Profile");
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

    @OnClick(R.id.editButton)
    public void onEditClicked(View view) {
        mActivityProfileBinding.setIsEditing(!mActivityProfileBinding.getIsEditing());
        mActivityProfileBinding.editTextDisplayname.requestFocus();
//        FloatingActionButton actionButton = (FloatingActionButton) view;
        if (!mActivityProfileBinding.getIsEditing()) {
//            actionButton.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorContact)));
            mProfileViewModel.save(mActivityProfileBinding.getProfile());
        } else {
//            actionButton.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorEditingMode)));
        }
    }

    private void updateUI(final Profile profile) {
        mActivityProfileBinding.setProfile(profile);
        GlideApp.with(this)
                .load(profile.getAvatarUrl())
                .fallback(R.drawable.ic_face)
                .centerCrop()
                .circleCrop()
                .into(mAvatarImageView);

    }
}
