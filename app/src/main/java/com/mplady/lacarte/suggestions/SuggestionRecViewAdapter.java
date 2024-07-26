package com.mplady.lacarte.suggestions;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.mplady.lacarte.R;
import com.mplady.lacarte.favori.Favori;

import java.util.ArrayList;


public class SuggestionRecViewAdapter extends RecyclerView.Adapter<SuggestionRecViewAdapter.ViewHolder>{

    ArrayList<Suggestion> suggestions;
    private final SuggestionActivity activity;

    public SuggestionRecViewAdapter(ArrayList<Suggestion> suggestions, SuggestionActivity activity) {
        this.suggestions = suggestions;
        this.activity = activity;
    }

    public void setSuggestions(ArrayList<Suggestion> suggestions) {
        this.suggestions = suggestions;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_carousel_suggestions, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        Suggestion suggestion = suggestions.get(position);
        holder.cardSuggestionName.setText(suggestions.get(position).getNom());
        holder.cardSuggestionCategorie.setText(suggestions.get(position).getCategorie());
        //holder.cardSuggestionImage.setImageBitmap(suggestions.get(position).getBitmap());
        ArrayList<Suggestion> suggestions = new ArrayList<>();
        suggestions.add(new Suggestion("Shin-Ya Ramen", "Restaurant", "Compans"));
        suggestions.add(new Suggestion("Les Cabochards", "Restaurant", "Cugnaux"));

        holder.detailsFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("Suggestions : " + suggestions);
                System.out.println("Suggestion choisie : " + suggestion);
                activity.openDrawer(suggestion);
            }
        });
    }

    @Override
    public int getItemCount() {
        return suggestions.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private MaterialCardView cardViewSuggestion;
        private ImageView cardSuggestionImage;
        private TextView cardSuggestionName, cardSuggestionCategorie;
        private FloatingActionButton detailsFAB;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            cardViewSuggestion = itemView.findViewById(R.id.cardViewSuggestion);
            cardSuggestionImage = itemView.findViewById(R.id.cardSuggestionImage);
            cardSuggestionName = itemView.findViewById(R.id.cardSuggestionName);
            cardSuggestionCategorie = itemView.findViewById(R.id.cardSuggestionCategorie);
            detailsFAB = itemView.findViewById(R.id.detailsFAB);
        }
    }
}
