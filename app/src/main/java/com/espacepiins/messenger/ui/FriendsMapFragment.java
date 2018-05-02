package com.espacepiins.messenger.ui;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.request.target.BaseTarget;
import com.bumptech.glide.request.target.SizeReadyCallback;
import com.bumptech.glide.request.transition.Transition;
import com.espacepiins.messenger.R;
import com.espacepiins.messenger.application.FirebaseRefs;
import com.espacepiins.messenger.application.GlideApp;
import com.espacepiins.messenger.application.LocationListener;
import com.espacepiins.messenger.application.MessengerApplicationContext;
import com.espacepiins.messenger.db.AppDatabase;
import com.espacepiins.messenger.db.entity.ContactEntity;
import com.espacepiins.messenger.model.Profile;
import com.espacepiins.messenger.util.MapUtil;
import com.espacepiins.messenger.util.PermissionRequester;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryDataEventListener;
import com.firebase.geofire.core.GeoHash;
import com.google.android.gms.location.LocationAvailability;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public final class FriendsMapFragment extends SupportMapFragment implements OnMapReadyCallback, GeoQueryDataEventListener, GoogleMap.OnCameraMoveListener {
    private final String TAG = FriendsMapFragment.class.getName();

    private static final int LOCATION_SERVICE_REQUEST_CODE = 0x1;
    private static final int INITIAL_ZOOM_LEVEL = 14;

    private GoogleMap mGoogleMap;
    private LocationListener mLocationListener;
    private GeoQuery mGeoQuery;
    private GeoFire mGeoFire;
    private Circle searchCircle;
    private Map<String, Marker> markers = new HashMap<>();
    private Set<String> mRegisteredUsers = new HashSet<>();

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == LOCATION_SERVICE_REQUEST_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                initializeGeoFireQuery();
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onStart() {
        super.onStart();
        this.getMapAsync(this);
    }

    /**
     * Initialize a GeoFire object for the current logged in user.
     * and start querying for friends around him (default radius: 6 kilometers).
     */
    @SuppressLint("MissingPermission")
    private void initializeGeoFireQuery() {
        Log.d(TAG, "initializeGeoFireQuery");

        final DatabaseReference locationsRef = FirebaseDatabase.getInstance().getReference("user-locations");
        mGeoFire = new GeoFire(locationsRef);

        mLocationListener = new LocationListener(this.getContext(), getLifecycle(), new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult != null) {
                    final Location lastLocation = locationResult.getLastLocation();
                    final LatLng currentLatLng = new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude());
                    Log.d(TAG, "Location Updates " + locationResult.getLastLocation().toString());

//                    // Not working issue https://github.com/firebase/geofire-java/issues/28
//                    mGeoFire.setLocation(FirebaseAuth.getInstance().getCurrentUser().getUid(), new GeoLocation(lastLocation.getLatitude(), lastLocation.getLongitude()), new GeoFire.CompletionListener() {
//                        @Override
//                        public void onComplete(String key, DatabaseError error) {
//                            Log.d(TAG, "GeoFire saved key: " + key);
//                            Log.w(TAG, error.getMessage(), error.toException());
//                        }
//                    });
                    // Workaround:
                    GeoHash geoHash = new GeoHash(new GeoLocation(lastLocation.getLatitude(), lastLocation.getLongitude()));
                    Map<String, Object> updates = new HashMap<>();
                    updates.put("g", geoHash.getGeoHashString());
                    updates.put("l", Arrays.asList(lastLocation.getLatitude(), lastLocation.getLongitude()));
                    mGeoFire.getDatabaseReference()
                            .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                            .setValue(updates, geoHash.getGeoHashString());

                    if (mGeoQuery == null) {
                        mGeoQuery = mGeoFire.queryAtLocation(new GeoLocation(lastLocation.getLatitude(), lastLocation.getLongitude()), 0.6);
                        mGeoQuery.addGeoQueryDataEventListener(FriendsMapFragment.this);
                    } else {
                        mGeoQuery.setCenter(new GeoLocation(lastLocation.getLatitude(), lastLocation.getLongitude()));
                    }

                    if (searchCircle == null) {
                        searchCircle = mGoogleMap.addCircle(new CircleOptions().center(currentLatLng).radius(1000));
                    } else {
                        searchCircle.setCenter(currentLatLng);
                    }

                    searchCircle.setFillColor(Color.argb(66, 255, 0, 255));
                    searchCircle.setStrokeColor(Color.argb(66, 0, 0, 0));

                    mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, INITIAL_ZOOM_LEVEL));

                }
            }

            @Override
            public void onLocationAvailability(LocationAvailability locationAvailability) {

            }
        });

