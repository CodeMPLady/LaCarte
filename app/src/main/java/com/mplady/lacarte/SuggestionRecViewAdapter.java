package com.mplady.lacarte;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;


public class SuggestionRecViewAdapter extends RecyclerView.Adapter<SuggestionRecViewAdapter.ViewHolder>{

    ArrayList<Suggestion> suggestions;
    Context context;

    public SuggestionRecViewAdapter(Context context) {
        this.context = context;
    }

    public void setSuggestions(ArrayList<Suggestion> suggestions) {
        this.suggestions = suggestions;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_suggestions, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.cardSuggestionName.setText(suggestions.get(position).getNom());
        holder.cardSuggestionCategorie.setText(suggestions.get(position).getCategorie());
        //holder.cardSuggestionImage.setImageBitmap(suggestions.get(position).getBitmap());

        holder.addFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "Détails à venir sur " + suggestions.get(position).getNom(), Toast.LENGTH_SHORT).show();
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
        private FloatingActionButton addFAB;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            cardViewSuggestion = itemView.findViewById(R.id.cardViewSuggestion);
            cardSuggestionImage = itemView.findViewById(R.id.cardSuggestionImage);
            cardSuggestionName = itemView.findViewById(R.id.cardSuggestionName);
            cardSuggestionCategorie = itemView.findViewById(R.id.cardSuggestionCategorie);
            addFAB = itemView.findViewById(R.id.addFAB);
        }
    }
}
