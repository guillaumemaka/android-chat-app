package com.espacepiins.messenger.ui.component;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceViewHolder;
import android.text.format.DateUtils;
import android.util.AttributeSet;
import android.widget.Button;

import com.espacepiins.messenger.R;
import com.espacepiins.messenger.application.Constants;
import com.espacepiins.messenger.service.ContactImportService;

import java.util.Date;

public final class PrefContactSync extends Preference implements SharedPreferences.OnSharedPreferenceChangeListener {
    private boolean syncing = false;

    public PrefContactSync(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PrefContactSync(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setWidgetLayoutResource(R.layout.preference_contact_sync);
        final SharedPreferences prefs = context.getSharedPreferences(context.getString(R.string.preference_key), Context.MODE_PRIVATE);
        prefs.registerOnSharedPreferenceChangeListener(this);
        bindSummary(prefs, getKey());
    }

    @Override
    public void onBindViewHolder(PreferenceViewHolder holder) {
        super.onBindViewHolder(holder);
        Button syncBtn = (Button) holder.findViewById(R.id.btn_pref_sync);

        syncBtn.setEnabled(isEnabled() && !syncing);

        syncBtn.setOnClickListener(v -> {
            syncing = true;
            v.setEnabled(false);
            ContactImportService.startActionContactImport(getContext());
        });
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(Constants.LAST_CONTACT_IMPORTED)) {
            syncing = false;
            bindSummary(sharedPreferences, key);
        }
    }

    public void bindSummary(SharedPreferences sharedPreferences, String key) {
        long timestamp = sharedPreferences.getLong(key, 0);
        if (timestamp == 0) {
            setSummary(getContext().getString(R.string.pref_sync_summary_alt));
        } else {
            setSummary(getContext().getString(
                    R.string.pref_sync_summary,
                    DateUtils.getRelativeTimeSpanString(timestamp, new Date().getTime(), Constants.DEFAULT_DATE_MIN_RESOLUTION)));
        }
    }
}
