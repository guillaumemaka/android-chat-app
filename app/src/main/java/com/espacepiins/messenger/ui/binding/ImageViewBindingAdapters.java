package com.espacepiins.messenger.ui.binding;

import android.databinding.BindingAdapter;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.widget.ImageView;

import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.espacepiins.messenger.R;
import com.espacepiins.messenger.application.FirebaseRefs;
import com.espacepiins.messenger.application.GlideApp;
import com.espacepiins.messenger.model.Profile;
import com.espacepiins.messenger.model.Room;
import com.espacepiins.messenger.util.GravatarUtil;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ImageViewBindingAdapters {
    private static final String TAG = ImageView.class.getName();

    @BindingAdapter(requireAll = true, value = {"bind:imageUrl", "bind:fallbackDrawable", "bind:gravatar"})
    public static void loadImageUrl(ImageView view, String imageUrl, Drawable fallbackDrawable, String email) {
        Log.i(TAG, "app:imageUrl: " + imageUrl);
        String url = gravatar(view, imageUrl, email);
        glidify(view, fallbackDrawable, url);
    }

    protected static String gravatar(ImageView view, String imageUrl, String email) {
        String url = imageUrl;

        if (url == null) {
            int size = (int) view.getResources().getDimension(R.dimen.profile_avatar_view);
            url = GravatarUtil.getGravatar(email, size);
            Log.i(TAG, "Gravatar: " + url + " Email: " + email);
        }
        return url;
    }

    protected static void glidify(ImageView view, Drawable fallbackDrawable, String imageUrl) {
        Drawable fallback = fallbackDrawable;

        if (fallback == null) {
            fallback = view.getResources().getDrawable(R.drawable.ic_face);
        }

        GlideApp.with(view)
                .load(imageUrl)
                .centerCrop()
                .circleCrop()
                .transition(DrawableTransitionOptions.withCrossFade())
                .placeholder(fallback)
                .fallback(fallback)
                .into(view);
    }

    @BindingAdapter("bind:fromRoom")
    public static void setFromRoom(ImageView view, Room room) {
        FirebaseDatabase.getInstance().getReference(FirebaseRefs.USER_PROFILES_REF(room.getTo()))
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        final Profile profile = dataSnapshot.getValue(Profile.class);
                        glidify(view, null, gravatar(view, profile.getAvatarUrl(), profile.getEmailAddress()));
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.d(TAG, databaseError.getMessage(), databaseError.toException());
                        glidify(view, null, null);
                    }
                });
    }
}
