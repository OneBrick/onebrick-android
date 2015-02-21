package org.onebrick.android.core;

import org.onebrick.android.models.Chapter;
import org.onebrick.android.models.Event;

import java.util.List;
import java.util.Map;

import retrofit.http.GET;
import retrofit.http.Query;

public interface OneBrickService {
    @GET("/chapters.json")
    Map<String, Chapter> getAllChapters();

    @GET("/event.json")
    List<Event> getAllEvents(@Query("chapter") int chapterId);
}
