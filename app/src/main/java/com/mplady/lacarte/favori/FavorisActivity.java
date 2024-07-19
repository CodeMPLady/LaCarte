package com.mplady.lacarte.favori;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
    private FloatingActionButton btnFermer;
    private FloatingActionButton btnSupprimerFavori;
    private ExtendedFloatingActionButton btnYAllerFavori;

    FavorisDB favorisDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favoris);
        initView();
        callBackDatabase();
        getFavoriListInBackground();
    }

    private void initView() {
        drawerLayout = findViewById(R.id.main);
        btnFermer = findViewById(R.id.btnFermer);
        btnYAllerFavori = findViewById(R.id.btnYAllerFavori);
        btnSupprimerFavori = findViewById(R.id.btnSupprimerFavori);
        imgLieuDetails = findViewById(R.id.imgLieuDetails);
        txtNomLieu = findViewById(R.id.txtNomLieu);
        txtTypeLieu = findViewById(R.id.txtTypeLieu);
        txtAdresseLieu = findViewById(R.id.txtAdresseLieu);

        favorisRecView = findViewById(R.id.favorisRecView);
        adapter = new FavoriRecViewAdapter(favoris, this);
        favorisRecView.setAdapter(adapter);
        favorisRecView.setLayoutManager(new GridLayoutManager(this, 2));

        FloatingActionButton btnFilter = findViewById(R.id.bntFiltre);
        btnFilter.setOnClickListener(v -> showDialog());
    }

    void openDrawer(Favori favori) {
        drawerLayout.openDrawer(GravityCompat.END);
        txtNomLieu.setText(favori.getNom());
        txtTypeLieu.setText(favori.getCategorie());
        txtAdresseLieu.setText(favori.getAdresse());

        if (favori.getBitmap() != null) {
            Bitmap bitmap = BitmapFactory.decodeByteArray(favori.getBitmap(), 0, favori.getBitmap().length);
            imgLieuDetails.setImageBitmap(bitmap);
        } else {
            imgLieuDetails.setImageResource(R.drawable.imgmapsdefaultresized);
        }

        btnFermer.setOnClickListener(v -> drawerLayout.closeDrawer(GravityCompat.END));
        btnSupprimerFavori.setOnClickListener(v -> {
            deleteFavoriInBackground(favori);
            drawerLayout.closeDrawer(GravityCompat.END);
            Toast.makeText(FavorisActivity.this, favori.getNom() + " supprimé de vos favoris !", Toast.LENGTH_SHORT).show();
            getFavoriListInBackground();
        });
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
        dialog.show();

        btnValider.setOnClickListener(v -> {
            filter[0] = checkRestaurant.isChecked();
            filter[1] = checkEssence.isChecked();
            filter[2] = checkSupermarche.isChecked();
            filter[3] = checkPharmacie.isChecked();
            filter[4] = checkMode.isChecked();
            filtre();
            dialog.dismiss();
        });

        btnAnnuler.setOnClickListener(v -> dialog.dismiss());
    }

    private void getFavoriListInBackground() {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());
        executorService.execute(() -> {
            try {
                favoris.clear();
                favoris.addAll(favorisDB.getFavoriDAO().getAllFavoris());
                preFilteredFavoris = new ArrayList<>(favoris);
                handler.post(() -> adapter.setFavoris(favoris));
            } catch (Exception e) {
                handler.post(() -> Toast.makeText(FavorisActivity.this, "Erreur lors de la récupération des favoris", Toast.LENGTH_SHORT).show());
            } finally {
                executorService.shutdown();
            }
        });
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

        favorisDB = Room.databaseBuilder(getApplicationContext(), FavorisDB.class, "FavorisDB")
                .addCallback(myCallback)
                .fallbackToDestructiveMigration()
                .build();
    }

    private void filtre() {
        filteredFavoris.clear();
        for (Favori fav : preFilteredFavoris) {
            String categorie = fav.getCategorie();
            if ((filter[0] && "Restaurant".equals(categorie)) ||
                    (filter[1] && "Station essence".equals(categorie)) ||
                    (filter[2] && "Supermarche".equals(categorie)) ||
                    (filter[3] && "Pharmacie".equals(categorie)) ||
                    (filter[4] && "Magasin".equals(categorie))) {
                filteredFavoris.add(fav);
            }
        }
        if (!filter[0] && !filter[1] && !filter[2] && !filter[3] && !filter[4])
            recreate();
        adapter.updateFavoris(filteredFavoris);
    }

    private void deleteFavoriInBackground(Favori favori) {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(() -> {
            favorisDB.getFavoriDAO().deleteFavori(favori);
            getFavoriListInBackground();
            executorService.shutdown();
        });
    }
}
