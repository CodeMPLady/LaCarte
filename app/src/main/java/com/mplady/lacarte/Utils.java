package com.mplady.lacarte;


import com.mplady.lacarte.favori.Favori;

import java.util.ArrayList;

public class Utils {

    private static Utils instance;
    private static ArrayList<Favori> lieuxFavoris;

    public Utils() {
        if (null == lieuxFavoris) {
            lieuxFavoris = new ArrayList<>();
            initData();
        }
    }

    private void initData() {
        //lieuxFavoris.add(new Favori("imageLieu", "Shin-ya Ramen", "Restaurant"));
        //lieuxFavoris.add(new Favori("imageLieu", "Leclerc", "Supermarche"));
    }

    public static Utils getInstance() {
        if (null == instance) {
            instance = new Utils();
        }
        return instance;
    }

    public static ArrayList<Favori> getLieuxFavoris() {
        return lieuxFavoris;
    }
}
