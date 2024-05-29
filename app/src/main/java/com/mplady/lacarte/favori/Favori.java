package com.mplady.lacarte.favori;

import androidx.annotation.NonNull;

public class Favori {
    private int id;
    private String imgLieuURL;
    private String nom;
    private String description;

    public Favori(int id, String imgLieuURL, String nom, String description) {
        this.id = id;
        this.imgLieuURL = imgLieuURL;
        this.nom = nom;
        this.description = description;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @NonNull
    @Override
    public String toString() {
        return "Favori{" +
                "id=" + id +
                ", imgLieuURL='" + imgLieuURL + '\'' +
                ", nom='" + nom + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}
