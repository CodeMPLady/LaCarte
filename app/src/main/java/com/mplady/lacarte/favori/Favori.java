package com.mplady.lacarte.favori;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

public class Favori implements Parcelable {
    private String imgLieuURL, nom, categorie;

    public Favori(String imgLieuURL, String nom, String categorie) {
        this.imgLieuURL = imgLieuURL;
        this.nom = nom;
        this.categorie = categorie;
    }


    protected Favori(Parcel in) {
        imgLieuURL = in.readString();
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

    public String getImgLieuURL() {
        return imgLieuURL;
    }
    public void setImgLieuURL(String imgLieuURL) {
        this.imgLieuURL = imgLieuURL;
    }
    public String getNom() {
        return nom;
    }
    public void setNom(String nom) {
        this.nom = nom;
    }
    public String getCategorie() {
        return categorie;
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
