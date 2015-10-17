package org.onebrick.android.core;

import org.onebrick.android.models.GeocodeResponse;

import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.Query;

public interface OneBrickMapService {

    //Googlemaps  api  Endpoint
    @GET("/maps/api/geocode/json")
    public void geocodeResponse(@Query("address") String address, Callback<GeocodeResponse> callback);
}
