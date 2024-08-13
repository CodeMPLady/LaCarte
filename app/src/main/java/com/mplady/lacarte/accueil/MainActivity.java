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

import androidx.activity.EdgeToEdge;

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
import com.mplady.lacarte.PlacesClientManager;
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
    private MaterialSwitch switchDarkMode1;

    private boolean isUserScrolling = false;

    private final List<String> suggestionList = new ArrayList<>();
    private PlacesClient placesClient;
    Handler handler;
    Runnable runnable;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        initView();
        animatedBackgroundSearchIcon();
        setSearchView();
        setCarousel();
        setDarkMode();
        manageBtnOnClick();
    }

    /**
     * Initialise les vues de l'activité, le PlaceClient pour les requêtes d'autocomplétion
     * ainsi que les variables des éléments visuels de l'application.
     */
    private void initView() {
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);

        listView = findViewById(R.id.suggestionsListView);
        searchView = findViewById(R.id.searchView);
        searchIcon = findViewById(R.id.searchIcon);
        fabFavoris = findViewById(R.id.fabFavoris);
        fabAbout = findViewById(R.id.fabAbout);
        searchCardView = findViewById(R.id.searchCardView);
        switchDarkMode1 = findViewById(R.id.switchDarkMode1);

        placesClient = PlacesClientManager.getPlacesClient(this);
    }

    /**
     * Lance l'animation du background de l'icone de recherche.
     */
    private void animatedBackgroundSearchIcon() {
        ImageView imageView = findViewById(R.id.searchIcon);
        AnimationDrawable animationDrawable = (AnimationDrawable) imageView.getBackground();
        animationDrawable.setEnterFadeDuration(1500);
        animationDrawable.setExitFadeDuration(1500);
        animationDrawable.start();
    }

    /**
     * Initialise la barre de recherche.
     * Met la barre de recherche sur écoute pour réagir en fonction des actions.
     * Pendant la recherche on met à jour la liste des suggestions tout en n'autorisant que les recherches en France.
     * Pour l'envoie on tranfert à SearchResultActivity la recherche (query)
     * pour qu'il puisse etre chercher de nouveau seulement si on clique sur un item.
     * Une fois searchResultActivity lancé on réinitialise la barre de recherche et MainActivity
     */
    private void setSearchView() {
        adapter = new ArrayAdapter<>(this, R.layout.list_item_suggestions, suggestionList);
        listView.setAdapter(adapter);
        searchIcon.setOnClickListener(v -> toggleSearchView());
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchView.clearFocus();
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
            searchView.setQuery("", false);
            searchView.clearFocus();
            recreate();
        });
    }

    /**
     * Permet de faire défiler la barre de recherche.
     */
    private void toggleSearchView() {
        Animation slideIn = AnimationUtils.loadAnimation(this, R.anim.fade_in);
        searchIcon.setVisibility(View.GONE);
        searchCardView.setVisibility(View.GONE);
        searchView.setVisibility(View.VISIBLE);
        listView.setVisibility(View.VISIBLE);
        searchView.startAnimation(slideIn);

        searchView.setIconified(false);
        searchView.requestFocusFromTouch();
    }

    /**
     * Initialise le carousel avec l'adapter ImageAdapterCarousel.
     * Fais une animation sur un autre thread pour faire défiler le carousel.
     */
    private void setCarousel() {
        RecyclerView recyclerViewCarousel = findViewById(R.id.recyclerCarousel);
        ImageAdapterCarousel adapter = getImageAdapterCarousel();
        recyclerViewCarousel.setAdapter(adapter);

        handler = new Handler();
        runnable = new Runnable() {
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
                    handler.removeCallbacks(runnable);
                }
            }
        });
    }

    /**
     * Initialise les images du carousel.
     * L'ordre des images est important pour le bon affichage.
     */
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

    /**
     * Gère le mode sombre/jour de l'application.
     * Le mode est sauvegardé dans les SharedPreferences sous la variable "isDarkModeOn" qui est un boolean.
     * AppCompatDelegate est utilisé pour changer le mode.
     * L'activity dois absolument extends AppCompatActivity pour que le passage en mode sombre fonctionne.
     */
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
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                editor.putBoolean("isDarkModeOn", true);

            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                editor.putBoolean("isDarkModeOn", false);

            }
            editor.apply();
        });
    }


    /**
     * Gère les boutons "A propos" et "Favoris" de MainActivity.
     * Dans "A propos" créer un dialog pour envoyé l'utilisateur vers l'activité du site Websiste_activity ou non.
     */
    private void manageBtnOnClick() {
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
        builder.setMessage("La Carte vous permet de chercher des lieux et de les ajouter à vos favoris. Elle permet aussi de voir quels sont les endroits situés autour de vous selon une catégorie spécifique.\n\n" +
                "Cette application a été développée par Michel P. et Naémie C.\n\n" + "Nous avons également développé un site de cuisine.");
        return builder;
    }

    /**
     * Arrête l'animation du carousel.
     */
    @Override
    protected void onStop() {
        super.onStop();
        handler.removeCallbacks(runnable);
    }

    /**
     * Arrête l'animation du carousel.
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (handler != null) {
            handler.removeCallbacks(runnable);
        }
    }

}