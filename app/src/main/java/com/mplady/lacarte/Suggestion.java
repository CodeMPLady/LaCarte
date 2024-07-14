package com.mplady.lacarte;

import android.graphics.Bitmap;

public class Suggestion {
    private String nom, categorie;
    Bitmap bitmap;

    public Suggestion(String nom, String categorie, Bitmap bitmap) {
        this.nom = nom;
        this.categorie = categorie;
        this.bitmap = bitmap;
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

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    @Override
    public String toString() {
        return "Suggestion{" +
                "nom='" + nom + '\'' +
                ", categorie='" + categorie + '\'' +
                ", bitmap=" + bitmap +
                '}';
    }
}
