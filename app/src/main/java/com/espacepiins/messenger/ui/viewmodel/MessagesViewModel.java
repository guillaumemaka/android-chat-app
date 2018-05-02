package com.espacepiins.messenger.ui.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;
import android.util.Log;

import com.espacepiins.messenger.application.FirebaseRefs;
import com.espacepiins.messenger.model.Message;
import com.espacepiins.messenger.model.Profile;
import com.espacepiins.messenger.model.Room;
import com.facebook.stetho.common.ArrayListAccumulator;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class MessagesViewModel extends AndroidViewModel {
    private final String TAG = MessagesViewModel.class.getName();

    private final MutableLiveData<List<Message>> mMessages;
    private final MutableLiveData<Boolean> mRecipientConnected;
    private final MutableLiveData<Profile> mTo;
    private final MutableLiveData<Profile> mFrom;
    private final MutableLiveData<Long> mLastOnline;

    private Room mRoom;
    private Query mMessagesQuery;
    private Query mStatusQuery;
    private Query mLastOnlineQuery;
    private String roomID;

    private final FirebaseDatabase mDatabase;

    private final ValueEventListener mStatusEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            final Boolean status = dataSnapshot.getValue(Boolean.class);
            Log.d(TAG, "Connected: " + status);
            mRecipientConnected.postValue(status);
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {
            Log.w(TAG, databaseError.getMessage(), databaseError.toException());
        }
    };

    private final ValueEventListener mLastOnlineEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            final Long lastOnline = dataSnapshot.getValue(Long.class);
            Log.d(TAG, "lastOnline: " + lastOnline);
            mLastOnline.postValue(lastOnline);
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {
            Log.w(TAG, databaseError.getMessage(), databaseError.toException());
        }
    };

    private ValueEventListener mMessagesValueEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            final List<Message> messages = new ArrayListAccumulator<>();
            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                final Message message = snapshot.getValue(Message.class);
                messages.add(message);
            }
            mMessages.postValue(messages);
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {
            Log.w(TAG, databaseError.getMessage(), databaseError.toException());
        }
    };


    public MessagesViewModel(@NonNull Application application) {
        super(application);
        this.mMessages = new MutableLiveData<>();
        this.mRecipientConnected = new MutableLiveData<>();
        this.mRecipientConnected.setValue(false);
        this.mTo = new MutableLiveData<>();
        this.mFrom = new MutableLiveData<>();
        this.mLastOnline = new MutableLiveData<>();
        this.mDatabase = FirebaseDatabase.getInstance();
    }

    public String getRoomID() {
        return roomID;
    }

    public void setRoomID(String roomID) {
        this.roomID = roomID;

        this.mMessagesQuery = FirebaseDatabase.getInstance().getReference(FirebaseRefs.MESSAGES_REF(roomID))
                .orderByKey();

        this.mMessagesQuery.addValueEventListener(mMessagesValueEventListener);

        getUserProfiles(roomID);
    }

    protected void getUserProfiles(String roomID) {
        mDatabase.getReference(FirebaseRefs.USER_ROOMS_REF(FirebaseAuth.getInstance().getCurrentUser().getUid()) + "/" + roomID)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        mRoom = dataSnapshot.getValue(Room.class);

                        Log.d(TAG, "mRoom: " + mRoom);

                        mDatabase.getReference(FirebaseRefs.USER_PROFILES_REF(mRoom.getTo()))
                                .addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        final Profile profile = dataSnapshot.getValue(Profile.class);
                                        mTo.postValue(profile);
                                        monitorStatus(profile.getUserUID());
                                        Log.d(TAG, "To: " + profile);
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {
                                        Log.w(TAG, databaseError.getMessage(), databaseError.toException());
                                    }
                                });

                        mDatabase.getReference(FirebaseRefs.USER_PROFILES_REF(mRoom.getFrom()))
                                .addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        final Profile profile = dataSnapshot.getValue(Profile.class);
                                        mFrom.postValue(profile);
                                        Log.d(TAG, "From: " + profile);
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {
                                        Log.w(TAG, databaseError.getMessage(), databaseError.toException());
                                    }
                                });
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.w(TAG, databaseError.getMessage(), databaseError.toException());
                    }
                });
    }

    public MutableLiveData<Long> getLastOnline() {
        return mLastOnline;
    }

    public MutableLiveData<Profile> getToProfile() {
        return mTo;
    }

    public void monitorStatus(String to) {
        this.mStatusQuery = FirebaseDatabase.getInstance().getReference(FirebaseRefs.USERS_PRESENCE_REF(to));
        this.mStatusQuery.addValueEventListener(mStatusEventListener);
        this.mLastOnlineQuery = FirebaseDatabase.getInstance().getReference(FirebaseRefs.USER_LAST_ONLINE_REF(to));
        this.mLastOnlineQuery.addValueEventListener(mLastOnlineEventListener);
    }

    public MutableLiveData<List<Message>> getMessages() {
        return mMessages;
    }

    public void postMessage(String text) {
        final DatabaseReference messageRef = FirebaseDatabase
                .getInstance().getReference(FirebaseRefs.MESSAGES_REF(roomID))
                .push();

        final Map<String, Object> values = new HashMap<>();

        final Message message = new Message();
        message.setContent(text);
        message.setSender(mFrom.getValue().getUserUID());
        message.setTo(mTo.getValue().getUserUID());
        message.setRoomId(roomID);

        Log.d(TAG, "Posted Message: " + message);

        // Update the room for this user (sender)
        mRoom.setLastMessage(text);
        mRoom.setLastMessageUID(messageRef.getKey());
        mRoom.setLastMessageTimestamp(message.getTimestamp());

        // Update/create the room for the recipient user (to)
        final Room roomToRecipient = new Room();

        roomToRecipient.setRoomUID(roomID);
        roomToRecipient.setFromDisplayName(mTo.getValue().getDisplayName());
        roomToRecipient.setFrom(mTo.getValue().getUserUID());
        roomToRecipient.setToDisplayName(mFrom.getValue().getDisplayName());
        roomToRecipient.setTo(mFrom.getValue().getUserUID());
        roomToRecipient.setLastMessage(text);
        roomToRecipient.setLastMessageUID(messageRef.getKey());
        roomToRecipient.setLastMessageTimestamp(message.getTimestamp());

        values.put("/users/" + mTo.getValue().getUserUID() + "/rooms/" + roomID, roomToRecipient.toMap());
        values.put("/users/" + mFrom.getValue().getUserUID() + "/rooms/" + roomID, mRoom.toMap());
        values.put(FirebaseRefs.MESSAGES_REF(roomID) + "/" + messageRef.getKey(), message.toMap());

        // Batch update
        FirebaseDatabase.getInstance().getReference()
                .updateChildren(values);
    }

    public MutableLiveData<Boolean> getRecipientStatus() {
        return mRecipientConnected;
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        if (this.mMessagesQuery != null)
            if (mMessagesValueEventListener != null)
                this.mMessagesQuery.removeEventListener(mMessagesValueEventListener);

        if (mStatusQuery != null)
            if (mStatusEventListener != null)
                mStatusQuery.removeEventListener(mStatusEventListener);

        if (mLastOnlineQuery != null)
            if (mLastOnlineEventListener != null)
                mLastOnlineQuery.removeEventListener(mLastOnlineEventListener);
    }
}
