package com.mplady.lacarte.searchResult;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.AutocompletePrediction;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.mplady.lacarte.R;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class SearchResultsActivity extends FragmentActivity implements OnMapReadyCallback {

    private String query;
    private boolean isFavorite;
    private TextView nomLieuSearch;
    private TextView categorieLieuSearch;
    private TextView adresseLieuSearch;
    private ImageView imgBtnFavoris;
    private ExtendedFloatingActionButton btnYAller;

    String nameLieuSearch;
    String adresse;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_search_results);
        setView();
        initView();
        initMap();
        setFields(query);

        btnYAller.setOnClickListener(v -> {
            Toast.makeText(SearchResultsActivity.this, "Vous allez vous rendre à " + nameLieuSearch, Toast.LENGTH_SHORT).show();
        });

        isFavorite = false;
        imgBtnFavoris.setOnClickListener(v -> {
            if (!isFavorite) {
                imgBtnFavoris.setImageResource(R.drawable.starfillorange);
                isFavorite = true;
                Toast.makeText(SearchResultsActivity.this, nameLieuSearch + " ajouté aux favoris !", Toast.LENGTH_SHORT).show();
            } else {
                imgBtnFavoris.setImageResource(R.drawable.staremptyorange);
                isFavorite = false;
                Toast.makeText(SearchResultsActivity.this, nameLieuSearch + " retiré des favoris !", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setFields(String query) {
        PlacesClient placesClient = Places.createClient(getApplicationContext());
        List<Place.Field> fields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.ADDRESS);
        FindAutocompletePredictionsRequest request = FindAutocompletePredictionsRequest.builder()
                .setQuery(query)
                .build();
        placesClient.findAutocompletePredictions(request).addOnSuccessListener((response) -> {
            for (AutocompletePrediction prediction : response.getAutocompletePredictions()) {
                String placeId = prediction.getPlaceId();
                FetchPlaceRequest requests = FetchPlaceRequest.builder(placeId, fields).build();

                placesClient.fetchPlace(requests).addOnSuccessListener((responses) -> {
                    Place place = responses.getPlace();
                    nameLieuSearch = place.getName();
                    nomLieuSearch.setText(nameLieuSearch);
                    adresse = place.getAddress();
                    adresseLieuSearch.setText(adresse);
                }).addOnFailureListener((exception) -> {
                    nomLieuSearch.setText("Erreur");
                    System.out.println("Error fetching place: " + exception.getMessage());
                });
            }
        });

    }

    private void initMap() {
        Intent intent = getIntent();
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        query = intent.getStringExtra("search_query");
        assert mapFragment != null;
        mapFragment.getMapAsync(this);
    }

    private void initView() {
        nomLieuSearch = findViewById(R.id.nomLieuSearch);
        adresseLieuSearch = findViewById(R.id.adresseLieuSearch);
        imgBtnFavoris = findViewById(R.id.imageBtnFavoris);
        btnYAller = findViewById(R.id.btnYAller);
    }

    private void setView() {
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());

        try {
            List<Address> addresses = geocoder.getFromLocationName(query, 1);
            if (addresses != null && !addresses.isEmpty()) {
                Address address = addresses.get(0);
                LatLng location = new LatLng(address.getLatitude(), address.getLongitude());

                googleMap.addMarker(new MarkerOptions().position(location).title(query));
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 15));


            } else {
                System.out.println("No address found for location: " + query);
            }
        } catch (IOException e) {
            System.out.println("Geocoder error: " + e.getMessage());
        }
    }
}