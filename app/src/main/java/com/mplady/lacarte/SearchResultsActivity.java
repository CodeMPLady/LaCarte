package com.mplady.lacarte;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class SearchResultsActivity extends AppCompatActivity {

    private TextView textSubmited;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_search_results);

        textSubmited = findViewById(R.id.textSubmited);

        Intent intent = getIntent();
        String query = intent.getStringExtra("search_query");

        // Afficher le texte dans le TextView
        if (query != null) {
            textSubmited.setText(query);
        }


    }
}