package com.mplady.lacarte;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.mplady.lacarte.favori.Favori;

import java.util.ArrayList;
import java.util.List;

@Dao
public interface FavoriDAO {

    @Insert
    public void addFavori(Favori favori);

    @Delete
    public void deleteFavori(Favori favori);

    @Update
    public void updateFavori(Favori favori);

    @Query("SELECT * FROM Favoris")
    public List<Favori> getAllFavoris();
}
