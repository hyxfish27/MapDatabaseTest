package com.exercise.mapdatabasetest;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import com.exercise.mapdatabasetest.Model.DAUPlace;
import com.exercise.mapdatabasetest.Model.Place;
import com.exercise.mapdatabasetest.databinding.ActivityMapsBinding;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback,
        GoogleMap.OnMapLongClickListener {

    private static final String TAG = "MapsActivity";

    private HashMap<String, Marker> markers = new HashMap<String, Marker>();

    private GoogleMap mMap;
    private Geocoder geocoder;
    private ActivityMapsBinding binding;

    private int ACCESS_LOCATION_REQUEST_CODE = 10001;
    FusedLocationProviderClient fusedLocationProviderClient;

    DAUPlace dauPlace = new DAUPlace();
    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    DatabaseReference databaseReference = firebaseDatabase.getReference("Place");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        geocoder = new Geocoder(this);
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        mMap.setOnMapLongClickListener(this);

        List<Place> placeList = new ArrayList<>();

        databaseReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Place place = dataSnapshot.getValue(Place.class);

                // stash the key in the title, for recall later

                Marker myMarker = mMap.addMarker(new MarkerOptions()
                        .position(new LatLng(place.getPlatitude(), place.getPlongitude()))
                        .title(dataSnapshot.getKey()));

                // cache the marker locally
                markers.put(dataSnapshot.getKey(), myMarker);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                Place place = dataSnapshot.getValue(Place.class);

                // Move markers on the map if changed on Firebase
                Marker changedMarker = markers.get(dataSnapshot.getKey());
                changedMarker.setPosition(new LatLng(place.getPlatitude(), place.getPlongitude()));
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                // When markers are removed from
                Marker deadMarker = markers.get(dataSnapshot.getKey());
                deadMarker.remove();

                markers.remove(dataSnapshot.getKey());
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                // This won't happen to our simple list, but log just in case
                Log.v(TAG, "moved !" + dataSnapshot.getValue());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
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
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                //We can show user a dialog why this permission is necessary
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        ACCESS_LOCATION_REQUEST_CODE);
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        ACCESS_LOCATION_REQUEST_CODE);
            }
        }

        // Add a marker in Sydney and move the camera
//        LatLng latLng = new LatLng(22.998837,120.226859);
//        MarkerOptions markerOptions = new MarkerOptions().position(latLng).title("Here");
//        mMap.addMarker(markerOptions);
//        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 16);
//        mMap.animateCamera(cameraUpdate);

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
        locationTask.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,18));
            }
        });
        locationTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull @NotNull Exception e) {
                Log.d(TAG, "onFailure: zoomToUserLocation Failed");
            }
        });

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull @NotNull String[] permissions, @NonNull @NotNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == ACCESS_LOCATION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                enableUserLocation();
                zoomToUserLocation();
            } else {
                //We can show a dialog that permission is not granted
            }
        }
    }

    @Override
    public void onMapLongClick(@NonNull @NotNull LatLng latLng) {
        Place place = new Place(latLng.latitude, latLng.longitude);
        dauPlace.add(place).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Log.d(TAG, "onSuccess: Add place successfully!!");
                Toast.makeText(MapsActivity.this,"Successfully",Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull @NotNull Exception e) {
                Log.d(TAG, "onFailure: Failed to add place...");
            }
        });
//        mMap.addMarker(new MarkerOptions().position(latLng).title(latLng.latitude +", "+ latLng.longitude).draggable(true)
//                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN)));
    }
}