package com.mplady.lacarte.accueil;

import static androidx.core.content.ContextCompat.startActivity;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.mplady.lacarte.suggestions.SuggestionActivity;
import com.mplady.lacarte.R;

public class ImageAdapterCarousel extends RecyclerView.Adapter<ImageAdapterCarousel.ViewHolder> {
    final Context context;
    private final int[] imageRessourceIDs;

    public ImageAdapterCarousel(Context context, int[] imageRessourceIDs) {
        this.context = context;
        this.imageRessourceIDs = imageRessourceIDs;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.image_list_item_carousel, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        int imageRessourceID = imageRessourceIDs[position];
        Glide.with(context)
                .load(imageRessourceID)
                .into(holder.imageView);
        holder.imageView.setOnClickListener(v -> {
            Intent intent = new Intent(context, SuggestionActivity.class);
            int positionType = holder.getAdapterPosition();
            intent.putExtra("position", positionType);
            startActivity(context, intent, null);
        });
    }

    @Override
    public int getItemCount() {
        return imageRessourceIDs.length;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        final ImageView imageView;
        public ViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.list_item_image_carousel);
        }
    }
}
