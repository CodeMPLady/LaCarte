package com.mplady.lacarte.favori;

import android.annotation.SuppressLint;
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
        callBackDatabase();
        getFavoriListInBackground();
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

        Favori favori = favoris.get(position);
        holder.txtName.setText(favori.getNom());
        holder.txtDescription.setText(favori.getCategorie());
        if (favori.getBitmap() != null) {
            Bitmap bitmap = BitmapFactory.decodeByteArray(favori.getBitmap(), 0, favori.getBitmap().length);
            holder.imgLieu.setImageBitmap(bitmap);
        } else {
            holder.imgLieu.setImageResource(R.drawable.imgmapsdefaultresized);
        }

        holder.itemView.setOnClickListener(v -> activity.openDrawer(favori));
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
