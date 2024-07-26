package com.mplady.lacarte.suggestions;

import androidx.annotation.NonNull;

public class Suggestion {
    private String nom, categorie, adresse;
    //Bitmap bitmap;

    public Suggestion(String nom, String categorie, String adresse) {
        this.nom = nom;
        this.categorie = categorie;
        this.adresse = adresse;
        //this.bitmap = bitmap;
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

    public String getAdresse() {
        return adresse;
    }

    public void setAdresse(String adresse) {
        this.adresse = adresse;
    }

    @NonNull
    @Override
    public String toString() {
        return "Suggestion{" +
                "nom='" + nom + '\'' +
                ", categorie='" + categorie + '\'' +
                '}';
    }
}
