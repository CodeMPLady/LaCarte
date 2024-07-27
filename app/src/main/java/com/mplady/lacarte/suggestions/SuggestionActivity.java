package com.mplady.lacarte.suggestions;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
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
import com.mplady.lacarte.FavorisDB;
import com.mplady.lacarte.R;
import com.mplady.lacarte.favori.Favori;
import com.mplady.lacarte.favori.FavorisActivity;
import com.mplady.lacarte.searchResult.SearchResultsActivity;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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
    private Bitmap bitmap, resizedBitmap;

    private DrawerLayout drawerLayoutSuggestions;
    private ImageView imgLieuDetailsSuggestions;
    private TextView txtNomLieuSuggestions, txtAdresseLieuSuggestions;
    private Chip chipTypeLieuSuggestions;
    private FloatingActionButton btnAjouterAuxFavoris;
    private FloatingActionButton btnFermerSuggestions;
    private ExtendedFloatingActionButton btnYAllerSuggestions;
    private FusedLocationProviderClient fusedLocationClient;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private double latitude, longitude;

    FavorisDB favorisDB;
    private List<Favori> favorisList;
    private boolean isFavorite;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_suggestions);
        initViews();
        setLocaltion();
        setTitle();
        setAdapter();
        callBackDatabase();
        getFavoriListInBackground();
        ajouterAuxFavoris();

        Places.initialize(getApplicationContext(), BuildConfig.MAPS_API_KEY);
        placesClientSuggestion = Places.createClient(SuggestionActivity.this);
    }

    private void setLocaltion() {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            getLastLocation();
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }
    }

    private void getLastLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
            return;
        }
        fusedLocationClient.getLastLocation()
                .addOnCompleteListener(this, new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        if (task.isSuccessful() && task.getResult() != null) {
                            Location location = task.getResult();
                            latitude = location.getLatitude();
                            longitude = location.getLongitude();
                            System.out.println("Latitude: " + latitude + ", Longitude: " + longitude);
                            fetchNearbyPlaces(latitude, longitude);
                        } else {
                            System.out.println("Location not found");
                        }
                    }
                });

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLastLocation();
            } else {
                System.out.println("Location permission denied");
            }
        }
    }

    private void setAdapter() {
        adapter = new SuggestionRecViewAdapter(this);
        selectionRecView.setAdapter(adapter);
        selectionRecView.setLayoutManager(new LinearLayoutManager(this));
    }

    private void fetchNearbyPlaces(double latitudeA, double longitudeA) {
    private void callBackDatabase() {
        RoomDatabase.Callback myCallback = new RoomDatabase.Callback() {
            @Override
            public void onOpen(@NonNull SupportSQLiteDatabase db) {
                super.onOpen(db);
            }

            @Override
            public void onCreate(@NonNull SupportSQLiteDatabase db) {
                super.onCreate(db);
            }
        };

        favorisDB = Room.databaseBuilder(getApplicationContext(), FavorisDB.class, "FavorisDB")
                .addCallback(myCallback)
                .fallbackToDestructiveMigration()
                .build();
    }

    public void getFavoriListInBackground() {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());
        executorService.execute(() -> {
            favorisList = favorisDB.getFavoriDAO().getAllFavoris();
            handler.post(() -> {});
        });
    }

    public void addFavoriInBackground(Favori favori) {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());
        executorService.execute(() -> {
            favorisDB.getFavoriDAO().addFavori(favori);
            handler.post(() -> {});
        });
    }

    public void deleteFavoriInBackground(Favori favori) {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());
        executorService.execute(() -> {
            favorisDB.getFavoriDAO().deleteFavori(favori);
            handler.post(() -> {});
        });
    }

    private void ajouterAuxFavoris() {
        isFavorite = false;
        btnAjouterAuxFavoris.setOnClickListener(v -> {
            if (!isFavorite) {
                String nomLieu = txtNomLieuSuggestions.getText().toString();
                String categorieLieu = chipTypeLieuSuggestions.getText().toString();
                String adresseLieu = txtAdresseLieuSuggestions.getText().toString();

                byte[] bitmapData = convertBitmapToByteArray(resizedBitmap);

                Favori favori1 = new Favori(nomLieu, categorieLieu, bitmapData, adresseLieu);
                addFavoriInBackground(favori1);

                btnAjouterAuxFavoris.setImageResource(R.drawable.bookmarkfill);
                isFavorite = true;
                Toast.makeText(SuggestionActivity.this, nomLieu + " ajouté aux favoris !", Toast.LENGTH_SHORT).show();
            } else {
                for(Favori favori : favorisList){
                    if(favori.getNom().equals(txtNomLieuSuggestions.getText().toString())){
                        String nomLieu = txtNomLieuSuggestions.getText().toString();
                        String categorieLieu = chipTypeLieuSuggestions.getText().toString();
                        String adresseLieu = txtAdresseLieuSuggestions.getText().toString();

                        byte[] bitmapData = convertBitmapToByteArray(resizedBitmap);

                        Favori favori1 = new Favori(nomLieu, categorieLieu, bitmapData, adresseLieu);
                        deleteFavoriInBackground(favori1);
                        break;
                    }
                }
                btnAjouterAuxFavoris.setImageResource(R.drawable.bookmarkempty);
                isFavorite = false;
                Toast.makeText(SuggestionActivity.this, txtNomLieuSuggestions.getText().toString() + " retiré des favoris !", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public byte[] convertBitmapToByteArray(Bitmap bitmap) {
        if (bitmap == null) {
            return null;
        }
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream); // Utilisez PNG ou JPEG selon vos besoins
        return outputStream.toByteArray();
    }

    private void fetchNearbyPlaces() {
        final List<Place.Field> placeFields = Arrays.asList(
                Place.Field.ID, Place.Field.NAME, Place.Field.ADDRESS,
                Place.Field.TYPES, Place.Field.PHOTO_METADATAS);

        LatLng center = new LatLng(latitudeA, longitudeA);
        CircularBounds circle = CircularBounds.newInstance(center, 5000);
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
                .addOnFailureListener(response -> System.out.println("Erreur :" + response));
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
        }).addOnFailureListener((exception) -> Log.e("SuggestionActivity", "Error fetching photo", exception));
        return bitmapData[0];
    }

    @SuppressLint("SetTextI18n")
    private void setTitle() {
        Intent intent = getIntent();
        int position = intent.getIntExtra("position", 0);
        textTypeTitle.setText(tableauSelectionCategories[position] + " autour de vous");

        selectionFAB.setOnClickListener(v -> showDialog());
    }

    void openDrawer(Favori suggestion) {
        drawerLayoutSuggestions.openDrawer(GravityCompat.END);
        txtNomLieuSuggestions.setText(suggestion.getNom());
        chipTypeLieuSuggestions.setText(suggestion.getCategorie());
        txtAdresseLieuSuggestions.setText(suggestion.getAdresse());
        imgLieuDetailsSuggestions.setImageResource(R.drawable.imgmapsdefaultresized);

        btnYAllerSuggestions.setOnClickListener(v -> openGoogleMaps(suggestion.getNom()));

        btnFermerSuggestions.setOnClickListener(v -> drawerLayoutSuggestions.closeDrawer(GravityCompat.END));

//        if (suggestion.getBitmap() != null) {
//            Bitmap bitmap = BitmapFactory.decodeByteArray(suggestion.getBitmap(), 0, suggestion.getBitmap().length);
//            imgLieuDetailsSuggestions.setImageBitmap(bitmap);
//        } else
//            imgLieuDetailsSuggestions.setImageResource(R.drawable.imgmapsdefaultresized);

        String nomLieu = txtNomLieuSuggestions.getText().toString();
        getFavoriListInBackground();
        for(Favori favori : favorisList) {
            if (favori.getNom().equals(nomLieu)) {
                btnAjouterAuxFavoris.setImageResource(R.drawable.bookmarkfill);
                isFavorite = true;
                break;
            } else {
                btnAjouterAuxFavoris.setImageResource(R.drawable.bookmarkempty);
                isFavorite = false;
            }
        }
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

    private void openGoogleMaps(String lieu) {
        Uri gmmIntentUri = Uri.parse("geo:0,0?q=" + lieu);
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        mapIntent.setPackage("com.google.android.apps.maps");
        if (mapIntent.resolveActivity(getPackageManager()) != null) {
            startActivity(mapIntent);
        } else {
            Uri webIntentUri = Uri.parse("https://www.google.com/maps/search/?api=1&query=" + lieu);
            Intent webIntent = new Intent(Intent.ACTION_VIEW, webIntentUri);
            startActivity(webIntent);
        }
    }

    private void initViews() {
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_VISIBLE;
        decorView.setSystemUiVisibility(uiOptions);

        textTypeTitle = findViewById(R.id.textTypeTitle);
        selectionFAB = findViewById(R.id.selectionFiltresFAB);
        selectionRecView = findViewById(R.id.selectionRecView);

        drawerLayoutSuggestions = findViewById(R.id.mainSuggestions);
        btnFermerSuggestions = findViewById(R.id.btnFermerSuggestions);
        btnYAllerSuggestions = findViewById(R.id.btnYAllerSuggestions);
        btnAjouterAuxFavoris = findViewById(R.id.btnAjouterAuxFavoris);
        imgLieuDetailsSuggestions = findViewById(R.id.imgLieuDetailsSuggestions);
        txtNomLieuSuggestions = findViewById(R.id.txtNomLieuSuggestions);
        txtAdresseLieuSuggestions = findViewById(R.id.txtAdresseLieuSuggestions);
        chipTypeLieuSuggestions = findViewById(R.id.chipTypeLieuSuggestions);
    }
}
