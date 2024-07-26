package com.mplady.lacarte.suggestions;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.mplady.lacarte.R;
import com.mplady.lacarte.favori.Favori;

import java.util.ArrayList;

public class SuggestionRecViewAdapter extends RecyclerView.Adapter<SuggestionRecViewAdapter.ViewHolder>{

    private ArrayList<Favori> suggestions = new ArrayList<>();
    private final Context context;

    public SuggestionRecViewAdapter(ArrayList<Suggestion> suggestions, SuggestionActivity activity) {
        this.suggestions = suggestions;
        this.activity = activity;
    }

    public void setSuggestions(ArrayList<Favori> suggestions) {
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
        Favori favori = suggestions.get(position);

        holder.cardSuggestionName.setText(favori.getNom());
        holder.cardSuggestionCategorie.setText(favori.getCategorie());

        holder.detailsFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("Suggestions : " + suggestions);
                System.out.println("Suggestion choisie : " + suggestion);
                activity.openDrawer(suggestion);
            }
        });
        if (favori.getBitmap() != null) {
            Bitmap bitmap = BitmapFactory.decodeByteArray(favori.getBitmap(), 0, favori.getBitmap().length);
            holder.cardSuggestionImage.setImageBitmap(bitmap);
        } else {
            holder.cardSuggestionImage.setImageResource(R.drawable.imgmapsdefaultresized);
        }

        holder.addFAB.setOnClickListener(v ->
                Toast.makeText(context, "Détails à venir sur " + favori.getNom(), Toast.LENGTH_SHORT).show()
        );
    }

    @Override
    public int getItemCount() {
        return suggestions.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private final MaterialCardView cardViewSuggestion;
        private final ImageView cardSuggestionImage;
        private final TextView cardSuggestionName;
        private final TextView cardSuggestionCategorie;
        private final FloatingActionButton addFAB;
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
