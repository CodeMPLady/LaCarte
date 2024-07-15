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
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.mplady.lacarte.FavorisDB;
import com.mplady.lacarte.R;
import com.mplady.lacarte.Utils;
import com.mplady.lacarte.searchResult.SearchResultsActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class FavorisActivity extends AppCompatActivity {
    private DrawerLayout drawerLayout;
    boolean[] filter = new boolean[5];
    ArrayList<Favori> favoris = new ArrayList<>();
    ArrayList<Favori> preFilteredFavoris = new ArrayList<>();
    ArrayList<Favori> filteredFavoris = new ArrayList<>();
    RecyclerView favorisRecView;
    FavoriRecViewAdapter adapter;
    private ImageView imgLieuDetails;
    private TextView txtNomLieu, txtTypeLieu;
    private Button btnFermer;
    private FloatingActionButton btnFilter;

    FavorisDB favorisDB;
    List<Favori> favorisList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favoris);

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
                .build();

        setView();
        initView();
        setFavAdapter();
        getFavoriListInBackground();
        Intent intent = new Intent(FavorisActivity.this, SearchResultsActivity.class);
        intent.putParcelableArrayListExtra("listeFavoris", favoris);

        btnFermer.setOnClickListener(v -> drawerLayout.closeDrawer(GravityCompat.END));
        btnFilter.setOnClickListener(v -> {
            preFilteredFavoris = Utils.getLieuxFavoris();
            showDialog();
        });



    }

    public void getFavoriListInBackground() {

        ExecutorService executorService = Executors.newSingleThreadExecutor();

        Handler handler = new Handler(Looper.getMainLooper());
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                favorisList = favorisDB.getFavoriDAO().getAllFavoris();

                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(FavorisActivity.this, "Ajouté à la BDD", Toast.LENGTH_SHORT).show();
                    }
                });

            }
        });
    }

    private void setFavAdapter() {
        adapter = new FavoriRecViewAdapter(favoris, this);
        favorisRecView.setAdapter(adapter);
        favorisRecView.setLayoutManager(new GridLayoutManager(this,2));
        Utils.getInstance();
        adapter.setFavoris(Utils.getLieuxFavoris());
    }

    void openDrawer(String nom, String categorie, Bitmap bitmap) {
        drawerLayout.openDrawer(GravityCompat.END);
        txtNomLieu.setText(nom);
        txtTypeLieu.setText(categorie);
        imgLieuDetails.setImageBitmap(bitmap);
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
        assert preFilteredFavoris != null;
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
    private void setView() {
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_VISIBLE;
        decorView.setSystemUiVisibility(uiOptions);
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