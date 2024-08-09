package com.mplady.lacarte.favori;

import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

import android.view.ViewGroup;
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

import com.google.android.material.chip.Chip;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.mplady.lacarte.FavorisDB;
import com.mplady.lacarte.R;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class FavorisActivity extends AppCompatActivity {
    private DrawerLayout drawerLayout;
    boolean[] filter = new boolean[10];
    ArrayList<Favori> favoris = new ArrayList<>();
    List<Favori> preFilteredFavoris;
    ArrayList<Favori> filteredFavoris = new ArrayList<>();
    RecyclerView favorisRecView;
    FavoriRecViewAdapter adapter;
    private ImageView imgLieuDetails;
    private TextView txtNomLieu, txtAdresseLieu;
    private Chip chipTypeLieu;
    private Button btnFermer, btnSupprimerFavori;
    private ExtendedFloatingActionButton btnYAllerFavori;

    private ViewGroup mainContent;

    FavorisDB favorisDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favoris);
        initView();
        setupLayoutManager();
        callBackDatabase();
        getFavoriListInBackground();
    }

    private void initView() {
        drawerLayout = findViewById(R.id.mainFavoris);
        btnFermer = findViewById(R.id.btnFermer);
        btnYAllerFavori = findViewById(R.id.btnYAllerFavori);
        btnSupprimerFavori = findViewById(R.id.btnSupprimerFavori);
        imgLieuDetails = findViewById(R.id.imgLieuDetails);
        txtNomLieu = findViewById(R.id.txtNomLieu);
        txtAdresseLieu = findViewById(R.id.txtAdresseLieu);
        chipTypeLieu = findViewById(R.id.chipTypeLieu);

        mainContent = findViewById(R.id.mainFrame);

        favorisRecView = findViewById(R.id.favorisRecView);
        adapter = new FavoriRecViewAdapter(favoris, this);
        favorisRecView.setAdapter(adapter);

        FloatingActionButton btnFilter = findViewById(R.id.bntFiltre);
        btnFilter.setOnClickListener(v -> showDialog());
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        setupLayoutManager();
    }

    private void setupLayoutManager() {
        int orientation = getResources().getConfiguration().orientation;
        int spanCount = (orientation == Configuration.ORIENTATION_LANDSCAPE) ? 3 : 2;
        favorisRecView.setLayoutManager(new GridLayoutManager(this, spanCount));
    }

    void openDrawer(Favori favori) {
        drawerLayout.openDrawer(GravityCompat.END);
        txtNomLieu.setText(favori.getNom());
        chipTypeLieu.setText(favori.getCategorie());
        txtAdresseLieu.setText(favori.getAdresse());

        if (favori.getBitmap() != null) {
            Bitmap bitmap = BitmapFactory.decodeByteArray(favori.getBitmap(), 0, favori.getBitmap().length);
            imgLieuDetails.setImageBitmap(bitmap);
        } else
            imgLieuDetails.setImageResource(R.drawable.imgmapsdefaultresized);

        drawerLayout.addDrawerListener(new DrawerLayout.DrawerListener() {

            @Override
            public void onDrawerSlide(@NonNull View drawerView, float slideOffset) {
            }

            @Override
            public void onDrawerOpened(@NonNull View drawerView) {
                setEnableRecursively(mainContent, false);
                btnFermer.setOnClickListener(v -> drawerLayout.closeDrawer(GravityCompat.END));
                btnSupprimerFavori.setOnClickListener(v -> {
                    deleteFavoriInBackground(favori);
                    drawerLayout.closeDrawer(GravityCompat.END);
                    Toast.makeText(FavorisActivity.this, favori.getNom() + " supprimé de vos favoris !", Toast.LENGTH_SHORT).show();
                    getFavoriListInBackground();
                    recreate();
                });
                btnYAllerFavori.setOnClickListener(v -> openGoogleMaps(favori.getNom()));
            }

            @Override
            public void onDrawerClosed(@NonNull View drawerView) {
                setEnableRecursively(mainContent, true);
            }

            @Override
            public void onDrawerStateChanged(int newState) {
            }
        });
    }

    private void setEnableRecursively(ViewGroup viewGroup, boolean enable) {
        for (int i = 0; i < viewGroup.getChildCount(); i++) {
            View child = viewGroup.getChildAt(i);
            child.setEnabled(enable);
            if (child instanceof ViewGroup) {
                setEnableRecursively((ViewGroup) child, enable);
            }
        }
    }

    private Set<String> getCategoriesFromFavoris() {
        Set<String> categories = new HashSet<>();
        for (Favori favori : favoris) {
            categories.add(favori.getCategorie());
        }
        return categories;
    }

    private void showDialog() {
        LayoutInflater inflater = LayoutInflater.from(this);
        View dialogView = inflater.inflate(R.layout.fav_dialog_layout, null);

        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this).setView(dialogView);

        CheckBox checkRestaurant = dialogView.findViewById(R.id.checkRestaurant);
        CheckBox checkEssence = dialogView.findViewById(R.id.checkEssence);
        CheckBox checkSupermarche = dialogView.findViewById(R.id.checkSupermache);
        CheckBox checkPharmacie = dialogView.findViewById(R.id.checkPharmacie);
        CheckBox checkMagasin = dialogView.findViewById(R.id.checkMagasin);
        CheckBox checkCinema = dialogView.findViewById(R.id.checkCinema);
        CheckBox checkParc = dialogView.findViewById(R.id.checkParc);
        CheckBox checkBoulangerie = dialogView.findViewById(R.id.checkBoulangerie);
        CheckBox checkMusee = dialogView.findViewById(R.id.checkMusee);
        CheckBox checkTous = dialogView.findViewById(R.id.checkTous);

        Set<String> categories = getCategoriesFromFavoris();

        FloatingActionButton btnValider = dialogView.findViewById(R.id.btnValider);
        FloatingActionButton btnAnnuler = dialogView.findViewById(R.id.btnAnnuler);

        checkRestaurant.setVisibility(categories.contains("Restaurant") ? View.VISIBLE : View.GONE);
        checkEssence.setVisibility(categories.contains("Station essence") ? View.VISIBLE : View.GONE);
        checkSupermarche.setVisibility(categories.contains("Supermarché") ? View.VISIBLE : View.GONE);
        checkPharmacie.setVisibility(categories.contains("Pharmacie") ? View.VISIBLE : View.GONE);
        checkMagasin.setVisibility(categories.contains("Magasin") ? View.VISIBLE : View.GONE);
        checkCinema.setVisibility(categories.contains("Cinéma") ? View.VISIBLE : View.GONE);
        checkParc.setVisibility(categories.contains("Parc") ? View.VISIBLE : View.GONE);
        checkBoulangerie.setVisibility(categories.contains("Boulangerie") ? View.VISIBLE : View.GONE);
        checkMusee.setVisibility(categories.contains("Musée") ? View.VISIBLE : View.GONE);

        AlertDialog dialog = builder.create();
        dialog.show();

        btnValider.setOnClickListener(v -> {
            filter[0] = checkRestaurant.isChecked();
            filter[1] = checkEssence.isChecked();
            filter[2] = checkSupermarche.isChecked();
            filter[3] = checkPharmacie.isChecked();
            filter[4] = checkMagasin.isChecked();
            filter[5] = checkCinema.isChecked();
            filter[6] = checkParc.isChecked();
            filter[7] = checkBoulangerie.isChecked();
            filter[8] = checkMusee.isChecked();
            filter[9] = checkTous.isChecked();
            filtre();
            dialog.dismiss();
        });
        System.out.println("FAVORIS FILTRES : " + filteredFavoris);
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
                    (filter[2] && "Supermarché".equals(categorie)) ||
                    (filter[3] && "Pharmacie".equals(categorie)) ||
                    (filter[4] && "Magasin".equals(categorie)) ||
                    (filter[5] && "Cinéma".equals(categorie)) ||
                    (filter[6] && "Parc".equals(categorie)) ||
                    (filter[7] && "Boulangerie".equals(categorie)) ||
                    (filter[8] && "Musée".equals(categorie))) {
                filteredFavoris.add(fav);
            }
        }
        if (filter[9])
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

    private void openGoogleMaps(String lieu) {
        Uri gmmIntentUri = Uri.parse("geo:0,0?q=" + lieu);
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        mapIntent.setPackage("com.google.android.apps.maps");
        if (mapIntent.resolveActivity(getPackageManager()) != null) {
            startActivity(mapIntent);
        } else {
            Uri webIntentUri = Uri.parse("https://www.google.com/maps/search/?api=1&query=" + lieu);
            Intent webIntent = new Intent(Intent.ACTION_VIEW, webIntentUri);
            startActivity(webIntent);
        }
    }
}
