package com.mplady.lacarte.favori;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

public class Favori implements Parcelable {
    private String nom, categorie;
    Bitmap bitmap;

    public Favori(String nom, String categorie, Bitmap bitmap) {
        this.nom = nom;
        this.categorie = categorie;
        this.bitmap = bitmap;
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
    public Bitmap getBitmap() {
        return bitmap;
    }
    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
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
