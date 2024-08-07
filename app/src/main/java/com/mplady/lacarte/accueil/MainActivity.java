package com.mplady.lacarte.accueil;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;

import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.AutocompletePrediction;
import com.google.android.libraries.places.api.model.AutocompleteSessionToken;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.SearchView;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;


import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.materialswitch.MaterialSwitch;
import com.mplady.lacarte.BuildConfig;
import com.mplady.lacarte.favori.FavorisActivity;
import com.mplady.lacarte.R;
import com.mplady.lacarte.searchResult.SearchResultsActivity;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private ImageView searchIcon;
    private SearchView searchView;
    private ExtendedFloatingActionButton fabFavoris;
    private FloatingActionButton fabAbout;
    private CardView searchCardView;
    private ArrayAdapter<String> adapter;
    private ListView listView;
    private final List<String> suggestionList = new ArrayList<>();
    private PlacesClient placesClient;
    private MaterialSwitch switchDarkMode1;
    private boolean isUserScrolling = false;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        setView();
        initView();
        setCarousel();
        setSearchView();
        btnOnClicks();
        animatedBackgroundSearchIcon();
        setDarkMode();

        Places.initialize(getApplicationContext(), BuildConfig.MAPS_API_KEY);
        placesClient = Places.createClient(MainActivity.this);
    }

    private void btnOnClicks() {
        fabAbout.setOnClickListener(v -> {
            AlertDialog.Builder builder = aboutBuilder();
            builder.setNegativeButton("Retour", (dialog, which) -> {});
            builder.setPositiveButton("Voir le site", (dialog, which) -> {
                Intent intent = new Intent(MainActivity.this, Website_activity.class);
                startActivity(intent);
            });
            builder.create().show();
        });

        fabFavoris.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, FavorisActivity.class);
            startActivity(intent);
        });
    }

    private void setSearchView() {
        adapter = new ArrayAdapter<>(this, R.layout.list_item_suggestions, suggestionList);
        listView.setAdapter(adapter);
        searchIcon.setOnClickListener(v -> toggleSearchView());
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Intent intent = new Intent(MainActivity.this, SearchResultsActivity.class);
                intent.putExtra("search_query", query);
                startActivity(intent);
                searchView.clearFocus();
                recreate();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                AutocompleteSessionToken token = AutocompleteSessionToken.newInstance();
                FindAutocompletePredictionsRequest request = FindAutocompletePredictionsRequest.builder()
                        .setSessionToken(token)
                        .setQuery(newText)
                        .setCountries("FR")
                        .build();
                placesClient.findAutocompletePredictions(request).addOnSuccessListener(response -> {
                    suggestionList.clear();
                    for (AutocompletePrediction prediction : response.getAutocompletePredictions()) {
                        suggestionList.add(prediction.getFullText(null).toString());
                        //System.out.println("nom : " + prediction.getFullText(null).toString()  + "type :" + prediction.getTypes());
                        //TODO: récupérer l'le placeID avec prediction.getPlaceId() et l'envoyer pour simplifié le code de SearchResultsActivity
                    }
                    adapter.notifyDataSetChanged();
                }).addOnFailureListener(exception -> System.out.println("Error fetching predictions: " + exception.getMessage()));
                return false;
            }
        });

        listView.setOnItemClickListener((parent, view, position, id) -> {
            String selectedSuggestion = suggestionList.get(position);
            Intent intent = new Intent(MainActivity.this, SearchResultsActivity.class);
            System.out.println("sugg :  " + selectedSuggestion);
            intent.putExtra("search_query", selectedSuggestion);
            startActivity(intent);
            searchView.setQuery("", false);
            searchView.clearFocus();
            recreate();
        });
    }

    private void animatedBackgroundSearchIcon() {
        ImageView imageView = findViewById(R.id.searchIcon);
        AnimationDrawable animationDrawable = (AnimationDrawable) imageView.getBackground();
        animationDrawable.setEnterFadeDuration(1500);
        animationDrawable.setExitFadeDuration(2000);
        animationDrawable.start();
    }

    private void toggleSearchView() {
        if (searchView.getVisibility() == View.GONE) {
            Animation slideIn = AnimationUtils.loadAnimation(this, R.anim.fade_in);
            searchView.setVisibility(View.VISIBLE);
            listView.setVisibility(View.VISIBLE);
            searchIcon.setVisibility(View.GONE);
            searchCardView.setVisibility(View.GONE);
            searchView.startAnimation(slideIn);
        } else {
            Animation slideOut = AnimationUtils.loadAnimation(this, R.anim.fade_out);
            searchView.startAnimation(slideOut);
            searchView.setVisibility(View.GONE);
            listView.setVisibility(View.GONE);
            searchIcon.setVisibility(View.VISIBLE);
            searchIcon.setVisibility(View.VISIBLE);
        }
        searchView.setIconified(false);
        searchView.requestFocusFromTouch();
    }

    private void setCarousel() {
        RecyclerView recyclerViewCarousel = findViewById(R.id.recyclerCarousel);
        ImageAdapterCarousel adapter = getImageAdapterCarousel();
        recyclerViewCarousel.setAdapter(adapter);

        final Handler handler = new Handler();
        final Runnable runnable = new Runnable() {
            int scrollAmount = 2;
            int currentOffset = 0;
            boolean isScrollingForward = true;

            @Override
            public void run() {
                currentOffset += scrollAmount;
                recyclerViewCarousel.scrollBy(scrollAmount, 0);

                if (!isUserScrolling) {
                    if (currentOffset >= (recyclerViewCarousel.computeHorizontalScrollRange() - recyclerViewCarousel.getWidth()) + 1800) {
                        isScrollingForward = false;
                        scrollAmount = -scrollAmount;
                    }
                } else {
                    if (currentOffset <= 0) {
                        isScrollingForward = true;
                        scrollAmount = -scrollAmount;
                    }
                }
                handler.postDelayed(this, 5);
            }
        };
        handler.postDelayed(runnable, 5);

        recyclerViewCarousel.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_DRAGGING || newState == RecyclerView.SCROLL_STATE_SETTLING) {
                    isUserScrolling = true;
                    handler.removeCallbacks(runnable); // Arrêter le défilement automatique
                }
            }
        });
    }

    @NonNull
    private ImageAdapterCarousel getImageAdapterCarousel() {
        int[] imageRessourceIDs = {
                R.drawable.iconerestaurant,
                R.drawable.iconemagasin,
                R.drawable.iconecarburant,
                R.drawable.iconepharmacie,
                R.drawable.iconesupermarche,
                R.drawable.iconeboulangerie,
                R.drawable.iconemusee,
                R.drawable.iconeparc,
                R.drawable.iconecinema,
        };

        return new ImageAdapterCarousel(this, imageRessourceIDs);
    }

    @NonNull
    private AlertDialog.Builder aboutBuilder() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("A propos de La Carte");
        builder.setMessage("La Carte vous permet de chercher des lieux, d'accéder à une liste en fonction de leur catégorie et de votre position et de les mettre dans vos favoris afin d'y retourner facilement.\n\n" +
                "Cette application a été développée par Michel P. et Naémie C.\n\n" + "Nous avons également développé un site de cuisine.");
        return builder;
    }

    private void setDarkMode() {

        SharedPreferences sharedPreferences = getSharedPreferences("sharedPrefs", MODE_PRIVATE);
        final SharedPreferences.Editor editor = sharedPreferences.edit();
        final boolean isDarkModeOn = sharedPreferences.getBoolean("isDarkModeOn",false);

        if (isDarkModeOn)
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        else
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        switchDarkMode1.setChecked(isDarkModeOn);

        switchDarkMode1.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                Toast.makeText(MainActivity.this, "Dark Mode Activé", Toast.LENGTH_SHORT).show();
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                editor.putBoolean("isDarkModeOn", true);

            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                Toast.makeText(MainActivity.this, "Dark Mode Désactivé", Toast.LENGTH_SHORT).show();
                editor.putBoolean("isDarkModeOn", false);

            }
            editor.apply();
            System.out.println("isDarkModeOn : " + isDarkModeOn);
        });
    }

    private void initView() {
        listView = findViewById(R.id.suggestionsListView);
        searchView = findViewById(R.id.searchView);
        searchIcon = findViewById(R.id.searchIcon);
        fabFavoris = findViewById(R.id.fabFavoris);
        fabAbout = findViewById(R.id.fabAbout);
        searchCardView = findViewById(R.id.searchCardView);
        switchDarkMode1 = findViewById(R.id.switchDarkMode1);
    }

    private void setView() {
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);
    }
}