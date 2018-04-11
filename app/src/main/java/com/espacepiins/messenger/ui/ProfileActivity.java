package com.espacepiins.messenger.ui;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.databinding.BindingAdapter;
import android.databinding.BindingMethod;
import android.databinding.BindingMethods;
import android.databinding.DataBindingUtil;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.espacepiins.messenger.application.GlideApp;
import com.espacepiins.messenger.model.Profile;
import com.espacepiins.messenger.ui.viewmodel.ProfileViewModel;
import com.espacepiins.messenger.util.GravatarUtil;
import com.espacepiins.messsenger.R;
import com.espacepiins.messsenger.databinding.ActivityProfileBinding;
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
public class ProfileActivity extends AppCompatActivity implements IPickResult {
    private static final String TAG = ProfileActivity.class.getName();

    private ActivityProfileBinding mActivityProfileBinding;
    private ProfileViewModel mProfileViewModel;
    private Uri mAvatarUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mActivityProfileBinding = DataBindingUtil.setContentView(this, R.layout.activity_profile);

        ButterKnife.bind(this, mActivityProfileBinding.getRoot());

        mProfileViewModel = ViewModelProviders.of(this).get(ProfileViewModel.class);

        mProfileViewModel.getProfileData().observe(this, this::updateUI);

        mActivityProfileBinding.setIsEditing(false);

        setSupportActionBar(mActivityProfileBinding.toolbar);
        ActionBar actionBar = getSupportActionBar();

        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.ic_cancel);
        actionBar.setTitle(R.string.activity_profile_title);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.profile_activity_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
            case R.id.action_signout:
                Log.d(TAG, "Signout called!");
                return false;
        }
        return super.onOptionsItemSelected(item);
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

    @BindingAdapter({"bind:imageUrl", "bind:fallbackDrawable", "bind:gravatar"})
    public static void loadImageUrl(ImageView view, String imageUrl, Drawable fallbackDrawable, String email) {
        Log.i(TAG, "app:imageUrl: " + imageUrl);
        String url = imageUrl;

        if (url == null) {
            int size = (int) view.getResources().getDimension(R.dimen.profile_avatar_view);
            url = GravatarUtil.getGravatar(email, size);
            Log.i(TAG, "Gravatar: " + url + " Email: " + email);
        }

        GlideApp.with(view)
                .load(url)
                .centerCrop()
                .circleCrop()
                .transition(DrawableTransitionOptions.withCrossFade())
                .placeholder(fallbackDrawable)
                .fallback(fallbackDrawable)
                .into(view);
    }
}
