package com.mplady.lacarte.stat;

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
import com.mplady.lacarte.R;

import java.util.ArrayList;

public class PlaceRecViewAdapter extends RecyclerView.Adapter<PlaceRecViewAdapter.ViewHolder> {

    private ArrayList<Place> places = new ArrayList<>();
    private Context context;

    public PlaceRecViewAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.place_list_item_stats, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.placeName.setText(places.get(position).getName());
        Glide.with(context)
                .asBitmap()
                .load(places.get(position).getImgPlace())
                .into(holder.imgPlace);
        holder.textCategorie.setText(places.get(position).getCategorie());
        holder.textNbVisites.setText(places.get(position).getNbVisites());

        holder.parent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, places.get(position).getName() + " selected", Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public int getItemCount() {
        return places.size();
    }

    public void setPlaces(ArrayList<Place> places) {
        this.places = places;
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private CardView parent;
        private ImageView imgPlace;
        private TextView placeName;
        private TextView textCategorie;
        private TextView textDescription;
        private TextView textNbVisites;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            parent = itemView.findViewById(R.id.parent);
            imgPlace = itemView.findViewById(R.id.imgPlace);
            placeName = itemView.findViewById(R.id.placeName);
            textCategorie = itemView.findViewById(R.id.textCategorie);
            textNbVisites = itemView.findViewById(R.id.textNbVisites);
        }
    }

}
