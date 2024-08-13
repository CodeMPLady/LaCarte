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

import androidx.activity.EdgeToEdge;
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
import com.mplady.lacarte.Place;
import com.mplady.lacarte.R;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class FavorisActivity extends AppCompatActivity {
    private TextView txtNomLieu, txtAdresseLieu;
    private ImageView imgLieuDetails;
    private Button btnFermer, btnSupprimerFavori, textNoFavoris;
    private ExtendedFloatingActionButton btnYAllerFavori;
    private RecyclerView favorisRecView;
    private FavoriRecViewAdapter adapter;
    private Chip chipTypeLieu;
    private DrawerLayout drawerLayout;
    private ViewGroup mainContent;

    boolean[] filter = new boolean[10];

    FavorisDB favorisDB;
    ArrayList<Place> favoris = new ArrayList<>();
    List<Place> preFilteredFavorises;
    ArrayList<Place> filteredFavorises = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_favoris);
        callBackDatabase();
        getFavoriListInBackground(true);
        initView();
        setupLayoutManager();

    }

    /**
     * Initialise les variables des élements visuels de l'activité.
     * Intialise et appel la méthode showDialog quand on clique sur le bouton filtre.
     */

    private void initView() {
        drawerLayout = findViewById(R.id.mainFavoris);
        btnFermer = findViewById(R.id.btnFermer);
        btnYAllerFavori = findViewById(R.id.btnYAllerFavori);
        btnSupprimerFavori = findViewById(R.id.btnSupprimerFavori);
        imgLieuDetails = findViewById(R.id.imgLieuDetails);
        txtNomLieu = findViewById(R.id.txtNomLieu);
        txtAdresseLieu = findViewById(R.id.txtAdresseLieu);
        chipTypeLieu = findViewById(R.id.chipTypeLieu);
        textNoFavoris = findViewById(R.id.textNoFavoris);
        mainContent = findViewById(R.id.mainFrame);
        favorisRecView = findViewById(R.id.favorisRecView);
        adapter = new FavoriRecViewAdapter(favoris, this);
        favorisRecView.setAdapter(adapter);

        FloatingActionButton btnFilter = findViewById(R.id.bntFiltre);
        btnFilter.setOnClickListener(v -> showDialog());
    }

    /**
     * Récupère l'orientation du téléphone pour savoir si on affiche 2 ou 3 colonnes.
     */
    private void setupLayoutManager() {
        int orientation = getResources().getConfiguration().orientation;
        int nbreColonne = (orientation == Configuration.ORIENTATION_LANDSCAPE) ? 3 : 2;
        favorisRecView.setLayoutManager(new GridLayoutManager(this, nbreColonne));
    }

    /**
     * Met à jour le layoutManager quand on change l'orientation du téléphone.
     */
    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        setupLayoutManager();
    }

    /**
     * Initialise la base de données.
     */
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

    /**
     * Récupère la liste des favoris de la base de données pour les rajoutés dans la liste favoris.
     * Exécute tout ça dans un autre thread pour ne pas bloquer l'interface.
     */
    private void getFavoriListInBackground(boolean setAdapter) {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());
        executorService.execute(() -> {
            try {
                favoris.clear();
                favoris.addAll(favorisDB.getFavoriDAO().getAllFavoris());
                preFilteredFavorises = new ArrayList<>(favoris);
                handler.post(() -> {
                    if (setAdapter)
                        adapter.setFavoris(favoris);
                    if (favoris.isEmpty())
                        textNoFavoris.setVisibility(View.VISIBLE);
                    else
                        textNoFavoris.setVisibility(View.GONE);
                });

            } catch (Exception e) {
                handler.post(() -> Toast.makeText(FavorisActivity.this, "Erreur lors de la récupération des favoris", Toast.LENGTH_SHORT).show());
            } finally {
                executorService.shutdown();
            }
        });
    }

    /**
     * Supprime un favori de la base de données dans un autre thread.
     */
    private void deleteFavoriInBackground(Place place) {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(() -> {
            favorisDB.getFavoriDAO().deleteFavori(place);
            getFavoriListInBackground(true);
            executorService.shutdown();
        });
    }

    /**
     * Affiche un dialog pour filtrer les favoris.
     * Initialise les variables des élements visuels du dialog grâce à dialogView.
     * Désactive les cases du filtres si aucun favori correspondant à la case n'est trouvé.
     */
    private void showDialog() {
        getFavoriListInBackground(false);
        LayoutInflater inflater = LayoutInflater.from(this);
        View dialogView = inflater.inflate(R.layout.fav_dialog_layout, null);
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this).setView(dialogView);

        CheckBox checkRestaurant = dialogView.findViewById(R.id.checkRestaurant);
        CheckBox checkEssence = dialogView.findViewById(R.id.checkEssence);
        CheckBox checkSupermarche = dialogView.findViewById(R.id.checkSupermarche);
        CheckBox checkPharmacie = dialogView.findViewById(R.id.checkPharmacie);
        CheckBox checkMagasin = dialogView.findViewById(R.id.checkMagasin);
        CheckBox checkCinema = dialogView.findViewById(R.id.checkCinema);
        CheckBox checkParc = dialogView.findViewById(R.id.checkParc);
        CheckBox checkBoulangerie = dialogView.findViewById(R.id.checkBoulangerie);
        CheckBox checkMusee = dialogView.findViewById(R.id.checkMusee);
        CheckBox checkTous = dialogView.findViewById(R.id.checkTous);
        FloatingActionButton btnValider = dialogView.findViewById(R.id.btnValider);
        FloatingActionButton btnAnnuler = dialogView.findViewById(R.id.btnAnnuler);

        Set<String> categories = getCategoriesFromFavoris();
        checkRestaurant.setEnabled(categories.contains("Restaurant"));
        checkEssence.setEnabled(categories.contains("Station essence"));
        checkSupermarche.setEnabled(categories.contains("Supermarché"));
        checkPharmacie.setEnabled(categories.contains("Pharmacie"));
        checkMagasin.setEnabled(categories.contains("Magasin"));
        checkCinema.setEnabled(categories.contains("Cinéma"));
        checkParc.setEnabled(categories.contains("Parc"));
        checkBoulangerie.setEnabled(categories.contains("Boulangerie"));
        checkMusee.setEnabled(categories.contains("Musée"));

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
        btnAnnuler.setOnClickListener(v -> dialog.dismiss());
    }

    /**
     * Filtre les favoris en fonction des cases coché du dialog.
     */
    private void filtre() {
        filteredFavorises.clear();
        boolean isAnyFilterChecked = false;
        for (int i = 0; i < filter.length - 1; i++) {
            if (filter[i]) {
                isAnyFilterChecked = true;
                break;
            }
        }
        if (isAnyFilterChecked) {
            for (Place fav : preFilteredFavorises) {
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
                    filteredFavorises.add(fav);
                }
            }
        } else
            filteredFavorises.addAll(preFilteredFavorises);
        if (filter[9])
            recreate();
        adapter.updateFavoris(filteredFavorises);
    }

    /**
     * Récupère les catégories des favoris pour showDialog()
     */
    private Set<String> getCategoriesFromFavoris() {
        Set<String> categories = new HashSet<>();
        for (Place place : favoris) {
            categories.add(place.getCategorie());
        }
        return categories;
    }

    /**
     * Ouvre le drawer avec les informations du favori.
     * Met sur écoute les boutons pour fermer, supprimer et y aller.
     * Quand le drawer s'ouvre ou se ferme il désactivé ou active tout les enfants de l'activité.
     */
    void openDrawer(Place place) {
        drawerLayout.openDrawer(GravityCompat.END);
        txtNomLieu.setText(place.getNom());
        txtAdresseLieu.setText(place.getAdresse());

        String recupCategorieFavoriDrawer = place.getCategorie();

        if (Objects.equals(recupCategorieFavoriDrawer, "")) {
            chipTypeLieu.setVisibility(View.GONE);
        } else {
            chipTypeLieu.setVisibility(View.VISIBLE);
            chipTypeLieu.setText(recupCategorieFavoriDrawer);
        }

        if (place.getBitmap() != null) {
            Bitmap bitmap = BitmapFactory.decodeByteArray(place.getBitmap(), 0, place.getBitmap().length);
            imgLieuDetails.setImageBitmap(bitmap);
        } else
            imgLieuDetails.setImageResource(R.drawable.imgmapsdefault);

        drawerLayout.addDrawerListener(new DrawerLayout.DrawerListener() {

            @Override
            public void onDrawerSlide(@NonNull View drawerView, float slideOffset) {
            }

            @Override
            public void onDrawerOpened(@NonNull View drawerView) {
                setEnableRecursively(mainContent, false);
                btnFermer.setOnClickListener(v -> drawerLayout.closeDrawer(GravityCompat.END));
                btnSupprimerFavori.setOnClickListener(v -> {
                    deleteFavoriInBackground(place);
                    drawerLayout.closeDrawer(GravityCompat.END);
                    Toast.makeText(FavorisActivity.this, place.getNom() + " supprimé de vos favoris !", Toast.LENGTH_SHORT).show();
                    getFavoriListInBackground(true);
                    recreate();
                });
                btnYAllerFavori.setOnClickListener(v -> openGoogleMaps(place.getNom()));
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

    /**
     * Désactive ou active les enfants de l'activité pour openDrawer().
     */
    private void setEnableRecursively(ViewGroup viewGroup, boolean enable) {
        for (int i = 0; i < viewGroup.getChildCount(); i++) {
            View child = viewGroup.getChildAt(i);
            child.setEnabled(enable);
            if (child instanceof ViewGroup) {
                setEnableRecursively((ViewGroup) child, enable);
            }
        }
    }

    /**
     * Ouvre Google Maps avec le nom du lieu.
     */
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
