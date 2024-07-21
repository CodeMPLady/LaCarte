package com.mplady.lacarte.suggestions;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.CircularBounds;
import com.google.android.libraries.places.api.model.PhotoMetadata;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.FetchPhotoRequest;
import com.google.android.libraries.places.api.net.FetchPhotoResponse;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.api.net.SearchNearbyRequest;
import com.google.android.material.chip.Chip;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.mplady.lacarte.BuildConfig;
import com.mplady.lacarte.R;
import com.mplady.lacarte.favori.Favori;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class SuggestionActivity extends AppCompatActivity {

    private TextView textTypeTitle;
    private ExtendedFloatingActionButton selectionFAB;
    private RecyclerView selectionRecView;
    private final String[] tableauSelectionCategories = {
            "Restaurants",
            "Magasins",
            "Stations essence",
            "Pharmacies",
            "Supermarchés"
    };
    private SuggestionRecViewAdapter adapter;
    private PlacesClient placesClientSuggestion;
    private List<Place> placesTrouve = new ArrayList<>();
    private ImageView placePhoto;
    private Bitmap bitmap;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_suggestions);

        initViews();
        setTitle();

        selectionFAB.setOnClickListener(v -> showDialog());

        adapter = new SuggestionRecViewAdapter(this);
        selectionRecView.setAdapter(adapter);
        selectionRecView.setLayoutManager(new LinearLayoutManager(this));

        Places.initialize(getApplicationContext(), BuildConfig.MAPS_API_KEY);
        placesClientSuggestion = Places.createClient(SuggestionActivity.this);

        fetchNearbyPlaces();
    }

    private void fetchNearbyPlaces() {
        final List<Place.Field> placeFields = Arrays.asList(
                Place.Field.ID, Place.Field.NAME, Place.Field.ADDRESS,
                Place.Field.TYPES, Place.Field.PHOTO_METADATAS);

        LatLng center = new LatLng(43.607027, 1.426917);
        CircularBounds circle = CircularBounds.newInstance(center, 1500);
        final List<String> includedTypes = Arrays.asList("restaurant", "cafe");

        final SearchNearbyRequest searchNearbyRequest =
                SearchNearbyRequest.builder(circle, placeFields)
                        .setIncludedTypes(includedTypes)
                        .setMaxResultCount(3)
                        .build();

        placesClientSuggestion.searchNearby(searchNearbyRequest)
                .addOnSuccessListener(response -> {
                    placesTrouve = response.getPlaces();
                    System.out.println("Place trouvé :" + placesTrouve);
                    updateRecyclerView();
                })
                .addOnFailureListener(response -> {
                    System.out.println("Erreur :" + response);
                });
    }

    private void updateRecyclerView() {
        if (placesTrouve != null && placesTrouve.size() >= 3) {
            ArrayList<Favori> places = new ArrayList<>();
            for (Place place : placesTrouve) {
                Favori favori = new Favori(
                        Objects.requireNonNull(place.getName()),
                        Objects.requireNonNull(place.getPlaceTypes()).get(0),
                        photoMetadataToByte(Objects.requireNonNull(place.getPhotoMetadatas()).get(0)),
                        place.getAddress()
                );
                places.add(favori);
            }
            adapter.setSuggestions(places);
        }
    }

    private byte[] photoMetadataToByte(PhotoMetadata photoMetadata) {
        if (photoMetadata == null)
            return null;
        if (placesClientSuggestion == null)
            return null;
        FetchPhotoRequest photoRequest = FetchPhotoRequest.builder(photoMetadata)
                .setMaxWidth(500)
                .setMaxHeight(500)
                .build();

        final byte[][] bitmapData = {null};
        placesClientSuggestion.fetchPhoto(photoRequest).addOnSuccessListener((FetchPhotoResponse fetchPhotoResponse) -> {
            Bitmap bitmap = fetchPhotoResponse.getBitmap();
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
            bitmapData[0] = outputStream.toByteArray();
        }).addOnFailureListener((exception) -> {
            Log.e("SuggestionActivity", "Error fetching photo", exception);
        });
        return bitmapData[0];
    }


    @SuppressLint("SetTextI18n")
    private void setTitle() {
        Intent intent = getIntent();
        int position = intent.getIntExtra("position", 0);
        textTypeTitle.setText(tableauSelectionCategories[position] + " autour de vous");
    }

    private void showDialog() {
        LayoutInflater inflater = LayoutInflater.from(this);
        View dialogView = inflater.inflate(R.layout.suggestions_dialog_layout, null);

        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this).setView(dialogView);

        Chip chipRestaurant = dialogView.findViewById(R.id.chipRestaurant);
        Chip chipSupermarche = dialogView.findViewById(R.id.chipSupermarche);
        Chip chipPharmacie = dialogView.findViewById(R.id.chipPharmacie);
        Chip chipStationEssence = dialogView.findViewById(R.id.chipStationEssence);
        Chip chipMagasin = dialogView.findViewById(R.id.chipMagasin);

        FloatingActionButton btnValiderSelection = dialogView.findViewById(R.id.btnValiderSelection);
        FloatingActionButton btnAnnulerSelection = dialogView.findViewById(R.id.btnAnnulerSelection);

        AlertDialog dialog = builder.create();
        btnAnnulerSelection.setOnClickListener(v -> dialog.dismiss());
        btnValiderSelection.setOnClickListener(v -> {
            // Handle selection logic
            dialog.dismiss();
        });
        dialog.show();
    }

    public byte[] convertBitmapToByteArray(Bitmap bitmap) {
        if (bitmap == null) {
            return null;
        }
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
        return outputStream.toByteArray();
    }

    private void initViews() {
        textTypeTitle = findViewById(R.id.textTypeTitle);
        selectionFAB = findViewById(R.id.selectionFiltresFAB);
        selectionRecView = findViewById(R.id.selectionRecView);
    }
}
