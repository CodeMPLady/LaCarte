package com.mplady.lacarte;

import androidx.annotation.NonNull;
import androidx.room.Database;

import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.mplady.lacarte.favori.Favori;

@Database(entities = {Favori.class}, version = 1)
public abstract class FavorisDB extends RoomDatabase {
    static FavorisDB favorisDB;

    public abstract FavoriDAO getFavoriDAO();

}
