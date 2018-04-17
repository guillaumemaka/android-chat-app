package com.espacepiins.messenger.application;

import android.annotation.SuppressLint;
import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleObserver;
import android.arch.lifecycle.OnLifecycleEvent;
import android.content.Context;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

public final class LocationListener implements LifecycleObserver {
    FusedLocationProviderClient mClient;
    LocationCallback mLocationCallback;
    Lifecycle mLifecycle;
    LocationRequest mLocationRequest;

    public LocationListener(Context context, Lifecycle lifecycle, LocationCallback callback) {
        this.mLocationCallback = callback;
        this.mLifecycle = lifecycle;
        this.mClient = LocationServices.getFusedLocationProviderClient(context);
        mLocationRequest = LocationRequest.create();
        mLocationRequest.setInterval(10000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_LOW_POWER);
        this.mLifecycle.addObserver(this);
    }

    public FusedLocationProviderClient getClient() {
        return mClient;
    }

    @SuppressLint("MissingPermission")
    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    public void onStart() {
        this.mClient.requestLocationUpdates(mLocationRequest, mLocationCallback, null);
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    public void onStop() {
        this.mClient.removeLocationUpdates(mLocationCallback);
    }
}
