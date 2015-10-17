package org.onebrick.android.core;


import android.support.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.onebrick.android.helpers.Utils;
import org.onebrick.android.models.GeocodeResponse;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.converter.GsonConverter;

public class OneBrickMapRESTClient {
    private static OneBrickMapRESTClient sInstance;
    private OneBrickMapService mRestService;

    private OneBrickMapRESTClient() {
        final Gson gson = new GsonBuilder()
                .create();


        final RestAdapter.Builder builder = new RestAdapter.Builder()
                .setEndpoint("https://maps.googleapis.com/")
                .setConverter(new GsonConverter(gson));

        if (Utils.isDebug()) {
            builder.setLogLevel(RestAdapter.LogLevel.FULL);
        }
        mRestService = builder.build().create(OneBrickMapService.class);
    }
    public static void init() {
        if (sInstance != null) {
            throw new IllegalStateException("OneBrickMapRESTClient is already initialized");
        }
        sInstance = new OneBrickMapRESTClient();
    }

    public static OneBrickMapRESTClient getInstance() {
        if (sInstance == null) {
            throw new IllegalStateException("OneBrickMapRESTClient is not initialized, call init() first");
        }
        return sInstance;
    }

    public void geocodeResponse(@NonNull String address,  Callback<GeocodeResponse> cb) {
        mRestService.geocodeResponse(address, cb);
    }
}
