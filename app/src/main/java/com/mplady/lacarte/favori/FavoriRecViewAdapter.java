package com.mplady.lacarte.favori;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mplady.lacarte.R;

import java.util.ArrayList;

public class FavoriRecViewAdapter extends RecyclerView.Adapter<FavoriRecViewAdapter.ViewHolder>{
    private ArrayList<Favori> favoris;
    private final FavorisActivity activity;


    public FavoriRecViewAdapter(ArrayList<Favori> favoris, FavorisActivity activity) {
        this.favoris = favoris;
        this.activity = activity;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_favori, parent, false);

        return new ViewHolder(view);
    }

    @SuppressLint("CheckResult")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        String favoriNom = String.valueOf(favoris.get(position).getNom());
        String favoriCategorie = String.valueOf(favoris.get(position).getCategorie());
        Bitmap favorisBitmap = favoris.get(position).getBitmap();

        holder.txtName.setText(favoris.get(position).getNom());
        holder.txtDescription.setText(favoris.get(position).getCategorie());
        holder.imgLieu.setImageBitmap(favorisBitmap);

        holder.itemView.setOnClickListener(v -> activity.openDrawer(favoriNom, favoriCategorie, favorisBitmap));
    }

    @Override
    public int getItemCount() {
        return favoris.size();
    }

    public void setFavoris(ArrayList<Favori> favoris) {
        this.favoris = favoris;
        notifyDataSetChanged();
    }

    public void updateFavoris(ArrayList<Favori> newFavoris) {
        this.favoris.clear();
        this.favoris.addAll(newFavoris);
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView txtName, txtDescription;
        private final ImageView imgLieu;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtName = itemView.findViewById(R.id.txtName);
            txtDescription = itemView.findViewById(R.id.txtDescription);
            imgLieu = itemView.findViewById(R.id.imgLieu);
        }
    }
}
