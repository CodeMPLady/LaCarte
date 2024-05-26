package com.mplady.lacarte;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class FavoriRecViewAdapter extends RecyclerView.Adapter<FavoriRecViewAdapter.ViewHolder>{
    private ArrayList<Favori> favoris = new ArrayList<>();
    private Context context;

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
        holder.txtDescription.setText(favoris.get(position).getDescription());

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

    public class ViewHolder extends RecyclerView.ViewHolder{

        private CardView parent;
        private TextView txtName;
        private TextView txtDescription;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            parent = itemView.findViewById(R.id.parent);
            txtName = itemView.findViewById(R.id.txtName);
            txtDescription = itemView.findViewById(R.id.txtDescription);
        }
    }


}
