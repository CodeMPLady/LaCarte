package com.mplady.lacarte.favori;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import androidx.appcompat.app.AlertDialog;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.mplady.lacarte.R;

import java.util.ArrayList;
import java.util.Objects;

public class FavorisActivity extends AppCompatActivity {
    private DrawerLayout drawerLayout;
    boolean[] filter = new boolean[3];
    ArrayList<Favori> favoris = new ArrayList<>();
    RecyclerView favorisRecView;


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
        FavoriRecViewAdapter adapter = new FavoriRecViewAdapter(this);
        setData(favoris);


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
                showDialog();
            }
        });

        favorisRecView.setAdapter(adapter);
        favorisRecView.setLayoutManager(new GridLayoutManager(this,2));
        adapter.setFavoris(favoris);
    }

    private void showDialog() {
        LayoutInflater inflater = LayoutInflater.from(this);
        View dialogView = inflater.inflate(R.layout.fav_dialog_layout, null);

        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this).setView(dialogView);
        TextView dialogTitle = dialogView.findViewById(R.id.dialogTitle);
        CheckBox checkRestaurant = dialogView.findViewById(R.id.checkRestaurant);
        CheckBox checkSupermarche = dialogView.findViewById(R.id.checkSupermache);
        CheckBox checkMode = dialogView.findViewById(R.id.checkMode);
        Button btnValider = dialogView.findViewById(R.id.btnValider);

        AlertDialog dialog = builder.create();
        btnValider.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isRestaurant = checkRestaurant.isChecked();
                boolean isSupermarche = checkSupermarche.isChecked();
                boolean isMode = checkMode.isChecked();
                if (isRestaurant)
                    filter[0] = true;
                if (isSupermarche)
                    filter[1] = true;
                if (isMode)
                    filter[2] = true;
                dialog.dismiss();
                filtre();
            }
        });
        dialog.show();
    }

    private void filtre() {
        System.out.println("filtre: " + filter[0] + filter[1] + filter[2]);
        for (Favori fav : favoris) {
            String categorie = fav.getCategorie();
            System.out.println(categorie);
            if (filter[0] && categorie.equals("Restaurant")) {
                System.out.println("FILTRER !");
                fav.state = false;
            }
            else if (filter[1] && categorie.equals("Supermarche")) {
                System.out.println("FILTRER !");
                fav.state = true;
            }
            else {
                System.out.println("PAS FILTRER !");
            }
        }


    }
    private void restartActivity() {
        Intent intent = getIntent();
        finish();
        startActivity(intent);
    }

    private void setView() {
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_VISIBLE;
        decorView.setSystemUiVisibility(uiOptions);
    }

    private void setData(ArrayList<Favori> favoris) {
        Favori resto = new Favori(1, "https://benedictelarre.wordpress.com/wp-content/uploads/2016/11/pa280017.jpg?w=1200", "Shin-ya Ramen", "Restaurant");
        resto.state = true;
        favoris.add(resto);
        Favori resto2 = new Favori(2, "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcRBdiNlirY34rzkkar1CXN2DLUagEd0mtmr", "Les Cabochards", "resto");
        resto2.state = true;
        favoris.add(resto2);
        Favori resto3 = new Favori(3, "https://img.cuisineaz.com/660x660/2016/10/23/i113627-poulet-roti-au-four.webp", "Cocorico", "lady");
        resto3.state = true;
        favoris.add(resto3);
       // favoris.add(new Favori(1, "https://benedictelarre.wordpress.com/wp-content/uploads/2016/11/pa280017.jpg?w=1200", "Shin-ya Ramen", "Restaurant"));
      //  favoris.add(new Favori(2, "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcRBdiNlirY34rzkkar1CXN2DLUagEd0mtmr2A", "Les Cabochards", "resto"));
       // favoris.add(new Favori(3, "https://img.cuisineaz.com/660x660/2016/10/23/i113627-poulet-roti-au-four.webp", "Cocorico", "lady"));

    }
}