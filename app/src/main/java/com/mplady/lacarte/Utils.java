package com.mplady.lacarte;


import com.mplady.lacarte.favori.Favori;

import java.util.ArrayList;

public class Utils {

    private static Utils instance;
    private static ArrayList<Favori> lieuxFavoris;

    public Utils() {
        if (null == lieuxFavoris)
            lieuxFavoris = new ArrayList<>();
    }

    public static Utils getInstance() {
        if (null == instance)
            instance = new Utils();
        return instance;
    }

    public static ArrayList<Favori> getLieuxFavoris() {
        return lieuxFavoris;
    }

    public static ArrayList<String> getCategorie() {
        ArrayList<String> categorieLieux = new ArrayList<>();
        for (Favori favori : lieuxFavoris) {
            String categorie = favori.getCategorie();
            categorieLieux.add(categorie);
        }
        return categorieLieux;
    }
}
