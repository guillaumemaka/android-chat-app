package com.espacepiins.messenger.ui;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;
import com.espacepiins.messenger.model.Contact;
import com.espacepiins.messenger.ui.ContactListFragment.OnListFragmentInteractionListener;
import com.espacepiins.messsenger.R;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * {@link RecyclerView.Adapter} that can display a {@link Contact} and makes a call to the
 * specified {@link OnListFragmentInteractionListener}.
 * TODO: Replace the implementation with code for your data type.
 */
public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.ContactViewHolder> implements ContactAdapterContract {
    @Nullable
    private List<? extends Contact> mContacts;
    @Nullable
    private OnListFragmentInteractionListener mListener;

    public ContactAdapter() {
    }

    public ContactAdapter(@Nullable final List<? extends Contact> items, @Nullable final OnListFragmentInteractionListener listener) {
        mContacts = items;
        mListener = listener;
    }

    @Override
    public ContactViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_contact, parent, false);
        return new ContactViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ContactViewHolder holder, int position) {
        holder.setContact(mContacts.get(position));
        Log.d(ContactAdapter.class.getName(), mContacts.get(position).getDisplayName());
        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onListFragmentInteraction(holder.mContact);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mContacts.size();
    }

    @Override
    public void setContacts(final List<? extends Contact> contacts) {
        Log.d(ContactAdapter.class.getName(), "Contacts size: " + contacts.size());
        mContacts = contacts;
        notifyDataSetChanged();
    }

    public void setListener(final OnListFragmentInteractionListener listener) {
        mListener = listener;
    }

    public static class ContactViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.row)
        View mView;
        @BindView(R.id.contact_avatar)
        ImageView mAvatarImageView;
        @BindView(R.id.contact_display_name)
        TextView mDisplayNameTextView;
        @BindView(R.id.contact_info)
        TextView mInfoTextView;
        @BindView(R.id.in_app_status)
        ImageView mInAppStatus;

        @NonNull
        public Contact mContact;


        public ContactViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }

        public void setContact(@NonNull final Contact contact) {
            mContact = contact;
            mDisplayNameTextView.setText(contact.getDisplayName());

            if(mContact.getEmailAddress() != null){
                mInfoTextView.setText(contact.getEmailAddress());
            } else{
                if(mContact.getPhoneNumber() != null){
                    mInfoTextView.setText(mContact.getPhoneNumber());
                }
            }

            if(mContact.getFirebaseUID() == null){
                mInAppStatus.setImageResource(R.drawable.ic_men);
            }else {
                mInAppStatus.setImageResource(R.drawable.ic_chat_bubbles);
            }

            Glide.with(mView)
                    .load(mContact.getPhotoThumbnailUri())
                    .apply(RequestOptions.placeholderOf(R.drawable.avatar_overlay_small))
                    .apply(RequestOptions.centerCropTransform())
                    .apply(RequestOptions.circleCropTransform())
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .into(mAvatarImageView);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mDisplayNameTextView.getText() + "'";
        }
    }
}
