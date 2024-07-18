package com.mplady.lacarte.favori;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
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

import com.mplady.lacarte.FavorisDB;
import com.mplady.lacarte.R;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class FavoriRecViewAdapter extends RecyclerView.Adapter<FavoriRecViewAdapter.ViewHolder>{
    private List<Favori> favoris;
    private final FavorisActivity activity;
    FavorisDB favorisDB;

    public FavoriRecViewAdapter(List<Favori> favoris, FavorisActivity activity) {
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
        //String favoriAdresse = String.valueOf(favoris.get(position).getAdresse());
        byte[] favorisBitmap = favoris.get(position).getBitmap();

        holder.txtName.setText(favoris.get(position).getNom());
        holder.txtDescription.setText(favoris.get(position).getCategorie());
        //holder.imgLieu.setImageBitmap(favorisBitmap);

        callBackDatabase();

        holder.itemView.setOnClickListener(v -> {
            getFavoriListInBackground();
            activity.openDrawer(favoris.get(position));
        });
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
                .build();
    }

    public void getFavoriListInBackground() {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(() -> {
            List<Favori> favorisList = favorisDB.getFavoriDAO().getAllFavoris();
            new Handler(Looper.getMainLooper()).post(() -> setFavoris(favorisList));
        });
    }

    @Override
    public int getItemCount() {
        return favoris.size();
    }

    public void setFavoris(List<Favori> favoris) {
        this.favoris = favoris;
        notifyDataSetChanged();
    }

    public void updateFavoris(List<Favori> newFavoris) {
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
