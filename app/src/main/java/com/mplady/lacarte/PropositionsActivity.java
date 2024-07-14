package com.mplady.lacarte;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.chip.Chip;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class PropositionsActivity extends AppCompatActivity {

    private TextView textTypeTitle;
    private ExtendedFloatingActionButton selectionFAB;
    private RecyclerView selectionRecView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_propositions);

        setView();
        initViews();

        selectionFAB.setOnClickListener(v -> showDialog());


    }

    private void showDialog() {
        LayoutInflater inflater = LayoutInflater.from(this);
        View dialogView = inflater.inflate(R.layout.propositions_dialog_layout, null);

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

    private void initViews() {
        textTypeTitle = findViewById(R.id.textTypeTitle);
        selectionFAB = findViewById(R.id.selectionFiltresFAB);
        selectionRecView = findViewById(R.id.selectionRecView);
    }

    private void setView() {
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_VISIBLE;
        decorView.setSystemUiVisibility(uiOptions);
    }
}