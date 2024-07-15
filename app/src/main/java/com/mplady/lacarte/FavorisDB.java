package com.mplady.lacarte;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.mplady.lacarte.favori.Favori;

@Database(entities = {Favori.class}, version = 1
)
public abstract class FavorisDB extends RoomDatabase {

    public abstract FavoriDAO getFavoriDAO();

}
