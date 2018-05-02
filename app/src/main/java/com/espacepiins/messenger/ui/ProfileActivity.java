package com.espacepiins.messenger.ui;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.databinding.BindingMethod;
import android.databinding.BindingMethods;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.NavUtils;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.espacepiins.messenger.R;
import com.espacepiins.messenger.databinding.ActivityProfileBinding;
import com.espacepiins.messenger.model.Profile;
import com.espacepiins.messenger.ui.viewmodel.ProfileViewModel;
import com.espacepiins.messenger.util.FirebaseUtil;
import com.google.firebase.auth.FirebaseAuth;
import com.vansuita.pickimage.bean.PickResult;
import com.vansuita.pickimage.dialog.PickImageDialog;
import com.vansuita.pickimage.listeners.IPickResult;
import com.yalantis.ucrop.UCrop;

import java.io.File;
import java.util.Arrays;

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
public class ProfileActivity extends FirebaseAuthAwareActivity implements IPickResult {
    private static final String TAG = ProfileActivity.class.getName();
    public static final String EXTRA_DISABLE_SIGNOUT = "disable_signout";
    public static final String EXTRA_USER_PROFILE_ID = "user_profile_id";
    public static final String EXTRA_READ_ONLY = "read_only";

    private ActivityProfileBinding mActivityProfileBinding;
    private ProfileViewModel mProfileViewModel;
    private Uri mAvatarUri;
    private String mUserProfileId;
    private boolean mDisableSignout;
    private boolean mReadOnly;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mActivityProfileBinding = DataBindingUtil.setContentView(this, R.layout.activity_profile);

        onNewIntent(getIntent());

        ButterKnife.bind(this, mActivityProfileBinding.getRoot());

        mProfileViewModel = ViewModelProviders.of(this).get(ProfileViewModel.class);

        mProfileViewModel.getProfileData(mUserProfileId).observe(this, this::updateUI);

        mActivityProfileBinding.setIsEditing(false);

