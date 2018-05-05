package com.espacepiins.messenger.ui.callback;

import android.support.v7.util.DiffUtil;

import com.espacepiins.messenger.model.SearchContactResult;

import java.util.List;

/**
 * Created by guillaume on 18-03-24.
 */

public class ContactSearchResultDiffCallback extends DiffUtil.Callback {
    private final List<SearchContactResult> mOldSearchResult;
    private final List<SearchContactResult> mNewSearchResult;

    public ContactSearchResultDiffCallback(List<SearchContactResult> oldSearchResult, List<SearchContactResult> newSearchResult) {
        mOldSearchResult = oldSearchResult;
        mNewSearchResult = newSearchResult;
    }

    @Override
    public int getOldListSize() {
        return mOldSearchResult.size();
    }

    @Override
    public int getNewListSize() {
        return mNewSearchResult.size();
    }

    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
        return mOldSearchResult.get(oldItemPosition).equals(mNewSearchResult.get(newItemPosition));
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        try {
            if (mOldSearchResult.get(oldItemPosition).getDisplayName().compareTo(mNewSearchResult.get(newItemPosition).getDisplayName()) == 0)
                if (mOldSearchResult.get(oldItemPosition).getLookupKey().compareTo(mNewSearchResult.get(newItemPosition).getLookupKey()) == 0)
                    if (mOldSearchResult.get(oldItemPosition).getFirebaseUID() != null && mNewSearchResult.get(newItemPosition).getFirebaseUID() != null)
                        if (mOldSearchResult.get(oldItemPosition).getFirebaseUID().equals(mNewSearchResult.get(newItemPosition).getFirebaseUID()))
                            if (mOldSearchResult.get(oldItemPosition).getPhotoThumbnailUri().equals(mNewSearchResult.get(newItemPosition).getPhotoThumbnailUri()))
                                return true;
        } catch (NullPointerException e) {
            return false;
        }
        return false;
    }
}
