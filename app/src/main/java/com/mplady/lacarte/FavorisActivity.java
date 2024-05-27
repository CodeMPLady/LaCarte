package com.mplady.lacarte;

import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.graphics.Insets;
import androidx.core.view.GravityCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class FavorisActivity extends AppCompatActivity {

    private Button btnPanneau;
    private Button btnFermer;
    private DrawerLayout drawerLayout;
    private RecyclerView favorisRecView;
    private FavoriRecViewAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //EdgeToEdge.enable(this);
        setContentView(R.layout.activity_favoris);
        setView();

        btnPanneau = findViewById(R.id.btnPanneau);
        btnFermer = findViewById(R.id.btnFermer);
        drawerLayout = findViewById(R.id.main);
        favorisRecView = findViewById(R.id.favorisRecView);
        adapter = new FavoriRecViewAdapter(this);

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



        favorisRecView.setAdapter(adapter);
        favorisRecView.setLayoutManager(new GridLayoutManager(this,2));

        ArrayList<Favori> favoris = new ArrayList<>();
        favoris.add(new Favori(1, "https://benedictelarre.wordpress.com/wp-content/uploads/2016/11/pa280017.jpg?w=1200", "Shin-ya Ramen", "Restaurant traditionnel de ramens au centre-ville de Toulouse"));
        favoris.add(new Favori(2, "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcRBdiNlirY34rzkkar1CXN2DLUagEd0mtmr2A", "Les Cabochards", "Restaurant de viande à Cugnaux"));
        favoris.add(new Favori(3, "https://img.cuisineaz.com/660x660/2016/10/23/i113627-poulet-roti-au-four.webp", "Cocorico", "Restaurant typiquement français à Tournefeuille"));

        adapter.setFavoris(favoris);
    }

    private void setView() {
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_VISIBLE;
        decorView.setSystemUiVisibility(uiOptions);
    }

}