        setSupportActionBar(mActivityProfileBinding.toolbar);
        ActionBar actionBar = getSupportActionBar();

        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.ic_cancel);
        actionBar.setTitle(R.string.activity_profile_title);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        mUserProfileId = intent.getStringExtra(EXTRA_USER_PROFILE_ID);
        mDisableSignout = intent.getBooleanExtra(EXTRA_DISABLE_SIGNOUT, false);
        mReadOnly = intent.getBooleanExtra(EXTRA_READ_ONLY, false);
        mActivityProfileBinding.setReadOnly(mReadOnly);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.profile_activity_menu, menu);
        if (mDisableSignout) {
            MenuItem signoutItem = menu.findItem(R.id.action_signout);
            signoutItem.setEnabled(false);
            signoutItem.setVisible(false);
        }

        if (!mCurrentUser.getUid().equals(mUserProfileId)) {
            MenuItem settingsItem = menu.findItem(R.id.action_settings);
            settingsItem.setEnabled(false);
            settingsItem.setVisible(false);
        }

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                Intent upIntent = NavUtils.getParentActivityIntent(this);
                if (NavUtils.shouldUpRecreateTask(this, upIntent)) {
                    // This activity is NOT part of this app's task, so create a new task
                    // when navigating up, with a synthesized back stack.
                    TaskStackBuilder.create(this)
                            // Add all of this activity's parents to the back stack
                            .addNextIntentWithParentStack(upIntent)
                            // Navigate up to the closest parent
                            .startActivities();
                } else {
                    // This activity is part of this app's task, so simply
                    // navigate up to the logical parent activity.
                    NavUtils.navigateUpFromSameTask(this);
                }
                return true;
            case R.id.action_signout:
                FirebaseUtil.setConnected(false);
                FirebaseAuth.getInstance().signOut();
                Log.d(TAG, "Signout called!");
                return false;
            case R.id.action_settings:
                startActivitySetting();
        }
        return super.onOptionsItemSelected(item);
    }

    private void startActivitySetting() {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.uploadAvatar)
    public void onProfileAvatarButtonClick(View view) {
        pickImage();
    }

    @OnClick(R.id.editButton)
    public void onEditClick(View view) {
        mActivityProfileBinding.setIsEditing(!mActivityProfileBinding.getIsEditing());

        if (mActivityProfileBinding.getIsEditing())
            mActivityProfileBinding.editTextDisplayname.requestFocus();

        if (!mActivityProfileBinding.getIsEditing()) {
            if (mAvatarUri != null) {
                mProfileViewModel.uploadAndSave(mAvatarUri, mActivityProfileBinding.getProfile());
            } else {
                mProfileViewModel.save(mActivityProfileBinding.getProfile());
            }
            mAvatarUri = null;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case UCrop.REQUEST_CROP:
                    mAvatarUri = UCrop.getOutput(data);
                    Log.d(TAG, "Crop Destination URI: " + mAvatarUri.toString());
                    mActivityProfileBinding.getProfile().setAvatarUrl(mAvatarUri.toString());
                    break;
                case UCrop.RESULT_ERROR:
                    break;
                default:
                    break;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void updateUI(final Profile profile) {
        mActivityProfileBinding.setProfile(profile);
        mActivityProfileBinding.executePendingBindings();
    }

    public void pickImage() {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            List<String> permissions = new ArrayList<>();
//
//            permissions.add(Manifest.permission.READ_EXTERNAL_STORAGE);
//            permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
//
//            if(isCamerasAvailable())
//                permissions.add(Manifest.permission.CAMERA);
//
//            if(checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
//                    && checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
//
//                boolean shouldRetryAskingPermissions = true;
//
//                if(!shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE)){
//                    shouldRetryAskingPermissions = false;
//                    permissions.remove(Manifest.permission.READ_EXTERNAL_STORAGE);
//                }
//
//                if(shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)){
//                    shouldRetryAskingPermissions = false;
//                    permissions.remove(Manifest.permission.WRITE_EXTERNAL_STORAGE);
//                }
//
//                if(isCamerasAvailable() && checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
//                    if(!shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)){
//                        shouldRetryAskingPermissions = false;
//                        permissions.remove(Manifest.permission.CAMERA);
//                    }
//                }
//
//                if (shouldRetryAskingPermissions && permissions.size() > 0) {
//                    Snackbar.make(mActivityProfileBinding.coordLayout, R.string.external_storage_permission_rationale, Snackbar.LENGTH_INDEFINITE)
//                            .setAction("OK", (v -> requestPermissions(permissions.toArray(new String[]), REQUEST_STORAGE_PERMISSION)))
//                            .show();
//                    return;
//                }
//
//                if(permissions.size() > 0){
//                    requestPermissions(new String[]{
//                            Manifest.permission.READ_EXTERNAL_STORAGE,
//                            Manifest.permission.WRITE_EXTERNAL_STORAGE
//                    }, REQUEST_STORAGE_PERMISSION);
//                    return;
//                }
//            }
//        }

        PickImageDialog
                .build(this)
                .show(this);
    }

//    private boolean isCamerasAvailable() {
//        String feature = PackageManager.FEATURE_CAMERA;
//
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
//            feature = PackageManager.FEATURE_CAMERA_ANY;
//        }
//
//        return this.getPackageManager().hasSystemFeature(feature);
//    }

//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        if(requestCode == REQUEST_STORAGE_PERMISSION){
//            boolean isAllGranted = true;
//            for(int grantResult : grantResults){
//                isAllGranted = grantResult == PackageManager.PERMISSION_GRANTED;
//            }
//
//            if(isAllGranted){
//                pickImage();
//            }else{
//                Snackbar.make(mActivityProfileBinding.coordLayout, "Permissions non accordées! Nous tenterons de vous le redemander ultérieurement!", Snackbar.LENGTH_LONG)
//                        .show();
//            }
//        }
//    }

    @Override
    public void onPickResult(PickResult result) {
        if (result.getError() == null) {
            if (result.getUri() != null && !result.getUri().toString().isEmpty()) {
                cropImage(result.getUri());
            }
        }
    }


    /**
     * Crop the image at the specified URI
     *
     * @param sourceUri the image source uri
     */
    private void cropImage(@NonNull final Uri sourceUri) {
        String ext = getImageExtension(sourceUri);

        String destinationFilename = String.format("avatar_%s.%s",
                FirebaseAuth.getInstance().getCurrentUser().getUid(),
                ext);

        Uri destinationUri = Uri.fromFile(new File(getCacheDir(), destinationFilename));

        int size = (int) getResources().getDimension(R.dimen.profile_avatar_view);

        UCrop.of(sourceUri, destinationUri)
                .withAspectRatio(1, 1)
                .withMaxResultSize(size, size)
                .start(this);

        Log.d(TAG, "Source: " + sourceUri.toString());
        Log.d(TAG, "Destination: " + destinationUri.toString());
    }

    @NonNull
    private String getImageExtension(@NonNull Uri sourceUri) {
        String[] parts = sourceUri.getPath().split(".");
        String ext = "jpg";

        if (parts.length > 0) {
            String[] imageTypes = new String[]{"jpeg", "jpg", "png"};
            if (Arrays.asList(imageTypes).contains(parts[parts.length - 1]))
                ext = parts[parts.length - 1];
        }
        return ext;
    }
}
