package com.mplady.lacarte.favori;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.mplady.lacarte.FavorisDB;
import com.mplady.lacarte.R;
import com.mplady.lacarte.searchResult.SearchResultsActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class FavorisActivity extends AppCompatActivity {
    private DrawerLayout drawerLayout;
    boolean[] filter = new boolean[5];
    ArrayList<Favori> favoris = new ArrayList<>();
    List<Favori> preFilteredFavoris;
    ArrayList<Favori> filteredFavoris = new ArrayList<>();
    RecyclerView favorisRecView;
    FavoriRecViewAdapter adapter;
    private ImageView imgLieuDetails;
    private TextView txtNomLieu, txtTypeLieu, txtAdresseLieu;
    private FloatingActionButton btnFermer, btnFilter, btnSupprimerFavori;
    private ExtendedFloatingActionButton btnYAllerFavori;

    FavorisDB favorisDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favoris);
        setView();
        initView();
        callBackDatabase();
        getFavoriListInBackground();
        //setFavAdapter();

        btnFilter.setOnClickListener(v -> {
            getPreFilteredFavorisInBackground();
            showDialog();
        });
    }

    private void setFavAdapter() {
       // getFavoriListInBackground();
        adapter = new FavoriRecViewAdapter(favoris, this);
        favorisRecView.setAdapter(adapter);
        favorisRecView.setLayoutManager(new GridLayoutManager(this,2));
    }

    void openDrawer(Favori favori) {
        drawerLayout.openDrawer(GravityCompat.END);
        txtNomLieu.setText(favori.getNom());
        txtTypeLieu.setText(favori.getCategorie());
        txtAdresseLieu.setText(favori.getAdresse());

        btnFermer.setOnClickListener(v -> drawerLayout.closeDrawer(GravityCompat.END));
        btnSupprimerFavori.setOnClickListener(v -> {
            deleteFavoriInBackground(favori);
            drawerLayout.closeDrawer(GravityCompat.END);
            Toast.makeText(FavorisActivity.this, favori.getNom() + " supprimé de vos favoris !", Toast.LENGTH_SHORT).show();
            recreate();
        });
        //imgLieuDetails.setImageBitmap(bitmap);
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
            dialog.dismiss();
            filtre();
        });
        dialog.show();
    }

    private void filtre() {
        filteredFavoris.clear();
        assert preFilteredFavoris != null;
        //preFilteredFavoris = favorisDB.getFavoriDAO().getAllFavoris();
        for (Favori fav : preFilteredFavoris) {
            String categorie = fav.getCategorie();
            if ((filter[0] && categorie.equals("Restaurant")) ||
                    (filter[1] && categorie.equals("Station essence")) ||
                    (filter[2] && categorie.equals("Supermarche")) ||
                    (filter[3] && categorie.equals("Pharmacie")) ||
                    (filter[4] && categorie.equals("Magasin"))) {
                filteredFavoris.add(fav);
            }
        }
        if (!filter[0] && !filter[1] && !filter[2] && !filter[3] && !filter[4])
            recreate();
        adapter.updateFavoris(filteredFavoris);
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
        favorisDB = Room.databaseBuilder(getApplicationContext(), FavorisDB.class, "FavorisDB2")
                .addCallback(myCallback)
                .build();
    }

    public void deleteFavoriInBackground(Favori favori) {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());
        executorService.execute(() -> {
            favorisDB.getFavoriDAO().deleteFavori(favori);
            handler.post(() -> {});
        });
    }

    public void getFavoriListInBackground() {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());
        executorService.execute(() -> {
            favoris = (ArrayList<Favori>) favorisDB.getFavoriDAO().getAllFavoris();

            handler.post(() -> {
                Toast.makeText(FavorisActivity.this, "Favoris chargés depuis la BDD", Toast.LENGTH_SHORT).show();
                setFavAdapter();
            });
        });
    }

    private void getPreFilteredFavorisInBackground() {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());
        executorService.execute(() -> {
            try {
                List<Favori> favoriList = favorisDB.getFavoriDAO().getAllFavoris();
                preFilteredFavoris = new ArrayList<>(favoriList);
                handler.post(() -> Toast.makeText(FavorisActivity.this, "Pré-filtrés favoris chargés depuis la BDD", Toast.LENGTH_SHORT).show());
            } catch (Exception e) {
                handler.post(() -> Toast.makeText(FavorisActivity.this, "Erreur lors de la récupération des pré-filtrés favoris", Toast.LENGTH_SHORT).show());
            }
        });
    }

    private void initView() {
        btnFermer = findViewById(R.id.btnFermer);
        btnFilter = findViewById(R.id.bntFiltre);
        drawerLayout = findViewById(R.id.main);
        txtNomLieu = findViewById(R.id.txtNomLieu);
        txtTypeLieu = findViewById(R.id.txtTypeLieu);
        txtAdresseLieu = findViewById(R.id.txtAdresseLieu);
        imgLieuDetails = findViewById(R.id.imgLieuDetails);
        favorisRecView = findViewById(R.id.favorisRecView);
        btnYAllerFavori = findViewById(R.id.btnYAllerFavori);
        btnSupprimerFavori = findViewById(R.id.btnSupprimerFavori);
    }

    private void setView() {
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_VISIBLE;
        decorView.setSystemUiVisibility(uiOptions);
    }
}