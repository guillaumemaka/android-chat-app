package com.espacepiins.messenger.ui.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.MutableLiveData;
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

public final class ProfileViewModel extends AndroidViewModel implements ValueEventListener {
    private final String TAG = ProfileViewModel.class.getName();
    private MutableLiveData<Profile> profileData;
    private DatabaseReference mDatabaseReference;

    public ProfileViewModel(@NonNull Application application) {
        super(application);
        this.profileData = new MutableLiveData<>();

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            this.mDatabaseReference = FirebaseDatabase.getInstance().getReference("profiles/" + currentUser.getUid());
            this.mDatabaseReference.addValueEventListener(this);
        }
    }

    public MutableLiveData<Profile> getProfileData() {
        return profileData;
    }

    public void save(Profile profile) {
        Log.i(TAG, "Livedata Profile: " + this.profileData.getValue());
        Log.i(TAG, "Profile: " + profile);
        if (mDatabaseReference != null) {
            this.mDatabaseReference.setValue(profile.toMap());
            this.profileData.postValue(profile);
        }
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
