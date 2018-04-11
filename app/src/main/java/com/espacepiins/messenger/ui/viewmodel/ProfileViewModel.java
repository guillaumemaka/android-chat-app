package com.espacepiins.messenger.ui.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.MutableLiveData;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;

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
    private MutableLiveData<Profile> profileData;
    private DatabaseReference mDatabaseReference;
    private StorageReference mStorageReference;

    public ProfileViewModel(@NonNull Application application) {
        super(application);
        this.profileData = new MutableLiveData<>();

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            this.mDatabaseReference = FirebaseDatabase.getInstance().getReference("profiles/" + currentUser.getUid());
            this.mDatabaseReference.addValueEventListener(this);
            this.mStorageReference = FirebaseStorage.getInstance().getReference("user-avatar");
        }
    }

    public MutableLiveData<Profile> getProfileData() {
        return profileData;
    }

    public void save(Profile profile) {
        if (mDatabaseReference != null) {
            this.mDatabaseReference.setValue(profile.toMap());
            this.profileData.postValue(profile);
        }
    }

    public void uploadAndSave(final Uri fileUri, final Profile profile) {
        final StorageReference reference = this.mStorageReference.child(fileUri.getLastPathSegment());
        reference.putFile(fileUri)
                .addOnSuccessListener(task -> {
                    profile.setAvatarUrl(task.getDownloadUrl().toString());
                    save(profile);
                });
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
        this.mDatabaseReference.removeEventListener(this);
        super.onCleared();
    }
}
