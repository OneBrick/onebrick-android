package org.onebrick.android.helpers;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.support.annotation.NonNull;
import android.util.Log;

import org.onebrick.android.core.OneBrickMapRESTClient;
import org.onebrick.android.models.GeocodeResponse;

import java.io.IOException;
import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class GeoCodeHelper {

    private static final String TAG = "GeoCodeHelper";

    private static final String GEOCODE_BASE_URL = "https://maps.googleapis.com/";

    public static void getGeoCode(@NonNull Context context, @NonNull String address, @NonNull final GeoCoderCallback callback) {
        try {
            List<Address> geocodeMatches = new Geocoder(context).getFromLocationName(address, 1);
            if (!geocodeMatches.isEmpty()) {
                double latitude = geocodeMatches.get(0).getLatitude();
                double longitude = geocodeMatches.get(0).getLongitude();
                Log.d(TAG, "latitude: " + latitude);
                Log.d(TAG, "longitude" + longitude);

                callback.onResponse(latitude, longitude);
            } else {
                OneBrickMapRESTClient.getInstance().geocodeResponse(address, new Callback<GeocodeResponse>() {
                    @Override
                    public void success(GeocodeResponse geocodeResponse, Response response) {
                        if (geocodeResponse.isSuccess()) {
                            Log.d(TAG, "GeoResults: " + geocodeResponse.getLatitude());
                            Log.d(TAG, "GeoResults: " + geocodeResponse.getLongitude());
                            callback.onResponse(geocodeResponse.getLatitude(), geocodeResponse.getLongitude());
                        }
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        Log.e(TAG, "GeocodeResponse: Failed to parse Geocode Response", error);
                    }
                });
            }
        } catch (IOException e) {
            Log.e("ERROR", "Error to retrieve geocode", e);
        }
    }

    public interface GeoCoderCallback {
        void onResponse(double  latitude, double longitude);
    }
}
