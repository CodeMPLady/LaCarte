package com.mplady.lacarte.searchResult;

import android.content.Intent;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.libraries.places.api.model.AutocompletePrediction;
import com.google.android.libraries.places.api.model.AutocompleteSessionToken;
import com.google.android.libraries.places.api.model.PhotoMetadata;
import com.google.android.libraries.places.api.net.FetchPhotoRequest;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.material.chip.Chip;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.mplady.lacarte.FavorisDB;
import com.mplady.lacarte.PlacesClientManager;
import com.mplady.lacarte.R;
import com.mplady.lacarte.Place;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SearchResultsActivity extends AppCompatActivity implements OnMapReadyCallback {
    private String query, adresse, nameLieuSearch, categorie;
    private List<String> listCategories;
    private final String[] tableauCategories = {
            "restaurant",
            "supermarket",
            "gas_station",
            "pharmacy",
            "store",
            "park",
            "bakery",
            "movie_theater",
            "museum"
    };
    private final String[] tableauJolieCategories = {
            "Restaurant",
            "Supermarché",
            "Station essence",
            "Pharmacie",
            "Magasin",
            "Parc",
            "Boulangerie",
            "Cinéma",
            "Musée"
    };
    private boolean isFavorite;
    private TextView nomLieuSearch, adresseLieuSearch;
    private Chip categorieLieuSearch;
    private ImageView placePhoto;
    private FloatingActionButton btnFavoris;
    private SearchView searchViewResults;
    private ListView listView;
    private GoogleMap gMap;
    private ArrayAdapter<String> adapter;
    private final List<String> suggestionList = new ArrayList<>();
    private PlacesClient placesClientResults;
    private Bitmap bitmap, resizedBitmap;
    private FavorisDB favorisDB;
    private List<Place> favorisList;
    private ImageView logoChargement;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_search_results);
        initView();
        callBackDatabase();
        getFavoriListInBackground();
        chargement();
        initMap();
        setFields(query);
        setSearchViewResults();
        ajouterAuxFavoris();
    }

    private void chargement() {
        Animation animationChargement = AnimationUtils.loadAnimation(this, R.anim.pulse);
        logoChargement.startAnimation(animationChargement);
        animationChargement.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {}
            @Override
            public void onAnimationEnd(Animation animation) {
                Animation fadeOutAnimation = AnimationUtils.loadAnimation(SearchResultsActivity.this, R.anim.fade_out);
                logoChargement.startAnimation(fadeOutAnimation);
                fadeOutAnimation.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {}
                    @Override
                    public void onAnimationEnd(Animation animation) {
                        logoChargement.setVisibility(View.GONE);
                    }
                    @Override
                    public void onAnimationRepeat(Animation animation) {}
                });
            }
            @Override
            public void onAnimationRepeat(Animation animation) {}
        });
    }

    private void setSearchViewResults () {
        adapter = new ArrayAdapter<>(this, R.layout.list_item_suggestions, suggestionList);
        listView.setAdapter(adapter);
        searchViewResults.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String newQuery) {
                searchViewResults.clearFocus();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                listView.setVisibility(View.VISIBLE);
                AutocompleteSessionToken token = AutocompleteSessionToken.newInstance();
                FindAutocompletePredictionsRequest request = FindAutocompletePredictionsRequest.builder()
                        .setSessionToken(token)
                        .setQuery(newText)
                        .setCountries("FR")
                        .build();
                placesClientResults.findAutocompletePredictions(request).addOnSuccessListener(response -> {
                    suggestionList.clear();
                    for (AutocompletePrediction prediction : response.getAutocompletePredictions()) {
                        suggestionList.add(prediction.getFullText(null).toString());
                    }
                    adapter.notifyDataSetChanged();
                }).addOnFailureListener(exception -> System.out.println("Error fetching predictions: " + exception.getMessage()));
                return false;
            }
        });

        listView.setOnItemClickListener((parent, view, position, id) -> {
            String newSelectedSuggestion = suggestionList.get(position);
            newMap(newSelectedSuggestion);
            placePhoto.setImageResource(R.drawable.imgmapsdefaultresized);
            setFields(newSelectedSuggestion);
            listView.setVisibility(View.GONE);
            searchViewResults.setQuery("", false);
            searchViewResults.clearFocus();

            for(Place place : favorisList){
                if(place.getNom().equals(nameLieuSearch)){
                    btnFavoris.setImageResource(R.drawable.bookmarkfill);
                    isFavorite = true;
                    break;
                } else {
                    btnFavoris.setImageResource(R.drawable.bookmarkempty);
                    isFavorite = false;
                }
            }
        });
    }

    private void setFields(String query) {
        chargement();
        List<com.google.android.libraries.places.api.model.Place.Field> fields = Arrays.asList(com.google.android.libraries.places.api.model.Place.Field.ID, com.google.android.libraries.places.api.model.Place.Field.NAME, com.google.android.libraries.places.api.model.Place.Field.ADDRESS, com.google.android.libraries.places.api.model.Place.Field.PHOTO_METADATAS, com.google.android.libraries.places.api.model.Place.Field.TYPES);
        FindAutocompletePredictionsRequest request = FindAutocompletePredictionsRequest.builder()
                .setQuery(query)
                .build();
        placesClientResults.findAutocompletePredictions(request).addOnSuccessListener((response) -> {
            for (AutocompletePrediction prediction : response.getAutocompletePredictions()) {
                String placeId = prediction.getPlaceId();
                FetchPlaceRequest requests = FetchPlaceRequest.builder(placeId, fields).build();

                placesClientResults.fetchPlace(requests).addOnSuccessListener((responses) -> {
                    com.google.android.libraries.places.api.model.Place place = responses.getPlace();
                    nameLieuSearch = place.getName();
                    nomLieuSearch.setText(nameLieuSearch);
                    adresse = place.getAddress();
                    adresseLieuSearch.setText(adresse);
                    listCategories = place.getPlaceTypes();
                    assert listCategories != null;
                    categorie = null;
                    selectionCategorie:

                    for (int i = 0; i < tableauCategories.length; i++) {
                        for (int j = 0; j < listCategories.size(); j++) {
                            if (tableauCategories[i].equals(listCategories.get(j))) {
                                categorie = tableauJolieCategories[i];
                                break selectionCategorie;
                            }
                        }
                    }
                    if (categorie == null)
                        categorie = "Sans catégorie";
                    categorieLieuSearch.setText(categorie);

                    List<PhotoMetadata> photoMetadataList = place.getPhotoMetadatas();
                    if (photoMetadataList != null && !photoMetadataList.isEmpty()) {
                        PhotoMetadata photoMetadata = photoMetadataList.get(0);
                        FetchPhotoRequest photoRequest = FetchPhotoRequest.builder(photoMetadata)
                                .setMaxWidth(500)
                                .setMaxHeight(500)
                                .build();
                        placesClientResults.fetchPhoto(photoRequest).addOnSuccessListener((fetchPhotoResponse) -> {
                            bitmap = fetchPhotoResponse.getBitmap();
                            resizedBitmap = Bitmap.createScaledBitmap(bitmap, 500, 500, true);
                            placePhoto.setImageBitmap(resizedBitmap);
                        }).addOnFailureListener((exception) -> System.out.println("Error fetching photo"));
                    }
                    for(Place favori : favorisList){
                        if(favori.getNom().equals(nameLieuSearch)){
                            btnFavoris.setImageResource(R.drawable.bookmarkfill);
                            isFavorite = true;
                            break;
                        } else {
                            btnFavoris.setImageResource(R.drawable.bookmarkempty);
                            isFavorite = false;
                        }
                    }
                }).addOnFailureListener((exception) -> System.out.println("Error fetching place: " + exception.getMessage()));
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

    private void newMap(@NonNull String newQuery) {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocationName(newQuery, 1);
            if (addresses != null && !addresses.isEmpty()) {
                Address address = addresses.get(0);
                LatLng location = new LatLng(address.getLatitude(), address.getLongitude());

                gMap.clear();
                gMap.addMarker(new MarkerOptions().position(location).title(newQuery));
                gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 15));
            } else {
                System.out.println("No address found for location: " + newQuery);
            }
        } catch (IOException e) {
            System.out.println("Geocoder error: " + e.getMessage());
        }
    }

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
                .build();
    }

    private void ajouterAuxFavoris() {
        isFavorite = false;
        btnFavoris.setOnClickListener(v -> {
            if (!isFavorite) {
                String nomLieu = nomLieuSearch.getText().toString();
                String categorieLieu = categorieLieuSearch.getText().toString();
                String adresseLieu = adresseLieuSearch.getText().toString();
                byte[] bitmapData = convertBitmapToByteArray(resizedBitmap);

                Place place1 = new Place(nomLieu, categorieLieu, bitmapData, adresseLieu);
                addFavoriInBackground(place1);

                btnFavoris.setImageResource(R.drawable.bookmarkfill);
                isFavorite = true;
                Toast.makeText(SearchResultsActivity.this, nameLieuSearch + " ajouté aux favoris !", Toast.LENGTH_SHORT).show();
            } else {
                for(Place place : favorisList){
                    if(place.getNom().equals(nameLieuSearch)){
                        String nomLieu = nomLieuSearch.getText().toString();
                        String categorieLieu = categorieLieuSearch.getText().toString();
                        String adresseLieu = adresseLieuSearch.getText().toString();
                        byte[] bitmapData = convertBitmapToByteArray(resizedBitmap);

                        Place place1 = new Place(nomLieu, categorieLieu, bitmapData, adresseLieu);
                        deleteFavoriInBackground(place1);
                        break;
                    }
                }
                btnFavoris.setImageResource(R.drawable.bookmarkempty);
                isFavorite = false;
                Toast.makeText(SearchResultsActivity.this, nameLieuSearch + " retiré des favoris !", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public byte[] convertBitmapToByteArray(Bitmap bitmap) {
        if (bitmap == null) {
            return null;
        }
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
        return outputStream.toByteArray();
    }

    public void deleteFavoriInBackground(Place place) {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());
        executorService.execute(() -> {
            favorisDB.getFavoriDAO().deleteFavori(place);
            handler.post(() -> {});
        });
    }

    public void addFavoriInBackground(Place place) {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());
        executorService.execute(() -> {
            favorisDB.getFavoriDAO().addFavori(place);
            handler.post(() -> {});
        });
    }

    public void getFavoriListInBackground() {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());
        executorService.execute(() -> {
            favorisList = favorisDB.getFavoriDAO().getAllFavoris();
            handler.post(() -> {});
        });
    }

    private void openGoogleMaps() {
        Uri gmmIntentUri = Uri.parse("geo:0,0?q=" + nameLieuSearch);
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        mapIntent.setPackage("com.google.android.apps.maps");
        if (mapIntent.resolveActivity(getPackageManager()) != null) {
            startActivity(mapIntent);
        } else {
            Uri webIntentUri = Uri.parse("https://www.google.com/maps/search/?api=1&query=" + nameLieuSearch);
            Intent webIntent = new Intent(Intent.ACTION_VIEW, webIntentUri);
            startActivity(webIntent);
        }
    }

    private void initView() {
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_VISIBLE;
        decorView.setSystemUiVisibility(uiOptions);

        ExtendedFloatingActionButton btnYAller = findViewById(R.id.btnYAller);
        nomLieuSearch = findViewById(R.id.nomLieuSearch);
        adresseLieuSearch = findViewById(R.id.adresseLieuSearch);
        categorieLieuSearch = findViewById(R.id.categorieLieuSearch);
        btnFavoris = findViewById(R.id.imageBtnFavoris);
        searchViewResults = findViewById(R.id.searchViewResults);
        listView = findViewById(R.id.suggestionsListViewResults);
        placePhoto = findViewById(R.id.placePhoto);
        logoChargement = findViewById(R.id.logoChargement);

        btnYAller.setOnClickListener(v -> {
            Toast.makeText(SearchResultsActivity.this, "Vous allez vous rendre à " + nameLieuSearch, Toast.LENGTH_SHORT).show();
            openGoogleMaps();
        });

        placesClientResults = PlacesClientManager.getPlacesClient(this);
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        this.gMap = googleMap;
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