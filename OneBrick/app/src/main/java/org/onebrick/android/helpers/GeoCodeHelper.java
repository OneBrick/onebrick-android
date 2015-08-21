package org.onebrick.android.helpers;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.support.annotation.NonNull;
import android.util.Log;

import java.io.IOException;
import java.util.List;

public class GeoCodeHelper {

    private static List<Address> geocodeMatches = null;

    public static List<Address> getGeoCode(@NonNull Context context, @NonNull String address){
        try {
            geocodeMatches = new Geocoder(context).getFromLocationName(address, 1);

            if (!geocodeMatches.isEmpty())
            {
//                double latitude = geocodeMatches.get(0).getLatitude();
//                double longitude = geocodeMatches.get(0).getLongitude();
//                Log.d("latitude: ", latitude + "");
//                Log.d("longitude", longitude + "");

                return geocodeMatches;
            }

        } catch (IOException e) {
            Log.e("ERROR", "Error to retrieve geocode" + e);
            e.printStackTrace();
        }
        return geocodeMatches;
    }
}
