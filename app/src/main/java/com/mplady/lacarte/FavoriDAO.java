package com.mplady.lacarte;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface FavoriDAO {

    @Insert
    void addFavori(Place place);

    @Delete
    void deleteFavori(Place place);

    @Update
    void updateFavori(Place place);

    @Query("SELECT * FROM Favoris")
    List<Place> getAllFavoris();
}
