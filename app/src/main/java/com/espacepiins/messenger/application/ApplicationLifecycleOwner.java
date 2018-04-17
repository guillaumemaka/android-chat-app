package com.espacepiins.messenger.application;

import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleObserver;
import android.arch.lifecycle.OnLifecycleEvent;

import com.espacepiins.messenger.util.FirebaseUtil;

public final class ApplicationLifecycleOwner implements LifecycleObserver {
    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    public void onMoveToForeground() {
        FirebaseUtil.setConnected(true);
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    public void onMoveToBackground() {
        FirebaseUtil.setConnected(false);
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    public void onDestroy() {
        FirebaseUtil.setConnected(false);
    }
}
