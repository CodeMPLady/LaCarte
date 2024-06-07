package com.mplady.lacarte.accueil;

import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;

import androidx.activity.EdgeToEdge;

import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.AutocompletePrediction;
import com.google.android.libraries.places.api.model.AutocompleteSessionToken;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.mplady.lacarte.BuildConfig;
import com.mplady.lacarte.favori.FavorisActivity;
import com.mplady.lacarte.R;
import com.mplady.lacarte.searchResult.SearchResultsActivity;
import com.mplady.lacarte.stat.StatActivity;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private ImageView searchIcon;
    private SearchView searchView;
    private ExtendedFloatingActionButton fabFavoris;
    private FloatingActionButton  fabStat, fabAbout;
    private CardView searchCardView;
    private ArrayAdapter<String> adapter;
    private ListView listView;
    private final List<String> suggestionList = new ArrayList<>();
    private PlacesClient placesClient;

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

        Places.initialize(getApplicationContext(), BuildConfig.MAPS_API_KEY);
        placesClient = Places.createClient(MainActivity.this);

    }
    private void setView() {
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);
    }

    private void animatedBackgroundSearchIcon() {
        ImageView imageView = findViewById(R.id.searchIcon);
        AnimationDrawable animationDrawable = (AnimationDrawable) imageView.getBackground();
        animationDrawable.setEnterFadeDuration(1500);
        animationDrawable.setExitFadeDuration(2000);
        animationDrawable.start();
    }

    private void btnOnClicks() {
        fabStat.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, StatActivity.class);
            startActivity(intent);
        });

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

    @NonNull
    private AlertDialog.Builder aboutBuilder() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("A propos de La Carte");
        builder.setMessage("La Carte vous permet de chercher des lieux, d'accéder à une liste en fonction de leur catégorie et de votre position et de les mettre dans vos favoris afin d'y retourner facilement.\n\n" +
                "Cette application a été développée par Michel P. et Naémie C.\n\n" + "Nous avons également développé un site de cuisine.");
        return builder;
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
                //TODO: ligne du bas a tester si elle résout le bug
                //searchView.clearFocus();
                recreate();
                return true;
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                AutocompleteSessionToken token = AutocompleteSessionToken.newInstance();
                FindAutocompletePredictionsRequest request = FindAutocompletePredictionsRequest.builder()
                        .setSessionToken(token)
                        .setQuery(newText)
                        .setCountries("FR")
                //        .setTypesFilter(Collections.singletonList(PlaceTypes.CITIES))
                        .build();
                placesClient.findAutocompletePredictions(request).addOnSuccessListener(response -> {
                    suggestionList.clear();
                    for (AutocompletePrediction prediction : response.getAutocompletePredictions()) {
                        suggestionList.add(prediction.getFullText(null).toString());
                        System.out.println("SSSS" + prediction.getTypes());
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
            intent.putExtra("search_query", selectedSuggestion);
            startActivity(intent);
            recreate();
        });
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
    }

    @NonNull
    private ImageAdapterCarousel getImageAdapterCarousel() {
        int[] imageRessourceIDs = {
                R.drawable.iconerestaurant,
                R.drawable.iconemode,
                R.drawable.iconecarburant,
                R.drawable.iconepharmacie,
                R.drawable.iconesupermache
        };
        return new ImageAdapterCarousel(this, imageRessourceIDs);
    }

    private void initView() {
        listView = findViewById(R.id.suggestionsListView);
        searchView = findViewById(R.id.searchView);
        searchIcon = findViewById(R.id.searchIcon);
        fabFavoris = findViewById(R.id.fabFavoris);
        fabStat = findViewById(R.id.fabStat);
        fabAbout = findViewById(R.id.fabAbout);
        searchCardView = findViewById(R.id.searchCardView);
    }
}