//        mLocationListener.getClient().getLastLocation().addOnSuccessListener(location -> mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), INITIAL_ZOOM_LEVEL)));

        mGoogleMap.setMyLocationEnabled(true);
    }

    private double zoomLevelToRadius(double zoomLevel) {
        // Approximation to fit circle into view
        return 16384000 / Math.pow(2, zoomLevel);
    }

    private boolean isUserInContacts(String firebaseUID) {
        return mRegisteredUsers.contains(firebaseUID) || FirebaseAuth.getInstance().getCurrentUser().getUid().equals(firebaseUID);
    }

    /**************************************************************
     *
     * Gmap OnMapReadyCallback
     *
     *************************************************************/

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.mGoogleMap = googleMap;
        this.mGoogleMap.setOnCameraMoveListener(this);

        final PermissionRequester permissionRequester = new PermissionRequester(this.getActivity())
                .setPermissions(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
                .setRequestCode(LOCATION_SERVICE_REQUEST_CODE);

        permissionRequester.request((grantedPermissions, deniedPermissions, requestCode) -> {
            if (grantedPermissions.size() == 2) {
                new fetchRegisteredUserAndInit(getContext()).execute();
            } else if (deniedPermissions.size() > 0) {
                Snackbar.make(getActivity().findViewById(R.id.room_activity_coord_layout), "Pour localiser vos amis, je dois avoir accès à votre position gégraphique!", Snackbar.LENGTH_INDEFINITE)
                        .setAction("OK", v -> requestPermissions(deniedPermissions.keySet().toArray(new String[deniedPermissions.size()]), requestCode))
                        .show();
            }
        });
    }


    /**************************************************************
     *
     * GeoQuery DataEventListener
     *
     *************************************************************/

    @Override
    public void onDataEntered(DataSnapshot dataSnapshot, GeoLocation location) {
        Log.d(TAG, "onDataEntered " + dataSnapshot.getValue().toString());
        Log.d(TAG, "onDataEntered " + location.toString());

//        if (!isUserInContacts(dataSnapshot.getKey()))
//            return;
        addMarker(dataSnapshot.getKey(), new LatLng(location.latitude, location.longitude));
    }

    @Override
    public void onDataExited(DataSnapshot dataSnapshot) {
        Log.d(TAG, "onDataExited " + dataSnapshot.getValue().toString());

//        if (!isUserInContacts(dataSnapshot.getKey()))
//            return;

        if (markers.containsKey(dataSnapshot.getKey())) {
            markers.get(dataSnapshot.getKey()).remove();
            markers.remove(dataSnapshot.getKey());
        }
    }

    @Override
    public void onDataMoved(DataSnapshot dataSnapshot, GeoLocation location) {
        Log.d(TAG, "onDataMoved " + dataSnapshot.getValue().toString());
        Log.d(TAG, "onDataMoved " + location.toString());

//        if (!isUserInContacts(dataSnapshot.getKey()))
//            return;

        if (markers.containsKey(dataSnapshot.getKey())) {
            markers.get(dataSnapshot.getKey()).setPosition(new LatLng(location.latitude, location.longitude));
        }
    }

    @Override
    public void onDataChanged(DataSnapshot dataSnapshot, GeoLocation location) {
        Log.d(TAG, "onDataChanged key: " + dataSnapshot.getKey());
        Log.d(TAG, "onDataChanged value: " + dataSnapshot.getValue().toString());
        Log.d(TAG, "onDataChanged location: " + location.toString());

        if (!isUserInContacts(dataSnapshot.getKey()))
            return;

        if (markers.containsKey(dataSnapshot.getKey())) {
            markers.get(dataSnapshot.getKey()).setPosition(new LatLng(location.latitude, location.longitude));
        }
    }

    @Override
    public void onGeoQueryReady() {
        Log.d(TAG, "onGeoQueryReady");
    }

    @Override
    public void onGeoQueryError(DatabaseError error) {
        Log.d(TAG, "onGeoQueryError " + error.getMessage(), error.toException());
    }

    @Override
    public void onCameraMove() {
        final CameraPosition cameraPosition = mGoogleMap.getCameraPosition();
        // Update the search criteria for this geoQuery and the circle on the map
        if (searchCircle != null) {
            LatLng center = cameraPosition.target;
            double radius = zoomLevelToRadius(cameraPosition.zoom);
            this.searchCircle.setCenter(center);
            this.searchCircle.setRadius(radius);

            if (mGeoQuery != null) {
                this.mGeoQuery.setCenter(new GeoLocation(center.latitude, center.longitude));
                // radius in km
                this.mGeoQuery.setRadius(radius / 1000);
            }
        }
    }

    public void addMarker(String userUID, LatLng latLng) {
        FirebaseDatabase.getInstance().getReference(FirebaseRefs.USER_PROFILES_REF(userUID)).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final Profile profile = dataSnapshot.getValue(Profile.class);

                GlideApp.with(FriendsMapFragment.this)
                        .load(profile.getAvatarUrl())
                        .fallback(R.drawable.ic_face)
                        .centerCrop()
                        .circleCrop()
                        .into(new BaseTarget<Drawable>() {
                            @Override
                            public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                                LayoutInflater inflater = (LayoutInflater) getActivity()
                                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                                View v = inflater.inflate(R.layout.gmap_marker_view_circle, null);
                                ImageView imageView = v.findViewById(R.id.map_avatar);
                                imageView.setImageDrawable(resource);
                                Marker marker = mGoogleMap.addMarker(new MarkerOptions()
                                        .position(latLng)
                                        .title(profile.getDisplayName())
                                        .icon(BitmapDescriptorFactory.fromBitmap(MapUtil.loadBitmapFromView(v))));
                                markers.put(userUID, marker);
                            }

                            @Override
                            public void getSize(@NonNull SizeReadyCallback cb) {
                                int size = (int) getResources().getDimension(R.dimen.avatar_size_extra_small);
                                cb.onSizeReady(size, size);
                            }

                            @Override
                            public void removeCallback(@NonNull SizeReadyCallback cb) {

                            }
                        });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, databaseError.getMessage(), databaseError.toException());
            }
        });
    }

    class fetchRegisteredUserAndInit extends AsyncTask<Void, Void, Set<String>> {
        private Context mContext;

        public fetchRegisteredUserAndInit(Context context) {
            mContext = context;
        }

        @Override
        protected Set<String> doInBackground(Void... voids) {
            final AppDatabase db = ((MessengerApplicationContext) mContext.getApplicationContext()).getAppDatabaseInstance();
            final List<ContactEntity> contacts = db.contactDao().getAll();
            final Set<String> registeredUsers = new HashSet<>();
            for (ContactEntity contactEntity : contacts) {
                if (contactEntity.getFirebaseUID() != null && !contactEntity.getFirebaseUID().isEmpty())
                    registeredUsers.add(contactEntity.getFirebaseUID());
            }
            return registeredUsers;
        }

        @Override
        protected void onPostExecute(Set<String> users) {
            Log.d(TAG, "registeredUsers: " + users);
            mRegisteredUsers = users;
            initializeGeoFireQuery();
        }
    }
}
