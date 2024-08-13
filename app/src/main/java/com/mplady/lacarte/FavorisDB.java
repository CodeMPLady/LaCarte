package com.mplady.lacarte;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(
        version = 2,
        entities = {Place.class}
)
public abstract class FavorisDB extends RoomDatabase {
    public abstract FavoriDAO getFavoriDAO();
}
