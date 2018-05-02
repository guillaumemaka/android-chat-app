package com.espacepiins.messenger.ui.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.MutableLiveData;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;

import com.espacepiins.messenger.application.FirebaseRefs;
import com.espacepiins.messenger.model.Profile;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public final class ProfileViewModel extends AndroidViewModel implements ValueEventListener {
    private final String TAG = ProfileViewModel.class.getName();
    private final FirebaseDatabase mDatabase;
    private MutableLiveData<Profile> profileData;
    private DatabaseReference mProfileRef;
    private StorageReference mStorageReference;
    private boolean readOnly = true;

    public ProfileViewModel(@NonNull Application application) {
        super(application);
        this.profileData = new MutableLiveData<>();
        this.mDatabase = FirebaseDatabase.getInstance();
    }

    public MutableLiveData<Profile> getProfileData(String userId) {
        final FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        this.readOnly = !currentUser.getUid().equals(userId);
        this.mProfileRef = mDatabase.getReference(FirebaseRefs.USER_PROFILES_REF(userId));
        this.mProfileRef.addValueEventListener(this);

        if (!readOnly) {
            this.mStorageReference = FirebaseStorage.getInstance().getReference(FirebaseRefs.USERS_AVATAR_STORAGE);
        }

        return profileData;
    }

    public void save(Profile profile) {
        if (isReadOnly())
            return;

        if (mProfileRef != null) {
            this.mProfileRef.setValue(profile.toMap());
            this.profileData.postValue(profile);
        }
    }

    public void uploadAndSave(final Uri fileUri, final Profile profile) {
        if (isReadOnly())
            return;

        final StorageReference reference = this.mStorageReference.child(fileUri.getLastPathSegment());
        reference.putFile(fileUri)
                .addOnSuccessListener(task -> {
                    profile.setAvatarUrl(task.getDownloadUrl().toString());
                    save(profile);
                });
    }

    public boolean isReadOnly() {
        return readOnly;
    }

    @Override
    public void onDataChange(DataSnapshot dataSnapshot) {
        Profile profile = dataSnapshot.getValue(Profile.class);
        this.profileData.postValue(profile);
    }

    @Override
    public void onCancelled(DatabaseError databaseError) {
        Log.w(TAG, databaseError.getMessage(), databaseError.toException());
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        if (mProfileRef != null)
            this.mProfileRef.removeEventListener(this);
    }
}
