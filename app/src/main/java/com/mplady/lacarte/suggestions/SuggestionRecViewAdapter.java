package com.mplady.lacarte.suggestions;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mplady.lacarte.R;
import com.mplady.lacarte.Place;

import java.util.ArrayList;
import java.util.Objects;

public class SuggestionRecViewAdapter extends RecyclerView.Adapter<SuggestionRecViewAdapter.ViewHolder>{

    private final ArrayList<Place> suggestions = new ArrayList<>();
    private final SuggestionActivity activity;
    private final String[] tableauTypes;
    private final String[] tableauJolisTypes;

    int i = 0;


    public SuggestionRecViewAdapter(SuggestionActivity activity, String[] tableauTypes, String[] tableauJolisTypes) {
        this.activity = activity;
        this.tableauTypes = tableauTypes;
        this.tableauJolisTypes = tableauJolisTypes;
    }

    public void setSuggestions(ArrayList<Place> newSuggestions) {
        this.suggestions.clear();
        this.suggestions.addAll(newSuggestions);
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
        Place suggestion = suggestions.get(position);
        String recupCategorieSuggestion = suggestion.getSousCategorie();

        i=0;
        while (!Objects.equals((tableauTypes[i]), recupCategorieSuggestion)) {
            i++;
            if (i >= 184)
                break;
        }
        holder.cardSuggestionCategorie.setText(tableauJolisTypes[i]);
        holder.cardSuggestionName.setText(suggestion.getNom());
        holder.detailsFAB.setOnClickListener(v -> activity.openDrawer(suggestion));
        if (suggestion.getPhoto() != null)
            holder.cardSuggestionImage.setImageBitmap(suggestion.getPhoto());
        else
            holder.cardSuggestionImage.setImageResource(R.drawable.imgmapsdefault);
    }

    @Override
    public int getItemCount() {
        return suggestions.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private final ImageView cardSuggestionImage;
        private final TextView cardSuggestionName;
        private final TextView cardSuggestionCategorie;
        private final Button detailsFAB;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            cardSuggestionImage = itemView.findViewById(R.id.cardSuggestionImage);
            cardSuggestionName = itemView.findViewById(R.id.cardSuggestionName);
            cardSuggestionCategorie = itemView.findViewById(R.id.cardSuggestionCategorie);
            detailsFAB = itemView.findViewById(R.id.detailsFAB);
        }
    }
}
