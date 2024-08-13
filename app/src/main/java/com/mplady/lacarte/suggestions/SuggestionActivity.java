package com.mplady.lacarte.suggestions;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
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
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.CircularBounds;
import com.google.android.libraries.places.api.model.PhotoMetadata;
import com.google.android.libraries.places.api.net.FetchPhotoRequest;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.api.net.SearchNearbyRequest;
import com.google.android.material.chip.Chip;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.slider.LabelFormatter;
import com.google.android.material.slider.Slider;
import com.mplady.lacarte.BuildConfig;
import com.mplady.lacarte.FavorisDB;
import com.mplady.lacarte.PlacesClientManager;
import com.mplady.lacarte.R;
import com.mplady.lacarte.Place;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

public class SuggestionActivity extends AppCompatActivity {

    private final String[] tableauSelectionCategoriesTitle = {
            "Restaurants",
            "Magasins",
            "Stations essence",
            "Pharmacies",
            "Supermarchés",
            "Boulangerie",
            "Musee",
            "Parcs",
            "Cinémas"
    };

    private TextView txtNomLieuSuggestions, txtAdresseLieuSuggestions;
    private ImageView logoChargement, imgLieuDetailsSuggestions;
    private Button textTypeTitle, selectionFAB, buttonNoSuggestions, btnFermerSuggestions;
    private ExtendedFloatingActionButton carteDisplay, btnYAllerSuggestions;
    private FloatingActionButton btnAjouterAuxFavoris;
    private SuggestionRecViewAdapter adapter;
    private RecyclerView selectionRecView;
    private DrawerLayout drawerLayoutSuggestions;
    private Chip chipTypeLieuSuggestions;
    private AlertDialog dialogMap;
    private SupportMapFragment mapFragment;
    private FloatingActionButton fermerMap;
    private Slider sliderRecherche;
    private ViewGroup mainContent;

    private int rayonDeRecherche = 500;
    private double latitude, longitude;
    private boolean isFavorite;
    private String categorieTitle, selectedText;
    private String[] tableauTypes, tableauJolisTypes;
    private Bitmap bitmapClassique, resizedBitmap;
    private List<Place> favorisList;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;

