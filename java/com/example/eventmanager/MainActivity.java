package com.example.eventmanager;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.view.View;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {

    private SearchView mapSearchView;
    private GoogleMap myMap;
    private String locationName;
    private double latitude, longitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mapSearchView = findViewById(R.id.map1);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this); // Initialize the map asynchronously

        mapSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String s) {
                locationName = mapSearchView.getQuery().toString();
                List<Address> addressList = null;

                if (locationName!= null &&!locationName.equals("")) { // Check if the location string is not null or empty
                    Geocoder geocoder = new Geocoder(MainActivity.this, Locale.getDefault());
                    try {
                        addressList = geocoder.getFromLocationName(locationName, 1);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    if (addressList!= null && addressList.size() > 0) {
                        Address address = addressList.get(0);
                        latitude = address.getLatitude();
                        longitude = address.getLongitude();
                        LatLng latLng = new LatLng(latitude, longitude);
                        myMap.addMarker(new MarkerOptions().position(latLng).title(locationName));
                        myMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10));

                        // Save the location data to Firebase
                        saveLocationData(locationName, latitude, longitude);
                    } else {
                        Toast.makeText(MainActivity.this, "No results found", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(MainActivity.this, "Please enter a location", Toast.LENGTH_SHORT).show();
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        myMap = googleMap;
        myMap.getUiSettings().setZoomControlsEnabled(true);
        myMap.getUiSettings().setCompassEnabled(true);

        LatLng latLng = new LatLng(0, 0);
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.title("My Position");
        markerOptions.position(latLng);
        myMap.addMarker(markerOptions);
    }

    public void saveLocation(View view) {
        if (locationName!= null &&!locationName.equals("") && latitude!= 0 && longitude!= 0) {
            // Move to CalendarActivity with location data
            moveToCalendarActivity(locationName, latitude, longitude);
        } else {
            Toast.makeText(this, "Please search for a location first", Toast.LENGTH_SHORT).show();
        }
    }

    private void moveToCalendarActivity(String location, double latitude, double longitude) {
        Intent intent = new Intent(MainActivity.this, CalendarActivity.class);
        intent.putExtra("location", location);
        intent.putExtra("latitude", latitude);
        intent.putExtra("longitude", longitude);
        startActivity(intent);
    }

    private void saveLocationData(String location, double latitude, double longitude) {
        // Generate a unique key for the location data
        String key = FirebaseDatabase.getInstance().getReference("locations").push().getKey();
        if (key != null) {
            // Save the location data to Firebase
            Location locationObj = new Location(location, latitude, longitude);
            FirebaseDatabase.getInstance().getReference("locations").child(key).setValue(locationObj);
        }
    }
}