package com.mplady.lacarte.favori;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.mplady.lacarte.R;

import java.util.ArrayList;

public class FavorisActivity extends AppCompatActivity {
    private DrawerLayout drawerLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //EdgeToEdge.enable(this);
        setContentView(R.layout.activity_favoris);
        setView();

        Button btnPanneau = findViewById(R.id.btnPanneau);
        Button btnFermer = findViewById(R.id.btnFermer);
        FloatingActionButton btnFilter = findViewById(R.id.bntFiltre);
        drawerLayout = findViewById(R.id.main);
        RecyclerView favorisRecView = findViewById(R.id.favorisRecView);
        FavoriRecViewAdapter adapter = new FavoriRecViewAdapter(this);
        ArrayList<Favori> favoris = new ArrayList<>();

        btnPanneau.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(GravityCompat.END);
            }
        });

        btnFermer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.closeDrawer(GravityCompat.END);
            }
        });

        btnFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(FavorisActivity.this, "Filtre", Toast.LENGTH_SHORT).show();
            }
        });

        favorisRecView.setAdapter(adapter);
        favorisRecView.setLayoutManager(new GridLayoutManager(this,2));
        setData(favoris);
        adapter.setFavoris(favoris);
    }

    private void setView() {
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_VISIBLE;
        decorView.setSystemUiVisibility(uiOptions);
    }

    private void setData(ArrayList<Favori> favoris) {
        favoris.add(new Favori(1, "https://benedictelarre.wordpress.com/wp-content/uploads/2016/11/pa280017.jpg?w=1200", "Shin-ya Ramen", "Restaurant traditionnel de ramens au centre-ville de Toulouse"));
        favoris.add(new Favori(2, "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcRBdiNlirY34rzkkar1CXN2DLUagEd0mtmr2A", "Les Cabochards", "Restaurant de viande à Cugnaux"));
        favoris.add(new Favori(3, "https://img.cuisineaz.com/660x660/2016/10/23/i113627-poulet-roti-au-four.webp", "Cocorico", "Restaurant typiquement français à Tournefeuille"));

    }

}