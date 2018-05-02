package com.espacepiins.messenger.ui;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.espacepiins.messenger.R;
import com.espacepiins.messenger.application.Constants;
import com.espacepiins.messenger.util.PermissionRequester;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A {@link PreferenceActivity} that presents a set of application settings. On
 * handset devices, settings are presented as a single list. On tablets,
 * settings are split by category, with category headers shown to the left of
 * the list of settings.
 * <p>
 * See <a href="http://developer.android.com/design/patterns/settings.html">
 * Android Design: Settings</a> for design guidelines and the <a
 * href="http://developer.android.com/guide/topics/ui/settings.html">Settings
 * API Guide</a> for more information on developing a Settings UI.
 */
public class SettingsActivity extends FirebaseAuthAwareActivity {
    @BindView(R.id.settings_toolbar)
    Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        ButterKnife.bind(this);

        setupActionBar();

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.settings_container, new SettingsFragment())
                .commit();
    }

    /**
     * Set up the {@link android.app.ActionBar}, if the API is available.
     */
    private void setupActionBar() {
        setSupportActionBar(mToolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Show the Up button in the action bar.
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(R.string.setting_activity_title);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public static class SettingsFragment extends PreferenceFragmentCompat {
        private final int ENABLE_CONTACT_SYNC_REQUEST_CODE = 0x1;

        public SettingsFragment() {
        }

        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            addPreferencesFromResource(R.xml.preferences);
        }

        @Override
        public void onStart() {
            super.onStart();

            // Check if we have the READ_CONTACTS Epermission
            final PermissionRequester permissionRequester = new PermissionRequester(getActivity());
            permissionRequester.setPermissions(Manifest.permission.READ_CONTACTS)
                    .setRequestCode(ENABLE_CONTACT_SYNC_REQUEST_CODE)
                    .request((grantedPermissions, deniedPermissions, requestCode) -> {
                        if (requestCode == ENABLE_CONTACT_SYNC_REQUEST_CODE) {
                            if (grantedPermissions.size() == 1) {
                                // If we have the required permission
                                // enable the preference
                                enableContactsSync();
                            } else {
                                // Otherwise try re-asking
                                if (deniedPermissions.size() == 1) {
                                    // Check if we can re-ask the permission
                                    if (deniedPermissions.get(Manifest.permission.READ_CONTACTS)) {
                                        Snackbar.make(this.getView(), R.string.read_contacts_permission_rational_message_hint, Snackbar.LENGTH_INDEFINITE)
                                                .setAction("OK", v -> requestPermissions(deniedPermissions.keySet().toArray(new String[deniedPermissions.size()]), requestCode))
                                                .show();
                                    }
                                }
                            }
                        }
                    });
        }

        @Override
        public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
            if (requestCode == ENABLE_CONTACT_SYNC_REQUEST_CODE) {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    enableContactsSync();
                }
            }
        }

        protected void enableContactsSync() {
            final Preference pref = findPreference(Constants.LAST_CONTACT_IMPORTED);
            if (pref != null) {
                pref.setEnabled(true);
            }
        }
    }
}
