package com.espacepiins.messenger.ui;

import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.espacepiins.messenger.R;
import com.espacepiins.messenger.application.FirebaseRefs;
import com.espacepiins.messenger.databinding.MessageRowBinding;
import com.espacepiins.messenger.model.Message;
import com.espacepiins.messenger.model.Profile;
import com.espacepiins.messenger.ui.callback.GenericDiffCallback;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {
    interface OnProfileSelectionListener {
        void showProfile(String profileId);
    }

    private final String TAG = MessageRowBinding.class.getName();
    private List<Message> mMessages;
    private OnProfileSelectionListener mListener;

    public MessageAdapter() {
        mMessages = new ArrayList<>();
    }

    public void setListener(OnProfileSelectionListener listener) {
        mListener = listener;
    }

    public void setMessages(List<Message> messages) {
        DiffUtil.DiffResult result = DiffUtil.calculateDiff(new GenericDiffCallback(mMessages, messages));
        mMessages = messages;
        result.dispatchUpdatesTo(this);
    }

    public boolean isMe(Message message) {
        return FirebaseAuth.getInstance()
                .getCurrentUser()
                .getUid().equals(message.getSender());
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        MessageRowBinding binding = DataBindingUtil.inflate(inflater, R.layout.message_row, parent, false);
        return new MessageViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {
        final Message message = mMessages.get(position);

        holder.binding.contactAvatar.setOnClickListener((v) -> {
            mListener.showProfile(holder.binding.getProfile().getUserUID());
        });

        FirebaseDatabase.getInstance().getReference(FirebaseRefs.USER_PROFILES_REF(message.getSender()))
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        final Profile profile = dataSnapshot.getValue(Profile.class);
                        holder.binding.setIsMe(isMe(message));
                        holder.binding.setMessage(message);
                        holder.binding.setProfile(profile);
                        holder.binding.executePendingBindings();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.w(TAG, databaseError.getMessage(), databaseError.toException());
                    }
                });
    }

    @Override
    public int getItemCount() {
        return mMessages.size();
    }

    class MessageViewHolder extends RecyclerView.ViewHolder {
        MessageRowBinding binding;

        public MessageViewHolder(MessageRowBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
