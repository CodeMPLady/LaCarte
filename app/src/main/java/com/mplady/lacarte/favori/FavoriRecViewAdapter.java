package com.mplady.lacarte.favori;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.google.android.material.divider.MaterialDivider;
import com.mplady.lacarte.FavorisDB;
import com.mplady.lacarte.Place;
import com.mplady.lacarte.R;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class FavoriRecViewAdapter extends RecyclerView.Adapter<FavoriRecViewAdapter.ViewHolder>{
    private List<Place> favorises;
    private final FavorisActivity activity;
    FavorisDB favorisDB;

    public FavoriRecViewAdapter(List<Place> favorises, FavorisActivity activity) {
        this.favorises = favorises;
        this.activity = activity;
        callBackDatabase();
        getFavoriListInBackground();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_favori, parent, false);
        return new ViewHolder(view);
    }

    /**
     * Met en forme les élements de la liste des favoris en fonction de si il ont une catégorie ou non.
     * Met sur écoute le click sur un favori pour ouvrir le drawer.
     */
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Place place = favorises.get(position);
        holder.txtName.setText(place.getNom());
        String recupCategorieFavoris = place.getCategorie();
        holder.txtDescription.setText(recupCategorieFavoris);

        if (Objects.equals(recupCategorieFavoris, "")) {
            holder.dividerItemFavori.setVisibility(View.GONE);
            holder.txtDescription.setVisibility(View.GONE);
        } else {
            holder.txtDescription.setVisibility(View.VISIBLE);
            holder.txtDescription.setText(recupCategorieFavoris);
            holder.dividerItemFavori.setVisibility(View.VISIBLE);
        }

        if (place.getBitmap() != null) {
            Bitmap bitmap = BitmapFactory.decodeByteArray(place.getBitmap(), 0, place.getBitmap().length);
            holder.imgLieu.setImageBitmap(bitmap);
        } else
            holder.imgLieu.setImageResource(R.drawable.imgmapsdefault);

        holder.itemView.setOnClickListener(v -> activity.openDrawer(place));
       // if (context.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE)
            //fais en sorte ici que le drawer ne devienne pas vide quand je change d'orientation



    }

    private void callBackDatabase() {
        RoomDatabase.Callback myCallback = new RoomDatabase.Callback() {
            @Override
            public void onOpen(@NonNull SupportSQLiteDatabase db) {
                super.onOpen(db);
            }

            @Override
            public void onCreate(@NonNull SupportSQLiteDatabase db) {
                super.onCreate(db);
            }
        };
        favorisDB = Room.databaseBuilder(activity.getApplicationContext(), FavorisDB.class, "FavorisDB")
                .addCallback(myCallback)
                .fallbackToDestructiveMigration()
                .build();
    }

    public void getFavoriListInBackground() {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(() -> {
            List<Place> favorisList = favorisDB.getFavoriDAO().getAllFavoris();
            new Handler(Looper.getMainLooper()).post(() -> setFavoris(favorisList));
        });
    }

    @Override
    public int getItemCount() {
        return favorises.size();
    }

    public void setFavoris(List<Place> favorises) {
        this.favorises = favorises;
        notifyDataSetChanged();
    }

    public void updateFavoris(List<Place> newFavorises) {
        this.favorises.clear();
        this.favorises.addAll(newFavorises);
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView txtName, txtDescription;
        private final ImageView imgLieu;
        private final MaterialDivider dividerItemFavori;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtName = itemView.findViewById(R.id.txtName);
            txtDescription = itemView.findViewById(R.id.txtDescription);
            imgLieu = itemView.findViewById(R.id.imgLieu);
            dividerItemFavori = itemView.findViewById(R.id.dividerItemFavori);
        }
    }
}
