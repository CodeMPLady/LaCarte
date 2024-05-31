package com.mplady.lacarte.favori;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.card.MaterialCardView;
import com.mplady.lacarte.R;

import java.util.ArrayList;

public class FavoriRecViewAdapter extends RecyclerView.Adapter<FavoriRecViewAdapter.ViewHolder>{
    private ArrayList<Favori> favoris = new ArrayList<>();
    private final Context context;

    public FavoriRecViewAdapter(Context context) {
        this.context = context;
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
        holder.txtName.setText(favoris.get(position).getNom());
        holder.txtDescription.setText(favoris.get(position).getCategorie());
        Glide.with(context)
                .asBitmap()
                .load(favoris.get(position).getImgLieuURL())
                        .into(holder.imgLieu);

        holder.parent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "click", Toast.LENGTH_SHORT).show();
            }
        });
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

        private final MaterialCardView parent;
        private final TextView txtName;
        private final TextView txtDescription;
        private final ImageView imgLieu;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            parent = itemView.findViewById(R.id.parent);
            txtName = itemView.findViewById(R.id.txtName);
            txtDescription = itemView.findViewById(R.id.txtDescription);
            imgLieu = itemView.findViewById(R.id.imgLieu);
        }

        public void makeVisible(CardView parent) {
            parent.setVisibility(View.VISIBLE);
        }

        public void makeInvisible(CardView parent) {
            parent.setVisibility(View.INVISIBLE);
        }
    }
}
