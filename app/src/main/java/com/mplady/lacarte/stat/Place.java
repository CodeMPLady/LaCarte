package com.mplady.lacarte.stat;

import android.widget.ImageView;

public class Place {
    private int id;
    private String imgPlaceURL;
    private String name;
    private String categorie;
    private String nbVisites;

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

    public void setImgPlace(String imgPlace) {
        this.imgPlaceURL = imgPlaceURL;
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

    public void setCategorie(String categorie) {
        this.categorie = categorie;
    }

    public String getNbVisites() {
        return nbVisites;
    }

    public void setNbVisites(String nbVisites) {
        this.nbVisites = nbVisites;
    }

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
