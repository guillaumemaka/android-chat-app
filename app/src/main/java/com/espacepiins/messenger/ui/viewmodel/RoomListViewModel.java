package com.espacepiins.messenger.ui.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;
import android.util.Log;

import com.espacepiins.messenger.application.FirebaseRefs;
import com.espacepiins.messenger.db.AppDatabase;
import com.espacepiins.messenger.model.Profile;
import com.espacepiins.messenger.model.Room;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by guillaume on 18-03-20.
 */

public class RoomListViewModel extends AndroidViewModel implements ValueEventListener {
    private final String TAG = RoomListViewModel.class.getName();

    public interface OnRoomCreated {
        void onSuccess(String roomKey);

        void onFailure(Exception exception);
    }

    private MutableLiveData<List<Room>> mRooms;
    private Query mRoomQuery;
    private AppDatabase mAppDatabase;
    private FirebaseDatabase mFirebaseDatabase;
    private FirebaseUser mCurrentUser;

    public RoomListViewModel(@NonNull Application application) {
        super(application);
        this.mAppDatabase = AppDatabase.getInstance(application);
        this.mRooms = new MutableLiveData<>();
        this.mRooms.setValue(new ArrayList<>());
        this.mFirebaseDatabase = FirebaseDatabase.getInstance();
        this.mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();

        if (this.mCurrentUser == null)
            throw new RuntimeException("You shouldn't be here!");

        mRoomQuery = mFirebaseDatabase.getReference(FirebaseRefs.USER_ROOMS_REF(mCurrentUser.getUid()));
        mRoomQuery.addValueEventListener(this);
    }

    public LiveData<List<Room>> getRooms() {
        return mRooms;
    }

    public void createOrRetrieveRoom(@NonNull Profile from, @NonNull String to, OnRoomCreated completionHandler) {
        DatabaseReference toProfileRef = mFirebaseDatabase.getReference(FirebaseRefs.USER_PROFILES_REF(to));
        final String exists = searchExistingRoom(to);
        if (exists != null) {
            completionHandler.onSuccess(exists);
        } else {
            toProfileRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    final Profile toProfile = dataSnapshot.getValue(Profile.class);
                    final DatabaseReference roomRef = mFirebaseDatabase.getReference(FirebaseRefs.USER_ROOMS_REF(from.getUserUID())).push();

                    final Map<String, Object> update = new HashMap<>();

                    final Room room = new Room();

                    room.setRoomUID(roomRef.getKey());
                    room.setFrom(from.getUserUID());
                    room.setFromDisplayName(from.getDisplayName());
                    room.setTo(toProfile.getUserUID());
                    room.setToDisplayName(toProfile.getDisplayName());

                    update.put(FirebaseRefs.ROOMS_REF(room.getRoomUID()), room.toMap());
                    update.put(FirebaseRefs.USER_ROOMS_REF(from.getUserUID()) + "/" + room.getRoomUID(), room.toMap());

                    mFirebaseDatabase.getReference().updateChildren(update);

                    completionHandler.onSuccess(roomRef.getKey());
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    completionHandler.onFailure(databaseError.toException());
                }
            });
        }
    }

    public String searchExistingRoom(String to) {
        for (Room room : mRooms.getValue()) {
            if (room.getTo().equals(to)) {
                return room.getRoomUID();
            }
        }

        return null;
    }

    @Override
    public void onDataChange(DataSnapshot dataSnapshot) {
        final List<Room> rooms = new ArrayList<>();
        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
            rooms.add(snapshot.getValue(Room.class));
        }
        mRooms.postValue(rooms);
    }

    @Override
    public void onCancelled(DatabaseError databaseError) {
        Log.w(TAG, databaseError.getMessage(), databaseError.toException());
    }
}
