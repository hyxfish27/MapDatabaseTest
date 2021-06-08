package com.exercise.mapdatabasetest;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.FragmentActivity;

import com.exercise.mapdatabasetest.Model.DAUPlace;
import com.exercise.mapdatabasetest.Model.Place;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback,
        GoogleMap.OnMapLongClickListener, GoogleMap.OnMarkerClickListener {

    private static final String TAG = "MapsActivity";

    private final HashMap<String, Marker> markers = new HashMap<>();

    private GoogleMap mMap;
    View mapView;
    private Geocoder geocoder;

    private final int ACCESS_LOCATION_REQUEST_CODE = 10001;
    FusedLocationProviderClient fusedLocationProviderClient;

    DAUPlace dauPlace = new DAUPlace();
    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    DatabaseReference databaseReference = firebaseDatabase.getReference("Place");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_maps);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);

        assert mapFragment != null;
        mapView = mapFragment.getView();
        mapFragment.getMapAsync(this);
        geocoder = new Geocoder(this);
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        //UiSettings uis = mMap.getUiSettings();
        //uis.setMyLocationButtonEnabled(false);
        //mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        GoogleMapOptions googleMapOptions = new GoogleMapOptions();
        googleMapOptions.mapType(GoogleMap.MAP_TYPE_NORMAL)
                .zoomControlsEnabled(false)
                .tiltGesturesEnabled(false)
                .scrollGesturesEnabled(true)
                .rotateGesturesEnabled(true)
                .zoomGesturesEnabled(true)
                .compassEnabled(true);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    public void onMapReady(@NotNull GoogleMap googleMap) {
//        UiSettings uis = mMap.getUiSettings();

        ImageButton myLocationBtn = findViewById(R.id.myLocationBtn);
        myLocationBtn.setOnClickListener(v -> zoomToUserLocation());

        mMap = googleMap;
        mMap.setOnMapLongClickListener(this);
        mMap.setOnMarkerClickListener(this);

//        if (mapView != null && mapView.findViewById(Integer.parseInt("1")) != null) {
//            // Get the button view
//            View locationButton = ((View) mapView.findViewById(Integer.parseInt("1")).getParent()).findViewById(Integer.parseInt("2"));
//            // and next place it, on bottom right (as Google Maps app)
//            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) locationButton.getLayoutParams();
        // position on right bottom
//            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.TRUE);
//            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, 0);
//            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.TRUE);
//            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, 0);
//
//            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_END, 0);
//            layoutParams.addRule(RelativeLayout.ALIGN_END, 0);
//            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
//            layoutParams.setMargins(0, 150, 0, 0);
//            layoutParams.setMarginStart(25);
//            rlp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
//            rlp.addRule(RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.TRUE);
//            rlp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, 0);
//            rlp.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0);
//
//            rlp.addRule(RelativeLayout.ALIGN_PARENT_END, 0);
//            rlp.addRule(RelativeLayout.ALIGN_END, 0);
//            rlp.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
//            rlp.setMargins(30, 0, 0, 40);
//        }

        databaseReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NotNull DataSnapshot dataSnapshot, String s) {
                Place place = dataSnapshot.getValue(Place.class);
                // stash the key in the title, for recall later
                if (place != null) {
                    int activity_type = place.getType();
                    int width = 300;
                    int height = 300;
                    BitmapDrawable bitmapDrawable;

                    switch (activity_type) {
                        case 1:
                            bitmapDrawable = (BitmapDrawable) ResourcesCompat.getDrawable(getResources(),R.drawable.icon_charity_3dmarker,null);
                            break;
                        case 2:
                            bitmapDrawable = (BitmapDrawable)ResourcesCompat.getDrawable(getResources(),R.drawable.icon_course_3dmarker,null);
                            break;
                        case 3:
                            bitmapDrawable = (BitmapDrawable)ResourcesCompat.getDrawable(getResources(),R.drawable.icon_discount_3dmarker,null);
                            break;
                        case 4:
                            bitmapDrawable = (BitmapDrawable)ResourcesCompat.getDrawable(getResources(),R.drawable.icon_leisure_3dmarker,null);
                            break;
                        default:
                            bitmapDrawable = (BitmapDrawable)ResourcesCompat.getDrawable(getResources(),R.drawable.icon_sport_3dmarker,null);
                    }
                    assert bitmapDrawable != null;
                    Bitmap bitmap = bitmapDrawable.getBitmap();
                    Bitmap bitmapMarker = Bitmap.createScaledBitmap(bitmap, width, height, false);

                    Marker myMarker = mMap.addMarker(new MarkerOptions()
                            .position(new LatLng(place.getPlatitude(), place.getPlongitude()))
                            .title(dataSnapshot.getKey())
                            .icon(BitmapDescriptorFactory.fromBitmap(bitmapMarker)));

                    // cache the marker locally
                    markers.put(dataSnapshot.getKey(), myMarker);
                }
            }

            @Override
            public void onChildChanged(@NotNull DataSnapshot dataSnapshot, String s) {
                Place place = dataSnapshot.getValue(Place.class);

                // Move markers on the map if changed on Firebase
                Marker changedMarker = markers.get(dataSnapshot.getKey());
                assert changedMarker != null;
                assert place != null;
                changedMarker.setPosition(new LatLng(place.getPlatitude(), place.getPlongitude()));
            }

            @Override
            public void onChildRemoved(@NotNull DataSnapshot dataSnapshot) {
//                 When markers are removed from
                if (markers.containsKey(dataSnapshot.getKey())) {
                    Marker deadMarker = markers.get(dataSnapshot.getKey());
                    if (deadMarker != null) {
                        Log.d(TAG, "onChildRemoved: ");
                        deadMarker.remove();
                        markers.remove(dataSnapshot.getKey());
                    } else {
                        Log.d(TAG, "deadMarker is null");
                    }
                } else {
                    Log.d(TAG, "onChildRemoved: ERROR");
                }
                Log.d(TAG, "onChildRemoved: Done");
            }

            @Override
            public void onChildMoved(@NotNull DataSnapshot dataSnapshot, String s) {
                // This won't happen to our simple list, but log just in case
                Log.v(TAG, "moved !" + dataSnapshot.getValue());
            }

            @Override
            public void onCancelled(@NotNull DatabaseError databaseError) {
                // Ignore cancelations (but log just in case)
                Log.v(TAG, "canceled!" + databaseError.getMessage());
            }
        });

        /*databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Place place = snapshot.getValue(Place.class);
                    //Log.d(TAG, "onDataChange: " + snapshot.child("platitude").toString());
                    //Log.d(TAG, "onDataChange: " + snapshot.getValue(Place.class));
                    placeList.add(place);
                   // LatLng latLng = new LatLng(snapshot.child("platitude"), snapshot.child("plogitude"));
                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });

        for (Place p : placeList) {
            LatLng latLng = new LatLng(p.getPlatitude(), p.getPlongitude());
            mMap.addMarker(new MarkerOptions().position(latLng).title(latLng.latitude +", "+ latLng.longitude)
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN)));
        }*/


        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED) {
            enableUserLocation();
            zoomToUserLocation();
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    ACCESS_LOCATION_REQUEST_CODE);
        }

        try {
            List<Address> addresses = geocoder.getFromLocationName("abc.xyz", 1);
            if (addresses.size() > 0) {
                Address address = addresses.get(0);
                LatLng tainan = new LatLng(address.getLatitude(), address.getLongitude());
                //Log.d(TAG, "onMapReady: " + address.toString());
                MarkerOptions markerOptions = new MarkerOptions()
                        .position(tainan)
                        .title(address.getLocality());
                mMap.addMarker(markerOptions);
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(tainan, 16));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

//        googleMap.setOnMarkerClickListener(marker -> {
//            dauPlace = new DAUPlace();
//            dauPlace.remove(marker.getTitle()).addOnSuccessListener(new OnSuccessListener<Void>() {
//                @Override
//                public void onSuccess(Void unused) {
//                    //Toast.makeText(MapsActivity.this,"Successfully removed",Toast.LENGTH_SHORT).show();
//                }
//            }).addOnFailureListener(new OnFailureListener() {
//                @Override
//                public void onFailure(@NonNull @NotNull Exception e) {
//                    //Toast.makeText(MapsActivity.this,"Failed delete place...",Toast.LENGTH_SHORT).show();
//                }
//            });
//            return false;
//        });

    }



    private void enableUserLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mMap.setMyLocationEnabled(true);
    }

    private void zoomToUserLocation() {
        //Move camera to user's location
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        Task<Location> locationTask = fusedLocationProviderClient.getLastLocation();
        locationTask.addOnSuccessListener(location -> {
            LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(latLng)
                    .tilt(70)
                    .zoom(16)
                    .build();
            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
//            int status = 0;
//            if (status == 0) {
//                MarkerOptions myPlace = new MarkerOptions()
//                        .position(latLng);
//                mMap.addMarker(myPlace);
//                String mKey =
//                status = 1;
//            }else {
//
//            }

        });
        locationTask.addOnFailureListener(e -> Log.d(TAG, "onFailure: zoomToUserLocation Failed"));

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull @NotNull String[] permissions, @NonNull @NotNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == ACCESS_LOCATION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                enableUserLocation();
                zoomToUserLocation();
            }  //We can show a dialog that permission is not granted

        }
    }

    @Override
    public void onMapLongClick(@NonNull @NotNull LatLng latLng) {
        int type = (int)(Math.random() * 5 + 1);
        Place place = new Place(latLng.latitude, latLng.longitude, type);
        dauPlace.add(place).addOnSuccessListener(unused -> {
            Log.d(TAG, "onSuccess: Add place successfully!!");
            Toast.makeText(MapsActivity.this,"Successfully",Toast.LENGTH_SHORT).show();
        }).addOnFailureListener(e -> Log.d(TAG, "onFailure: Failed to add place..."));
//        mMap.addMarker(new MarkerOptions().position(latLng).title(latLng.latitude +", "+ latLng.longitude).draggable(true)
//                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN)));
    }

    @Override
    public boolean onMarkerClick(@NonNull @NotNull Marker marker) {
        dauPlace = new DAUPlace();
        dauPlace.remove(marker.getTitle())
                .addOnSuccessListener(unused -> Toast.makeText(MapsActivity.this,"Successfully deleted",Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(MapsActivity.this,"Failed delete place...",Toast.LENGTH_SHORT).show());
        return false;
    }
}