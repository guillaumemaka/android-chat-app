package com.espacepiins.messenger.util;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class PermissionRequester {
    private final Activity mActivity;
    private int mRequestCode;
    private String[] mPermissions;

    public PermissionRequester(@NonNull Activity activity) {
        this.mActivity = activity;
    }

    @NonNull
    public PermissionRequester setPermissions(@NonNull String... permissions) {
        this.mPermissions = permissions;
        return this;
    }

    @NonNull
    public PermissionRequester setRequestCode(@NonNull int requestCode) {
        this.mRequestCode = requestCode;
        return this;
    }

    public void request(PermissionRequestResultListener callback) {
        final Set<String> grantedPermissions = new HashSet<>();
        final Map<String, Boolean> deniedPermissions = new HashMap<>();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            for (String perm : this.mPermissions) {
                if (ContextCompat.checkSelfPermission(mActivity, perm) == PackageManager.PERMISSION_GRANTED) {
                    grantedPermissions.add(perm);
                } else {
                    deniedPermissions.put(perm, ActivityCompat.shouldShowRequestPermissionRationale(mActivity, perm));
                }
            }
        } else {
            grantedPermissions.addAll(Arrays.asList(mPermissions));
        }

        callback.onPermissionsResult(grantedPermissions, deniedPermissions, mRequestCode);
    }

    public interface PermissionRequestResultListener {
        void onPermissionsResult(Set<String> grantedPermissions, Map<String, Boolean> deniedPermissions, int requestCode);
    }
}
