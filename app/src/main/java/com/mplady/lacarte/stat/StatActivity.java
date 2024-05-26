package com.mplady.lacarte.stat;

import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.mplady.lacarte.R;

import java.util.ArrayList;

public class StatActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //EdgeToEdge.enable(this);
        setContentView(R.layout.activity_stat);
        setView();
        animatedBackgroundSearchIcon();

        RecyclerView placesRecView = findViewById(R.id.placesRecView);
        PlaceRecViewAdapter adapter = new PlaceRecViewAdapter(this);

        placesRecView.setAdapter(adapter);
        placesRecView.setLayoutManager(new GridLayoutManager(this, 2));

        ArrayList<Place> places = new ArrayList<>();
        places.add(new Place(1, "https://benedictelarre.wordpress.com/wp-content/uploads/2016/11/pa280017.jpg?w=1200","Shin-ya Ramen", "Restaurant", "3" ));
        places.add(new Place(2, "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcRBdiNlirY34rzkkar1CXN2DLUagEd0mtmr2A", "Les Cabochards", "Restaurant", "1"));
        places.add(new Place(3, "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcRrhJxxsddVa7JL-jSy6n0KE4jpYTPzUVMo1w", "Leclerc Blagnac", "Supermarché", "Trop"));

        adapter.setPlaces(places);
   }

    private void setView() {
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_VISIBLE;
        decorView.setSystemUiVisibility(uiOptions);
    }

    private void animatedBackgroundSearchIcon() {
        ConstraintLayout constraintLayout = findViewById(R.id.main);
        AnimationDrawable animationDrawable = (AnimationDrawable) constraintLayout.getBackground();
        animationDrawable.setEnterFadeDuration(1500);
        animationDrawable.setExitFadeDuration(2000);
        animationDrawable.start();
    }
}