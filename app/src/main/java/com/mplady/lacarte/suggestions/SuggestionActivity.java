package com.mplady.lacarte.suggestions;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
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
import com.mplady.lacarte.favori.Favori;
import com.mplady.lacarte.favori.FavorisActivity;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SuggestionActivity extends AppCompatActivity {

    private TextView textTypeTitle;
    ArrayList<Suggestion> suggestions;
    private ExtendedFloatingActionButton selectionFAB;
    private RecyclerView selectionRecView;
    private final String[] tableauSelectionCategories = {
            "Restaurants",
            "Magasins",
            "Stations essence",
            "Pharmacies",
            "Supermarchés"
    };
    private SuggestionRecViewAdapter adapter;

    private DrawerLayout drawerLayoutSuggestions;
    private ImageView imgLieuDetailsSuggestions;
    private TextView txtNomLieuSuggestions, txtAdresseLieuSuggestions;
    private Chip chipTypeLieuSuggestions;
    private FloatingActionButton btnAjouterAuxFavoris;
    private FloatingActionButton btnFermerSuggestions;
    private ExtendedFloatingActionButton btnYAllerSuggestions;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_suggestions);

        setView();
        initViews();
        setTitle();

        selectionFAB.setOnClickListener(v -> showDialog());

        adapter = new SuggestionRecViewAdapter(suggestions,this);
        selectionRecView.setAdapter(adapter);
        selectionRecView.setLayoutManager(new LinearLayoutManager(this));

        ArrayList<Suggestion> suggestions = new ArrayList<>();
        suggestions.add(new Suggestion("Shin-Ya Ramen", "Restaurant", "Compans"));
        suggestions.add(new Suggestion("Les Cabochards", "Restaurant", "Cugnaux"));
        adapter.setSuggestions(suggestions);

    }

    private void initViews() {
        textTypeTitle = findViewById(R.id.textTypeTitle);
        selectionFAB = findViewById(R.id.selectionFiltresFAB);
        selectionRecView = findViewById(R.id.selectionRecView);

        drawerLayoutSuggestions = findViewById(R.id.mainSuggestions);
        btnFermerSuggestions = findViewById(R.id.btnFermerSuggestions);
        btnYAllerSuggestions = findViewById(R.id.btnYAllerSuggestions);
        btnAjouterAuxFavoris = findViewById(R.id.btnAjouterAuxFavoris);
        imgLieuDetailsSuggestions = findViewById(R.id.imgLieuDetailsSuggestions);
        txtNomLieuSuggestions = findViewById(R.id.txtNomLieuSuggestions);
        txtAdresseLieuSuggestions = findViewById(R.id.txtAdresseLieuSuggestions);
        chipTypeLieuSuggestions = findViewById(R.id.chipTypeLieuSuggestions);
    }

    @SuppressLint("SetTextI18n")
    private void setTitle() {
        Intent intent = getIntent();
        int position = intent.getIntExtra("position", 0);
        for (int i = 0; i < tableauSelectionCategories.length; i++) {
            if (i == position) {
                textTypeTitle.setText(tableauSelectionCategories[i] + " autour de vous");
            }
        }
    }

    void openDrawer(Suggestion suggestion) {
        drawerLayoutSuggestions.openDrawer(GravityCompat.END);
        txtNomLieuSuggestions.setText(suggestion.getNom());
        chipTypeLieuSuggestions.setText(suggestion.getCategorie());
        txtAdresseLieuSuggestions.setText(suggestion.getAdresse());
        imgLieuDetailsSuggestions.setImageResource(R.drawable.imgmapsdefaultresized);

        btnYAllerSuggestions.setOnClickListener(v -> openGoogleMaps(suggestion.getNom()));

        btnFermerSuggestions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayoutSuggestions.closeDrawer(GravityCompat.END);
            }
        });

//        if (suggestion.getBitmap() != null) {
//            Bitmap bitmap = BitmapFactory.decodeByteArray(suggestion.getBitmap(), 0, suggestion.getBitmap().length);
//            imgLieuDetailsSuggestions.setImageBitmap(bitmap);
//        } else
//            imgLieuDetailsSuggestions.setImageResource(R.drawable.imgmapsdefaultresized);
    }

    private void showDialog() {
        LayoutInflater inflater = LayoutInflater.from(this);
        View dialogView = inflater.inflate(R.layout.suggestions_dialog_layout, null);

        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this).setView(dialogView);

        Chip chipRestaurant = dialogView.findViewById(R.id.chipRestaurant);
        Chip chipSupermarche = dialogView.findViewById(R.id.chipSupermarche);
        Chip chipPharmacie = dialogView.findViewById(R.id.chipPharmacie);
        Chip chipStationEssence = dialogView.findViewById(R.id.chipStationEssence);
        Chip chipMagasin = dialogView.findViewById(R.id.chipMagasin);

        FloatingActionButton btnValiderSelection = dialogView.findViewById(R.id.btnValiderSelection);
        FloatingActionButton btnAnnulerSelection = dialogView.findViewById(R.id.btnAnnulerSelection);

        AlertDialog dialog = builder.create();
        btnAnnulerSelection.setOnClickListener(v -> dialog.dismiss());
        btnValiderSelection.setOnClickListener(v -> {
            boolean isRestaurantSelected = chipRestaurant.isChecked();
            boolean isSupermarcheSelected = chipSupermarche.isChecked();
            boolean isPharmacieSelected = chipPharmacie.isChecked();
            boolean isStationEssenceSelected = chipStationEssence.isChecked();
            boolean isMagasinSelected = chipMagasin.isChecked();
        });
        dialog.show();

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

    private void setView() {
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_VISIBLE;
        decorView.setSystemUiVisibility(uiOptions);
    }
}