package com.mplady.lacarte;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.util.Arrays;
import java.util.Objects;

@Entity(tableName = "Favoris")
public class Place implements Parcelable {

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
    private String sousCategorie;

    public Place(@NonNull String nom, String categorie, byte[] bitmapData, String adresse) {
        this.nom = nom;
        this.categorie = categorie;
        this.bitmapData = bitmapData;
        this.adresse = adresse;
    }

    public Place(@NonNull String nom, String categorie, Bitmap photo, String adresse, String sousCategorie) {
        this.nom = nom;
        this.categorie = categorie;
        this.adresse = adresse;
        this.photo = photo;
        this.sousCategorie = sousCategorie;
    }

    protected Place(Parcel in) {
        nom = Objects.requireNonNull(in.readString());
        categorie = in.readString();
        adresse = in.readString();
        bitmapData = in.createByteArray();
    }

    public static final Creator<Place> CREATOR = new Creator<Place>() {
        @Override
        public Place createFromParcel(Parcel in) {
            return new Place(in);
        }

        @Override
        public Place[] newArray(int size) {
            return new Place[size];
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
    public String getSousCategorie() {
        return sousCategorie;
    }


    @NonNull
    @Override
    public String toString() {
        return "Favori{" +
                "Nom='" + nom + '\'' +
                ", Adresse='" + adresse + '\'' +
                ", Categorie='" + categorie + '\'' +
                ", has Photo = " + (photo != null) +
                ", has bitmapData = " + Arrays.toString(bitmapData) +
                ", sousCategorie='" + (sousCategorie != null) + '\'' +
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
