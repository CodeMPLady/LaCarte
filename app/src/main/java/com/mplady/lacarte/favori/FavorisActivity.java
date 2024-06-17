package com.mplady.lacarte.favori;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import androidx.appcompat.app.AlertDialog;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.mplady.lacarte.R;

import java.util.ArrayList;

public class FavorisActivity extends AppCompatActivity {
    private DrawerLayout drawerLayout;
    boolean[] filter = new boolean[5];
    ArrayList<Favori> favoris = new ArrayList<>();
    ArrayList<Favori> filteredFavoris = new ArrayList<>();
    RecyclerView favorisRecView;
    FavoriRecViewAdapter adapter;
    private ImageView imgLieuDetails;
    private TextView txtNomLieu, txtTypeLieu;
    private Button btnFermer;
    private FloatingActionButton btnFilter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //EdgeToEdge.enable(this);
        setContentView(R.layout.activity_favoris);
        setView();
        initView();
        setFavAdapter();

        btnFermer.setOnClickListener(v -> drawerLayout.closeDrawer(GravityCompat.END));
        btnFilter.setOnClickListener(v -> {
            favoris.clear();
            setData(favoris);
            showDialog();
        });
    }

    private void setFavAdapter() {
        adapter = new FavoriRecViewAdapter(favoris, this, this);
        setData(favoris);
        favorisRecView.setAdapter(adapter);
        favorisRecView.setLayoutManager(new GridLayoutManager(this,2));
        adapter.setFavoris(favoris);
    }

    void openDrawer(String nom, String categorie, String imfURL) {
        drawerLayout.openDrawer(GravityCompat.END);
        txtNomLieu.setText(nom);
        txtTypeLieu.setText(categorie);
        Glide.with(this).load(imfURL).into(imgLieuDetails);
    }

    private void showDialog() {
        LayoutInflater inflater = LayoutInflater.from(this);
        View dialogView = inflater.inflate(R.layout.fav_dialog_layout, null);

        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this).setView(dialogView);

        CheckBox checkRestaurant = dialogView.findViewById(R.id.checkRestaurant);
        CheckBox checkEssence = dialogView.findViewById(R.id.checkEssence);
        CheckBox checkSupermarche = dialogView.findViewById(R.id.checkSupermache);
        CheckBox checkPharmacie = dialogView.findViewById(R.id.checkPharmacie);
        CheckBox checkMode = dialogView.findViewById(R.id.checkMode);

        FloatingActionButton btnValider = dialogView.findViewById(R.id.btnValider);
        FloatingActionButton btnAnnuler = dialogView.findViewById(R.id.btnAnnuler);

        AlertDialog dialog = builder.create();
        btnAnnuler.setOnClickListener(v -> dialog.dismiss());
        btnValider.setOnClickListener(v -> {
            boolean isRestaurant = checkRestaurant.isChecked();
            boolean isEssence = checkEssence.isChecked();
            boolean isSupermarche = checkSupermarche.isChecked();
            boolean isPharmacie = checkPharmacie.isChecked();
            boolean isMode = checkMode.isChecked();

            filter[0] = isRestaurant;
            filter[1] = isEssence;
            filter[2] = isSupermarche;
            filter[3] = isPharmacie;
            filter[4] = isMode;
            filtre();
            dialog.dismiss();
        });
        dialog.show();
    }

    private void filtre() {
        filteredFavoris.clear();
        for (Favori fav : favoris) {
            String categorie = fav.getCategorie();
            if ((filter[0] && categorie.equals("Restaurant")) ||
                    (filter[1] && categorie.equals("Station essence")) ||
                    (filter[2] && categorie.equals("Supermarche")) ||
                    (filter[3] && categorie.equals("Pharmacie")) ||
                    (filter[4] && categorie.equals("Mode"))) {
                filteredFavoris.add(fav);
            }
        }
        if (!filter[0] && !filter[1] && !filter[2] && !filter[3] && !filter[4])
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
        favoris.add(new Favori(2, "https://benedictelarre.wordpress.com/wp-content/uploads/2016/11/pa280017.jpg?w=1200", "Leclerc", "Supermarche"));
        favoris.add(new Favori(3, "https://img.cuisineaz.com/660x660/2016/10/23/i113627-poulet-roti-au-four.webp", "Celio", "Mode"));
        favoris.add(new Favori(4, "https://img.cuisineaz.com/660x660/2016/10/23/i113627-poulet-roti-au-four.webp", "Total", "Station essence"));
        favoris.add(new Favori(5, "https://img.cuisineaz.com/660x660/2016/10/23/i113627-poulet-roti-au-four.webp", "Pharmacie de Brienne", "Pharmacie"));
    }

    private void initView() {
        btnFermer = findViewById(R.id.btnFermer);
        btnFilter = findViewById(R.id.bntFiltre);
        drawerLayout = findViewById(R.id.main);
        txtNomLieu = findViewById(R.id.txtNomLieu);
        txtTypeLieu = findViewById(R.id.txtTypeLieu);
        imgLieuDetails = findViewById(R.id.imgLieuDetails);
        favorisRecView = findViewById(R.id.favorisRecView);
    }
}