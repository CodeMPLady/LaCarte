package com.mplady.lacarte;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.mplady.lacarte.favori.Favori;

import java.util.List;

@Dao
public interface FavoriDAO {

    @Insert
    void addFavori(Favori favori);

    @Delete
    void deleteFavori(Favori favori);

    @Update
    void updateFavori(Favori favori);

    @Query("SELECT * FROM Favoris")
    List<Favori> getAllFavoris();

    //LiveData<List<Favori>> getAllFavoris();
}
