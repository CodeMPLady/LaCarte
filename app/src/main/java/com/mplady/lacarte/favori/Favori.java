package com.mplady.lacarte.favori;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "Favoris")
public class Favori implements Parcelable {

    @PrimaryKey
    @ColumnInfo(name = "nomLieu")
    @NonNull
    private String nom;

    @ColumnInfo(name = "categorieLieu")
    private String categorie;

//    @ColumnInfo(name = "adresseLieu")
//    private String adresse;

    @ColumnInfo(name = "bitmapLieu")
    public byte[] bitmapData;

    @Ignore
    public Favori() {
        nom = "";
    }

    public Favori(String nom, String categorie, byte[] bitmapData) {
        this.nom = nom;
        this.categorie = categorie;
        this.bitmapData = bitmapData;
    }

    protected Favori(Parcel in) {
        nom = in.readString();
        categorie = in.readString();
    }

    public static final Creator<Favori> CREATOR = new Creator<Favori>() {
        @Override
        public Favori createFromParcel(Parcel in) {
            return new Favori(in);
        }

        @Override
        public Favori[] newArray(int size) {
            return new Favori[size];
        }
    };

    public String getNom() {
        return nom;
    }
    public void setNom(String nom) {
        this.nom = nom;
    }
    public String getCategorie() {
        return categorie;
    }

//    public String getAdresse() {
//        return adresse;
//    }
//
//    public void setAdresse(String adresse) {
//        this.adresse = adresse;
//    }

    public byte[] getBitmap() {
        return bitmapData;
    }
    public void setBitmap(byte[] bitmapData) {
        this.bitmapData = bitmapData;
    }
    public void setCategorie(String categorie) {
        this.categorie = categorie;
    }
    @NonNull
    @Override
    public String toString() {
        return "Favori{" +
                ", nom='" + nom + '\'' +
                ", description='" + categorie + '\'' +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(nom);
        dest.writeString(categorie);
    }
}
