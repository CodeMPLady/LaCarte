package com.mplady.lacarte.favori;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.util.Objects;

@Entity(tableName = "Favoris")
public class Favori implements Parcelable {

    @PrimaryKey
    @ColumnInfo(name = "nomLieu")
    @NonNull
    private String nom;

    @ColumnInfo(name = "categorieLieu")
    private String categorie;

    @ColumnInfo(name = "adresseLieu")
    private String adresse;

    @ColumnInfo(name = "bitmapLieu")
    public byte[] bitmapData;

    @Ignore
    private Bitmap photo;

    @Ignore
    public Favori() {
        nom = "";
    }

    public Favori(@NonNull String nom, String categorie, byte[] bitmapData, String adresse) {
        this.nom = nom;
        this.categorie = categorie;
        this.bitmapData = bitmapData;
        this.adresse = adresse;
    }

    public Favori(@NonNull String nom, String categorie,Bitmap photo, String adresse) {
        this.nom = nom;
        this.categorie = categorie;
        this.adresse = adresse;
        this.photo = photo;
    }

    protected Favori(Parcel in) {
        nom = Objects.requireNonNull(in.readString());
        categorie = in.readString();
        adresse = in.readString();
        bitmapData = in.createByteArray();
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

    @NonNull
    public String getNom() {
        return nom;
    }
    public String getCategorie() {
        return categorie;
    }
    public String getAdresse() {
        return adresse;
    }
    public byte[] getBitmap() {
        return bitmapData;
    }
    public Bitmap getPhoto() {
        return photo;
    }


    @NonNull
    @Override
    public String toString() {
        return "Favori{" +
                "Nom='" + nom + '\'' +
                ", Adresse='" + adresse + '\'' +
                ", Categorie='" + categorie + '\'' +
                ", has Photo = " + (photo != null) +
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
        dest.writeString(adresse);
        dest.writeByteArray(bitmapData);
    }
}
