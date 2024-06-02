package com.mplady.lacarte.searchResult;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentContainerView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.mplady.lacarte.R;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class SearchResultsActivity extends FragmentActivity implements OnMapReadyCallback {

    private String query;
    GoogleMap gMap;
    FragmentContainerView map;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_search_results);

        Intent intent = getIntent();
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        query = intent.getStringExtra("search_query");
        map = findViewById(R.id.map);

        assert mapFragment != null;
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        this.gMap = googleMap;
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());

        try {
            List<Address> addresses = geocoder.getFromLocationName(query, 1);
            if (addresses != null && !addresses.isEmpty()) {
                Address address = addresses.get(0);
                LatLng location = new LatLng(address.getLatitude(), address.getLongitude());

                this.gMap.addMarker(new MarkerOptions().position(location).title(query));
                this.gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 15));
            } else {
                System.out.println("No address found for location: " + query);
            }
        } catch (IOException e) {
            System.out.println("Geocoder error: " + e.getMessage());
        }
    }
}