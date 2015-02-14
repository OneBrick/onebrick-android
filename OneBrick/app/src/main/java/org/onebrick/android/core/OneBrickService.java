package org.onebrick.android.core;

import org.onebrick.android.models.Chapter;

import java.util.Map;

import retrofit.http.GET;

public interface OneBrickService {
    @GET("/chapters.json")
    Map<String, Chapter> getAllChapters();


}
