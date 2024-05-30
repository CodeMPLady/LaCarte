package com.mplady.lacarte.favori;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;

public class Favori {
    private int id;
    private String imgLieuURL;
    private String nom;
    private String categorie;


    public Favori(int id, String imgLieuURL, String nom, String categorie) {
        this.id = id;
        this.imgLieuURL = imgLieuURL;
        this.nom = nom;
        this.categorie = categorie;
    }
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

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
                "id=" + id +
                ", imgLieuURL='" + imgLieuURL + '\'' +
                ", nom='" + nom + '\'' +
                ", description='" + categorie + '\'' +
                '}';
    }
}
