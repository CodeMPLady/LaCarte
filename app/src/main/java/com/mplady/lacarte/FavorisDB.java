package com.mplady.lacarte;

import androidx.room.Database;
import androidx.room.RoomDatabase;


import com.mplady.lacarte.favori.Favori;

@Database(
        version = 2,
        entities = {Favori.class}
)
public abstract class FavorisDB extends RoomDatabase {
    public abstract FavoriDAO getFavoriDAO();
}
