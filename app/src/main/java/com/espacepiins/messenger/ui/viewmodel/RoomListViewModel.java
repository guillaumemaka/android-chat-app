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
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Created by guillaume on 18-03-20.
 */

public class RoomListViewModel extends AndroidViewModel implements ChildEventListener {
    private final String TAG = RoomListViewModel.class.getName();

    public interface OnRoomCreated {
        void onSuccess(String roomKey);

        void onFailure(Exception exception);
    }

    private MutableLiveData<List<Room>> mRooms;
    private Query mRoomQuery;
    private AppDatabase mAppDatabase;
    private FirebaseDatabase mFirebaseDatabase;

    public RoomListViewModel(@NonNull Application application) {
        super(application);
        this.mAppDatabase = AppDatabase.getInstance(application);
        this.mRooms = new MutableLiveData<>();
        this.mRooms.setValue(new ArrayList<>());
        this.mFirebaseDatabase = FirebaseDatabase.getInstance();
    }

    public LiveData<List<Room>> getRooms(String userUID) {
        mRoomQuery = mFirebaseDatabase.getReference(FirebaseRefs.ROOMS_REF(userUID));
        mRoomQuery.addChildEventListener(this);
        mRoomQuery.addListenerForSingleValueEvent(new ValueEventListener() {
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
        });

        return mRooms;
    }


    public void createRoom(@NonNull Profile from, @NonNull String to, OnRoomCreated completionHandler) {
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference toProfileRef = database.getReference(FirebaseRefs.USER_PROFILES_REF(to));

        toProfileRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final Profile toProfile = dataSnapshot.getValue(Profile.class);
                final DatabaseReference roomRef = database.getReference(FirebaseRefs.ROOMS_REF(from.getUserUID())).push();
                final Room room = new Room();
                room.setRoomUID(roomRef.getKey());
                room.setFrom(from.getUserUID());
                room.setFromDisplayName(from.getDisplayName());
                room.setTo(toProfile.getUserUID());
                room.setToDisplayName(toProfile.getDisplayName());
                roomRef.setValue(room.toMap());
                completionHandler.onSuccess(roomRef.getKey());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                completionHandler.onFailure(databaseError.toException());
            }
        });
    }

    @Override
    protected void onCleared() {
        mRoomQuery.removeEventListener(this);
        super.onCleared();
    }

    @Override
    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
        final Room room = dataSnapshot.getValue(Room.class);
        final List<Room> rooms = this.mRooms.getValue();
        rooms.add(room);
        mRooms.postValue(rooms);
        Log.d(TAG, room.toString());
    }

    @Override
    public void onChildChanged(DataSnapshot dataSnapshot, String s) {
        final Room changedRoom = dataSnapshot.getValue(Room.class);
        final List<Room> previousRooms = mRooms.getValue();
        for (Room room : previousRooms) {
            if (Objects.equals(room.getRoomUID(), changedRoom.getRoomUID())) {
                int index = previousRooms.indexOf(room);
                previousRooms.set(index, room);
                break;
            }
        }
        mRooms.postValue(previousRooms);
    }

    @Override
    public void onChildRemoved(DataSnapshot dataSnapshot) {
        final Room deletedRoom = dataSnapshot.getValue(Room.class);
        final List<Room> previousRooms = mRooms.getValue();
        for (Room room : previousRooms) {
            if (Objects.equals(room.getRoomUID(), deletedRoom.getRoomUID())) {
                previousRooms.remove(room);
                break;
            }
        }
        mRooms.postValue(previousRooms);
    }

    @Override
    public void onChildMoved(DataSnapshot dataSnapshot, String s) {

    }

    @Override
    public void onCancelled(DatabaseError databaseError) {
        Log.w(TAG, databaseError.getMessage(), databaseError.toException());
    }
}
