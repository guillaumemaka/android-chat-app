package com.espacepiins.messenger.ui.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.os.AsyncTask;
import android.support.annotation.NonNull;

import com.espacepiins.messenger.application.FirebaseRefs;
import com.espacepiins.messenger.db.AppDatabase;
import com.espacepiins.messenger.db.entity.RoomEntity;
import com.espacepiins.messenger.model.Message;
import com.espacepiins.messenger.model.Room;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.util.Date;
import java.util.List;

/**
 * Created by guillaume on 18-03-20.
 */

public class RoomListViewModel extends AndroidViewModel implements ChildEventListener {
    private LiveData<List<RoomEntity>> mRooms;
    private Query mQuery;
    private AppDatabase mAppDatabase;

    public RoomListViewModel(@NonNull Application application) {
        super(application);
        this.mAppDatabase = AppDatabase.getInstance(application);
        this.mRooms = mAppDatabase.roomDao().getRooms();
        this.mQuery = FirebaseDatabase.getInstance().getReference(FirebaseRefs.MESSAGES_REF)
                .orderByChild("timestamp")
                .equalTo(FirebaseAuth.getInstance().getCurrentUser().getUid());
        mQuery.addChildEventListener(this);
    }

    public LiveData<List<RoomEntity>> getRooms() {
        return mRooms;
    }

    public String createRoom(@NonNull  String from, @NonNull String to){
        DatabaseReference roomRef = FirebaseDatabase.getInstance().getReference(FirebaseRefs.MESSAGES_REF).getRef();
        Room room = new Room();
        room.setFrom(from);
        room.setTo(to);
        roomRef.push().setValue(room.toMap());
        return roomRef.getKey();
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        mQuery.removeEventListener(this);
    }

    @Override
    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
        for(DataSnapshot messageSnapshot : dataSnapshot.getChildren()){
            final Message message = messageSnapshot.getValue(Message.class);
            new updateRoomwithLasMessageAsyncTask().execute(message);
        }
    }

    @Override
    public void onChildChanged(DataSnapshot dataSnapshot, String s) {

    }

    @Override
    public void onChildRemoved(DataSnapshot dataSnapshot) {

    }

    @Override
    public void onChildMoved(DataSnapshot dataSnapshot, String s) {

    }

    @Override
    public void onCancelled(DatabaseError databaseError) {

    }

    public class updateRoomwithLasMessageAsyncTask extends AsyncTask<Message, Void, Void>{
        @Override
        protected Void doInBackground(Message... messages) {
            RoomEntity roomEntity = mAppDatabase.roomDao().getRoomById(messages[0].getRoomId());
            roomEntity.setLastMessageDate(new Date(messages[0].getTimestamp()));
            roomEntity.setLastMessage(messages[0].getContent());
            mAppDatabase.roomDao().update(roomEntity);
            return null;
        }
    }
}
