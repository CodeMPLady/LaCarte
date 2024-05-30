package com.mplady.lacarte.favori;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import androidx.appcompat.app.AlertDialog;
import android.widget.Button;
import android.widget.CheckBox;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.mplady.lacarte.R;

import java.util.ArrayList;

public class FavorisActivity extends AppCompatActivity {
    private DrawerLayout drawerLayout;
    boolean[] filter = new boolean[3];
    ArrayList<Favori> favoris = new ArrayList<>();
    ArrayList<Favori> filteredFavoris = new ArrayList<>();
    RecyclerView favorisRecView;
    FavoriRecViewAdapter adapter;

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
        favorisRecView = findViewById(R.id.favorisRecView);
        adapter = new FavoriRecViewAdapter(this);
        setData(favoris);


        btnPanneau.setOnClickListener(v -> drawerLayout.openDrawer(GravityCompat.END));
        btnFermer.setOnClickListener(v -> drawerLayout.closeDrawer(GravityCompat.END));
        btnFilter.setOnClickListener(v -> {
            favoris.clear();
            setData(favoris);
            showDialog();
        });

        favorisRecView.setAdapter(adapter);
        favorisRecView.setLayoutManager(new GridLayoutManager(this,2));
        adapter.setFavoris(favoris);
    }

    private void showDialog() {
        LayoutInflater inflater = LayoutInflater.from(this);
        View dialogView = inflater.inflate(R.layout.fav_dialog_layout, null);

        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this).setView(dialogView);
        CheckBox checkRestaurant = dialogView.findViewById(R.id.checkRestaurant);
        CheckBox checkSupermarche = dialogView.findViewById(R.id.checkSupermache);
        CheckBox checkMode = dialogView.findViewById(R.id.checkMode);
        FloatingActionButton btnValider = dialogView.findViewById(R.id.btnValider);
        FloatingActionButton btnAnnuler = dialogView.findViewById(R.id.btnAnnuler);

        AlertDialog dialog = builder.create();
        btnAnnuler.setOnClickListener(v -> recreate());
        btnValider.setOnClickListener(v -> {
            boolean isRestaurant = checkRestaurant.isChecked();
            boolean isSupermarche = checkSupermarche.isChecked();
            boolean isMode = checkMode.isChecked();

            filter[0] = isRestaurant;
            filter[1] = isSupermarche;
            filter[2] = isMode;
            filtre();
            dialog.dismiss();
        });
        dialog.show();
    }

    private void filtre() {
        filteredFavoris.clear();
        for (Favori fav : favoris) {
            String categorie = fav.getCategorie();
            System.out.println(fav.getNom());
            if ((filter[0] && categorie.equals("Restaurant")) ||
                    (filter[1] && categorie.equals("Supermarche")) ||
                    (filter[2] && categorie.equals("Mode"))) {
                filteredFavoris.add(fav);
                System.out.println(fav.getNom());
            }
        }
        if (!filter[0] && !filter[1] && !filter[2])
            recreate();
        adapter.updateFavoris(filteredFavoris);
    }
     private void setView() {
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_VISIBLE;
        decorView.setSystemUiVisibility(uiOptions);
    }

    private static void setData(ArrayList<Favori> favoris) {
        favoris.add(new Favori(1, "https://benedictelarre.wordpress.com/wp-content/uploads/2016/11/pa280017.jpg?w=1200", "Shin-ya Ramen", "Restaurant"));
        favoris.add(new Favori(2, "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcRBdiNlirY34rzkkar1CXN2DLUagEd0mt", "PhoSaigon", "Restaurant"));
        favoris.add(new Favori(3, "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcRBdiNlirY34rzkkar1CXN2DLUagEd0mt", "Leclerc", "Supermarche"));
        favoris.add(new Favori(4, "https://img.cuisineaz.com/660x660/2016/10/23/i113627-poulet-roti-au-four.webp", "Carfour City", "Supermarche"));
        favoris.add(new Favori(5, "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcRBdiNlirY34rzkkar1CXN2DLUagEd0mt", "Celio", "Mode"));
        favoris.add(new Favori(6, "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcRBdiNlirY34rzkkar1CXN2DLUagEd", "H&M", "Mode"));
    }
}