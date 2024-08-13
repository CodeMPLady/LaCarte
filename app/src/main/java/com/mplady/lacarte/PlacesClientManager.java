package com.mplady.lacarte;

import android.content.Context;

import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.net.PlacesClient;

public class PlacesClientManager {
    private static PlacesClient placesClient;

    public static synchronized PlacesClient getPlacesClient(Context context) {
        if (placesClient == null) {
            Places.initialize(context.getApplicationContext(), BuildConfig.MAPS_API_KEY);
            placesClient = Places.createClient(context.getApplicationContext());
        }
        return placesClient;
    }
}