    FavorisDB favorisDB;
    private PlacesClient placesClientSuggestion;
    private List<com.google.android.libraries.places.api.model.Place> placesTrouve = new ArrayList<>();
    private FusedLocationProviderClient fusedLocationClient;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_suggestions);
        initViews();
        setupLayoutManager();
        setLocaltion();
        setTitle();
        setAdapter();
        getRadius();
        callBackDatabase();
        getFavoriListInBackground();
        ajouterAuxFavoris();
        openMapsWithPlaces();
    }

    private void getRadius() {
        sliderRecherche.addOnChangeListener((slider, value, fromUser) -> rayonDeRecherche = (int) value);
        sliderRecherche.setLabelFormatter(new LabelFormatter() {
            @NonNull
            @Override
            public String getFormattedValue(float value) {
                int intValue = (int) value;
                if (intValue <= 1000)
                    return intValue + "m";
                else
                    return value/1000 +  "km";
            }
        });

        sliderRecherche.addOnSliderTouchListener(new Slider.OnSliderTouchListener() {
            @Override
            public void onStartTrackingTouch(@NonNull Slider slider) {
            }

            @Override
            public void onStopTrackingTouch(@NonNull Slider slider) {
                fetchNearbyPlaces(latitude, longitude);
            }
        });
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        setupLayoutManager();
    }

    private void setupLayoutManager() {
        LinearLayoutManager layoutManager;
        int orientation = getResources().getConfiguration().orientation;
        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        } else {
            layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        }
        selectionRecView.setLayoutManager(layoutManager);
    }

    private void setAdapter() {
        adapter = new SuggestionRecViewAdapter(this, getTableauTypes(), getTableauJolisTypes());
        selectionRecView.setAdapter(adapter);
    }

    private void fetchNearbyPlaces(double latitudeA, double longitudeA) {
        chargement();
        final List<com.google.android.libraries.places.api.model.Place.Field> placeFields = Arrays.asList(
                com.google.android.libraries.places.api.model.Place.Field.ID, com.google.android.libraries.places.api.model.Place.Field.NAME, com.google.android.libraries.places.api.model.Place.Field.ADDRESS,
                com.google.android.libraries.places.api.model.Place.Field.TYPES, com.google.android.libraries.places.api.model.Place.Field.PHOTO_METADATAS, com.google.android.libraries.places.api.model.Place.Field.PRIMARY_TYPE);

        LatLng center = new LatLng(latitudeA, longitudeA);
        CircularBounds circle = CircularBounds.newInstance(center, rayonDeRecherche);
        final List<String> includedTypes = Collections.singletonList(categorieTitle);

        final SearchNearbyRequest searchNearbyRequest =
                SearchNearbyRequest.builder(circle, placeFields)
                        .setIncludedTypes(includedTypes)
                        .setMaxResultCount(20)
                        .setRankPreference(SearchNearbyRequest.RankPreference.DISTANCE)
                        .build();

        placesClientSuggestion.searchNearby(searchNearbyRequest)
                .addOnSuccessListener(response -> {
                        placesTrouve = response.getPlaces().stream()
                                .filter(place -> Optional.ofNullable(place.getPrimaryType()).map(pt -> pt.contains(categorieTitle)).orElse(false))
                                .collect(Collectors.toList());
                        updateRecyclerView();
                })
                .addOnFailureListener(response -> System.out.println("Erreur :" + response));
    }

    public String[] getTableauTypes() {
        return tableauTypes;
    }

    public String[] getTableauJolisTypes() {
        return tableauJolisTypes;
    }

    @SuppressLint("SetTextI18n")
    private void updateRecyclerView() {
        if (placesTrouve.isEmpty()) {
            buttonNoSuggestions.setVisibility(View.VISIBLE);
            buttonNoSuggestions.setText("Pas de " +  selectedText +" dans ce périmètre");
            ArrayList<Place> placesVide = new ArrayList<>();
            adapter.setSuggestions(placesVide);
        } else {
            buttonNoSuggestions.setVisibility(View.GONE);
            ArrayList<Place> places = new ArrayList<>();
            for (com.google.android.libraries.places.api.model.Place place : placesTrouve) {
                List<PhotoMetadata> photoMetadataList = place.getPhotoMetadatas();
                if (photoMetadataList != null && !photoMetadataList.isEmpty()) {
                    PhotoMetadata photoMetadata = photoMetadataList.get(0);
                    FetchPhotoRequest photoRequest = FetchPhotoRequest.builder(photoMetadata)
                            .setMaxWidth(400)
                            .setMaxHeight(400)
                            .build();
                    placesClientSuggestion.fetchPhoto(photoRequest).addOnSuccessListener((fetchPhotoResponse) -> {
                        bitmapClassique = fetchPhotoResponse.getBitmap();
                        resizedBitmap = Bitmap.createScaledBitmap(bitmapClassique, 400, 400, true);

                        Place favori = new Place(
                                Objects.requireNonNull(place.getName()),
                                categorieTitle,
                                resizedBitmap,
                                place.getAddress(),
                                Objects.requireNonNull(place.getPlaceTypes()).get(0)
                        );
                        places.add(favori);
                        setAdapter();
                        adapter.setSuggestions(places);
                    }).addOnFailureListener((exception) -> System.out.println("Error fetching photo: " + exception.getMessage()));
                }
            }
        }
    }

    void openDrawer(Place suggestion) {
        drawerLayoutSuggestions.openDrawer(GravityCompat.END);
        txtNomLieuSuggestions.setText(suggestion.getNom());

        int i=0;
        while (!suggestion.getCategorie().equals(tableauTypes[i]))
            i++;

        String jolieCat = tableauJolisTypes[i];

        chipTypeLieuSuggestions.setText(jolieCat);
        txtAdresseLieuSuggestions.setText(suggestion.getAdresse());
        if (suggestion.getPhoto() != null)
            imgLieuDetailsSuggestions.setImageBitmap(suggestion.getPhoto());
        else
            imgLieuDetailsSuggestions.setImageResource(R.drawable.imgmapsdefault);

        drawerLayoutSuggestions.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(@NonNull View drawerView, float slideOffset) {
            }

            @Override
            public void onDrawerOpened(@NonNull View drawerView) {
                setEnableRecursively(mainContent, false);
                btnYAllerSuggestions.setOnClickListener(v -> openGoogleMaps(suggestion.getNom()));
                btnFermerSuggestions.setOnClickListener(v -> drawerLayoutSuggestions.closeDrawer(GravityCompat.END));
                String nomLieu = txtNomLieuSuggestions.getText().toString();
                isFavorite = false;
                btnAjouterAuxFavoris.setImageResource(R.drawable.bookmarkempty);
                getFavoriListInBackground();
                for(Place place : favorisList) {
                    if (place.getNom().equals(nomLieu)) {
                        btnAjouterAuxFavoris.setImageResource(R.drawable.bookmarkfill);
                        isFavorite = true;
                        break;
                    } else {
                        btnAjouterAuxFavoris.setImageResource(R.drawable.bookmarkempty);
                        isFavorite = false;
                    }
                }
            }

            @Override
            public void onDrawerClosed(@NonNull View drawerView) {
                setEnableRecursively(mainContent, true);
                selectionRecView.setEnabled(true);
            }

            @Override
            public void onDrawerStateChanged(int newState) {
            }
        });
    }

    private void setEnableRecursively(ViewGroup viewGroup, boolean enable) {
        for (int i = 0; i < viewGroup.getChildCount(); i++) {
            View child = viewGroup.getChildAt(i);
            child.setEnabled(enable);
            if (child instanceof ViewGroup) {
                setEnableRecursively((ViewGroup) child, enable);
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private void showDialog() {
        LayoutInflater inflater = LayoutInflater.from(this);
        View dialogView = inflater.inflate(R.layout.suggestions_dialog_layout, null);

        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this).setView(dialogView);

        FloatingActionButton btnValiderSelection = dialogView.findViewById(R.id.btnValiderSelection);
        FloatingActionButton btnAnnulerSelection = dialogView.findViewById(R.id.btnAnnulerSelection);
        RadioGroup radioGroupDialogSuggestions = dialogView.findViewById(R.id.radioGroupDialogSuggestions);
        RadioGroup radioGroupDialog2 = dialogView.findViewById(R.id.radioGroupDialog2);
        RadioGroup radioGroupDialog3 = dialogView.findViewById(R.id.radioGroupDialog3);

        AlertDialog dialog = builder.create();

        final boolean[] isSettingByCode = {false};

        int orientationDialog = getResources().getConfiguration().orientation;
        if (orientationDialog == Configuration.ORIENTATION_PORTRAIT) {
            setUpDialogPortrait(btnValiderSelection, radioGroupDialogSuggestions,
                    radioGroupDialog2, isSettingByCode,
                    dialogView, dialog);
        } else {
            setUpDialogLandscape(btnValiderSelection, radioGroupDialogSuggestions,
                    radioGroupDialog2, radioGroupDialog3,
                    isSettingByCode, dialogView, dialog);
        }
        btnAnnulerSelection.setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }

    @SuppressLint("SetTextI18n")
    public void setUpDialogPortrait(FloatingActionButton btnValiderSelection, RadioGroup radioGroupDialogSuggestions,
                                    RadioGroup radioGroupDialog2, final boolean[] isSettingByCode,
                                    View dialogView, AlertDialog dialog) {
        radioGroupDialogSuggestions.setOnCheckedChangeListener((group, checkedId) -> {
            if (!isSettingByCode[0]) {
                if (checkedId != -1) {
                    isSettingByCode[0] = true;
                    radioGroupDialog2.clearCheck();
                    isSettingByCode[0] = false;
                }
            }
        });

        radioGroupDialog2.setOnCheckedChangeListener((group, checkedId) -> {
            if (!isSettingByCode[0]) {
                if (checkedId != -1) {
                    isSettingByCode[0] = true;
                    radioGroupDialogSuggestions.clearCheck();
                    isSettingByCode[0] = false;
                }
            }
        });

        btnValiderSelection.setOnClickListener(v -> {
            int selectedIdLeft = radioGroupDialogSuggestions.getCheckedRadioButtonId();
            int selectedIdMiddle = radioGroupDialog2.getCheckedRadioButtonId();

            if (selectedIdLeft != -1) {
                RadioButton selectedRadioButton = dialogView.findViewById(selectedIdLeft);
                selectedText = selectedRadioButton.getText().toString();

                int i=0;
                while (!selectedText.equals(tableauJolisTypes[i])) {
                    i++;
                }
                categorieTitle = tableauTypes[i];
                fetchNearbyPlaces(latitude, longitude);
                textTypeTitle.setText(tableauJolisTypes[i] + " autour de vous");
                updateRecyclerView();
                openMapsWithPlaces();
            }

            if (selectedIdMiddle != -1) {
                RadioButton selectedRadioButton = dialogView.findViewById(selectedIdMiddle);
                selectedText = selectedRadioButton.getText().toString();

                int i=0;
                while (!selectedText.equals(tableauJolisTypes[i])) {
                    i++;
                }
                categorieTitle = tableauTypes[i];
                fetchNearbyPlaces(latitude, longitude);
                textTypeTitle.setText(tableauJolisTypes[i] + " autour de vous");
                updateRecyclerView();
                openMapsWithPlaces();
            }
            dialog.dismiss();
        });
    }

    @SuppressLint("SetTextI18n")
    public void setUpDialogLandscape(FloatingActionButton btnValiderSelection, RadioGroup radioGroupDialogSuggestions,
                                     RadioGroup radioGroupDialog2, RadioGroup radioGroupDialog3,
                                     final boolean[] isSettingByCode, View dialogView, AlertDialog dialog) {
        radioGroupDialogSuggestions.setOnCheckedChangeListener((group, checkedId) -> {
            if (!isSettingByCode[0]) {
                if (checkedId != -1) {
                    isSettingByCode[0] = true;
                    radioGroupDialog2.clearCheck();
                    radioGroupDialog3.clearCheck();
                    isSettingByCode[0] = false;
                }
            }
        });

        radioGroupDialog2.setOnCheckedChangeListener((group, checkedId) -> {
            if (!isSettingByCode[0]) {
                if (checkedId != -1) {
                    isSettingByCode[0] = true;
                    radioGroupDialogSuggestions.clearCheck();
                    radioGroupDialog3.clearCheck();
                    isSettingByCode[0] = false;
                }
            }
        });

        radioGroupDialog3.setOnCheckedChangeListener((group, checkedId) -> {
            if (!isSettingByCode[0]) {
                if (checkedId != -1) {
                    isSettingByCode[0] = true;
                    radioGroupDialogSuggestions.clearCheck();
                    radioGroupDialog2.clearCheck();
                    isSettingByCode[0] = false;
                }
            }
        });

        btnValiderSelection.setOnClickListener(v -> {
            int selectedIdLeft = radioGroupDialogSuggestions.getCheckedRadioButtonId();
            int selectedIdMiddle = radioGroupDialog2.getCheckedRadioButtonId();
            int selectedIdRight = radioGroupDialog3.getCheckedRadioButtonId();

            if (selectedIdLeft != -1) {
                RadioButton selectedRadioButton = dialogView.findViewById(selectedIdLeft);
                selectedText = selectedRadioButton.getText().toString();

                int i=0;
                while (!selectedText.equals(tableauJolisTypes[i])) {
                    i++;
                }
                categorieTitle = tableauTypes[i];
                fetchNearbyPlaces(latitude, longitude);
                textTypeTitle.setText(tableauJolisTypes[i] + " autour de vous");
                updateRecyclerView();
                openMapsWithPlaces();
            }

            if (selectedIdMiddle != -1) {
                RadioButton selectedRadioButton = dialogView.findViewById(selectedIdMiddle);
                selectedText = selectedRadioButton.getText().toString();

                int i=0;
                while (!selectedText.equals(tableauJolisTypes[i])) {
                    i++;
                }
                categorieTitle = tableauTypes[i];
                fetchNearbyPlaces(latitude, longitude);
                textTypeTitle.setText(tableauJolisTypes[i] + " autour de vous");
                updateRecyclerView();
                openMapsWithPlaces();
            }

            if (selectedIdRight != -1) {
                RadioButton selectedRadioButton = dialogView.findViewById(selectedIdRight);
                selectedText = selectedRadioButton.getText().toString();

                int i=0;
                while (!selectedText.equals(tableauJolisTypes[i])) {
                    i++;
                }
                categorieTitle = tableauTypes[i];
                fetchNearbyPlaces(latitude, longitude);
                textTypeTitle.setText(tableauJolisTypes[i] + " autour de vous");
                updateRecyclerView();
                openMapsWithPlaces();
            }
            dialog.dismiss();
        });
    }

    void openMapsWithPlaces() {
        carteDisplay.setOnClickListener(v -> {
            LayoutInflater inflater = LayoutInflater.from(SuggestionActivity.this);
            View dialogView = inflater.inflate(R.layout.suggestion_dialog_map, null);
            MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(SuggestionActivity.this)
                    .setView(dialogView);
            dialogMap = builder.create();
            dialogMap.show();

            mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapSuggestion);
            if (mapFragment == null) {
                mapFragment = SupportMapFragment.newInstance();
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.mapSuggestion, mapFragment)
                        .commit();
            }
            mapFragment.getMapAsync(this::updateMapWithPlaces);

            fermerMap = dialogView.findViewById(R.id.btnFermerMap);
            fermerMap.setOnClickListener(t -> {
                dialogMap.dismiss();
                getSupportFragmentManager().beginTransaction().remove(mapFragment).commit();
            });
            dialogMap.setOnDismissListener(dialog -> {
                SupportMapFragment existingMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapSuggestion);
                if (existingMapFragment != null)
                    getSupportFragmentManager().beginTransaction().remove(existingMapFragment).commit();
            });
        });
    }

    public void updateMapWithPlaces(@NonNull GoogleMap googleMap) {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());

        if (placesTrouve != null && !placesTrouve.isEmpty()) {
            LatLngBounds.Builder boundsBuilder = new LatLngBounds.Builder();

            for (com.google.android.libraries.places.api.model.Place place : placesTrouve) {
                String addressStr = place.getAddress();
                try {
                    assert addressStr != null;
                    List<Address> addresses = geocoder.getFromLocationName(addressStr, 1);
                    if (addresses != null && !addresses.isEmpty()) {
                        Address address = addresses.get(0);
                        LatLng location = new LatLng(address.getLatitude(), address.getLongitude());
                        googleMap.addMarker(new MarkerOptions().position(location).title(place.getName()));
                        boundsBuilder.include(location);
                    } else
                        System.out.println("No address found for: " + addressStr);
                } catch (IOException e) {
                    System.out.println("Geocoder error: " + e.getMessage());
                }
            }
            LatLngBounds bounds = boundsBuilder.build();
            int padding = 100;
            googleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, padding));
        } else
            System.out.println("No places found");
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

    private void chargement() {
        Animation animationChargement = AnimationUtils.loadAnimation(this, R.anim.pulse);
        logoChargement.bringToFront();
        logoChargement.startAnimation(animationChargement);
        animationChargement.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {}
            @Override
            public void onAnimationEnd(Animation animation) {
                Animation fadeOutAnimation = AnimationUtils.loadAnimation(SuggestionActivity.this, R.anim.fade_out);
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

    public void addFavoriInBackground(Place place) {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());
        executorService.execute(() -> {
            favorisDB.getFavoriDAO().addFavori(place);
            handler.post(() -> {});
        });
    }

    public void deleteFavoriInBackground(Place place) {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());
        executorService.execute(() -> {
            favorisDB.getFavoriDAO().deleteFavori(place);
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
                BitmapDrawable draw = (BitmapDrawable) imgLieuDetailsSuggestions.getDrawable();
                byte[] bitmapData = convertBitmapToByteArray(draw.getBitmap());

                Place place1 = new Place(nomLieu, categorieLieu, bitmapData, adresseLieu);
                addFavoriInBackground(place1);

                btnAjouterAuxFavoris.setImageResource(R.drawable.bookmarkfill);
                isFavorite = true;
                Toast.makeText(SuggestionActivity.this, nomLieu + " ajouté aux favoris !", Toast.LENGTH_SHORT).show();
            } else {
                for(Place place : favorisList){
                    if(place.getNom().equals(txtNomLieuSuggestions.getText().toString())){
                        String nomLieu = txtNomLieuSuggestions.getText().toString();
                        String categorieLieu = chipTypeLieuSuggestions.getText().toString();
                        String adresseLieu = txtAdresseLieuSuggestions.getText().toString();
                        byte[] bitmapData = convertBitmapToByteArray(resizedBitmap);

                        Place place1 = new Place(nomLieu, categorieLieu, bitmapData, adresseLieu);
                        deleteFavoriInBackground(place1);
                        break;
                    }
                }
                btnAjouterAuxFavoris.setImageResource(R.drawable.bookmarkempty);
                isFavorite = false;
                Toast.makeText(SuggestionActivity.this, txtNomLieuSuggestions.getText().toString() + " retiré des favoris !", Toast.LENGTH_SHORT).show();
            }
        });
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
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        Location location = task.getResult();
                        latitude = location.getLatitude();
                        longitude = location.getLongitude();
                        fetchNearbyPlaces(latitude, longitude);
                    } else {
                        System.out.println("Location not found");
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

    public byte[] convertBitmapToByteArray(Bitmap bitmap) {
        if (bitmap == null) {
            return null;
        }
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
        return outputStream.toByteArray();
    }

    private void initViews() {
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_VISIBLE;
        decorView.setSystemUiVisibility(uiOptions);

        textTypeTitle = findViewById(R.id.textTypeTitle);
        selectionFAB = findViewById(R.id.selectionFiltresFAB);
        selectionRecView = findViewById(R.id.selectionRecView);
        carteDisplay = findViewById(R.id.mapFAB);

        drawerLayoutSuggestions = findViewById(R.id.mainSuggestions);
        btnFermerSuggestions = findViewById(R.id.btnFermerSuggestions);
        btnYAllerSuggestions = findViewById(R.id.btnYAllerSuggestions);
        btnAjouterAuxFavoris = findViewById(R.id.btnAjouterAuxFavoris);
        imgLieuDetailsSuggestions = findViewById(R.id.imgLieuDetailsSuggestions);
        txtNomLieuSuggestions = findViewById(R.id.txtNomLieuSuggestions);
        txtAdresseLieuSuggestions = findViewById(R.id.txtAdresseLieuSuggestions);
        chipTypeLieuSuggestions = findViewById(R.id.chipTypeLieuSuggestions);
        logoChargement = findViewById(R.id.logoChargementS);
        fermerMap = findViewById(R.id.btnFermerMap);
        sliderRecherche = findViewById(R.id.sliderRecherche);
        mainContent = findViewById(R.id.frameSuggestions);
        buttonNoSuggestions = findViewById(R.id.buttonNoSuggestions);

        Places.initialize(getApplicationContext(), BuildConfig.MAPS_API_KEY);
        placesClientSuggestion = PlacesClientManager.getPlacesClient(this);
    }

    @SuppressLint("SetTextI18n")
    private void setTitle() {
        Intent intent = getIntent();
        int position = intent.getIntExtra("position", 0);
        textTypeTitle.setText(tableauSelectionCategoriesTitle[position] + " autour de vous");
        selectedText = tableauSelectionCategoriesTitle[position];

        Resources res = getResources();
        tableauTypes = res.getStringArray(R.array.tableauTypesGMaps);
        tableauJolisTypes = res.getStringArray(R.array.tableauJolisTypesGMaps);

        categorieTitle = tableauTypes[position];
        selectionFAB.setOnClickListener(v -> showDialog());
    }
}