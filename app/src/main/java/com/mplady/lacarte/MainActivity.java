package com.mplady.lacarte;

import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class MainActivity extends AppCompatActivity {
    private ImageView searchIcon;
    private SearchView searchView;
    private ExtendedFloatingActionButton fabFavoris;
    private FloatingActionButton  fabStat, fabAbout;
    private CardView searchCardView;


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
        fabStat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, StatActivity.class);
                startActivity(intent);
            }
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

        fabFavoris.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "Favoris clicked", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @NonNull
    private AlertDialog.Builder aboutBuilder() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("A propos de La Carte");
        builder.setMessage("La Carte vous permet de chercher des lieux, d'accéder à une liste en fonction de leur catégorie et de votre position et de les mettre dans vos favoris afin d'y retourner facilement.\n\n" +
                "Cette application a été développée par Michel PUGLIESE et Naémie CATHALA.\n\n" + "Nous avons également développé un site de cuisine.");
        return builder;
    }

    private void setSearchView() {
        searchIcon.setOnClickListener(v -> toggleSearchView());
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Toast.makeText(MainActivity.this, "Search submitted: " + query, Toast.LENGTH_SHORT).show();
                return false;
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
    }
    private void toggleSearchView() {
        if (searchView.getVisibility() == View.GONE) {
            Animation slideIn = AnimationUtils.loadAnimation(this, R.anim.fade_in);
            searchView.setVisibility(View.VISIBLE);
            searchIcon.setVisibility(View.GONE);
            searchCardView.setVisibility(View.GONE);
            searchView.startAnimation(slideIn);
        } else {
            Animation slideOut = AnimationUtils.loadAnimation(this, R.anim.fade_out);
            searchView.startAnimation(slideOut);
            searchView.setVisibility(View.GONE);
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
        searchView = findViewById(R.id.searchView);
        searchIcon = findViewById(R.id.searchIcon);
        fabFavoris = findViewById(R.id.fabFavoris);
        fabStat = findViewById(R.id.fabStat);
        fabAbout = findViewById(R.id.fabAbout);
        searchCardView = findViewById(R.id.searchCardView);
    }
}