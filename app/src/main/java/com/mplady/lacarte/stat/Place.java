package com.mplady.lacarte.stat;

import androidx.annotation.NonNull;

public class Place {
    private int id;
    private final String imgPlaceURL, categorie, nbVisites;
    private String name;

    public Place(int id, String imgPlaceURL, String name, String categorie, String nbVisites) {
        this.id = id;
        this.imgPlaceURL = imgPlaceURL;
        this.name = name;
        this.categorie = categorie;
        this.nbVisites = nbVisites;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getImgPlace() {
        return imgPlaceURL;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategorie() {
        return categorie;
    }

    public String getNbVisites() {
        return nbVisites;
    }

    @NonNull
    @Override
    public String toString() {
        return "Place{" +
                "id=" + id +
                ", imgPlace=" + imgPlaceURL +
                ", name='" + name + '\'' +
                ", categorie='" + categorie + '\'' +
                ", nbVisites='" + nbVisites + '\'' +
                '}';
    }
}
