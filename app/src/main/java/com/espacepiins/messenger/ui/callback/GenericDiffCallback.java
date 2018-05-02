package com.espacepiins.messenger.ui.callback;

import android.support.annotation.Nullable;
import android.support.v7.util.DiffUtil;

import java.util.List;

/**
 * Created by guillaume on 18-03-22.
 */

public class GenericDiffCallback<T extends Comparable> extends DiffUtil.Callback {
    public enum DiffState {
        STATE_NEW, STATE_OLD
    }

    List<T> oldData;
    List<T> newData;

    public GenericDiffCallback(List<T> oldData, List<T> newData) {
        this.oldData = oldData;
        this.newData = newData;
    }

    @Override
    public int getOldListSize() {
        return this.oldData.size();
    }

    @Override
    public int getNewListSize() {
        return this.newData.size();
    }

    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
        return oldData.get(oldItemPosition).compareTo(newData.get(newItemPosition)) == 0;
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        return oldData.get(oldItemPosition).equals(newData.get(newItemPosition));
    }

    @Nullable
    @Override
    public Object getChangePayload(int oldItemPosition, int newItemPosition) {
        if(oldData.get(oldItemPosition).compareTo(newData.get(newItemPosition)) > 0){
            return DiffState.STATE_NEW;
        }else {
            return DiffState.STATE_OLD;
        }
    }
}
