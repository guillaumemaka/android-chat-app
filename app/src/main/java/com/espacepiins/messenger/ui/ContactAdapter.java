package com.espacepiins.messenger.ui;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.espacepiins.messenger.R;
import com.espacepiins.messenger.application.GlideApp;
import com.espacepiins.messenger.model.Contact;
import com.espacepiins.messenger.model.SearchContactResult;
import com.espacepiins.messenger.ui.ContactListFragment.OnListFragmentInteractionListener;
import com.espacepiins.messenger.ui.callback.ContactSearchResultDiffCallback;

import java.util.ArrayList;
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
    private List<SearchContactResult> mContacts;
    @Nullable
    private OnListFragmentInteractionListener mListener;

    public ContactAdapter() {
        mContacts = new ArrayList<>();
    }

    public ContactAdapter(@Nullable final List<SearchContactResult> items, @Nullable final OnListFragmentInteractionListener listener) {
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
    public void setContacts(final List<SearchContactResult> contacts) {
        DiffUtil.DiffResult result = DiffUtil.calculateDiff(new ContactSearchResultDiffCallback(mContacts, contacts));
        mContacts = contacts;
        result.dispatchUpdatesTo(this);
    }

    public void setListener(final OnListFragmentInteractionListener listener) {
        mListener = listener;
    }

    public class ContactViewHolder extends RecyclerView.ViewHolder {
        private final String TAG = ContactViewHolder.class.getName();
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
        public SearchContactResult mContact;


        public ContactViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }

        public void setContact(@NonNull final SearchContactResult contact) {
            mContact = contact;
            updateUI(contact);
        }

        private void updateUI(@NonNull SearchContactResult contact) {
            mDisplayNameTextView.setText(contact.getDisplayName());

            if(mContact.getEmailAddresses() != null && mContact.getEmailAddresses().size() > 0){
                mInfoTextView.setText(contact.getEmailAddresses().get(0).getEmailAddress());
            }
//            else{
//                if(mContact.getPhoneNumbers() != null && mContact.getPhoneNumbers().size() > 0){
//                    mInfoTextView.setText(mContact.getPhoneNumbers().get(0).getPhoneNumber());
//                }
//            }

            if(mContact.getFirebaseUID() != null && !mContact.getFirebaseUID().isEmpty()){
                mInAppStatus.setImageResource(R.drawable.ic_chat_bubbles);
            }else {
                mInAppStatus.setImageResource(R.drawable.ic_men);
            }

            GlideApp.with(mView)
                    .load(mContact.getPhotoThumbnailUri())
                    .placeholder(R.drawable.avatar_overlay_small)
                    .centerCrop()
                    .circleCrop()
                    .fallback(R.drawable.ic_face)
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .into(mAvatarImageView);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mDisplayNameTextView.getText() + "'";
        }
    }
}
