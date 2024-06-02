package com.mplady.lacarte.searchResult;

import android.content.Intent;
import android.os.Bundle;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.mplady.lacarte.R;

public class SearchResultsActivity extends FragmentActivity implements OnMapReadyCallback {

    private TextView textSubmited;
    GoogleMap gMap;
    FrameLayout map;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_search_results);


        textSubmited = findViewById(R.id.textSubmited);
        map = findViewById(R.id.map);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        assert mapFragment != null;
        mapFragment.getMapAsync(this);

        Intent intent = getIntent();
        String query = intent.getStringExtra("search_query");


        // Afficher le texte dans le TextView
        if (query != null) {
            textSubmited.setText(query);
        }
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        this.gMap = googleMap;


        LatLng location = new LatLng( 43.6043, 1.4437);
        this.gMap.addMarker(new MarkerOptions().position(location).title("Toulouse!"));
        this.gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 15));
    }
}