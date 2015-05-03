package org.onebrick.android.core;

import org.onebrick.android.models.Chapter;
import org.onebrick.android.models.Event;

import java.util.List;
import java.util.Map;

import retrofit.client.Response;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.PUT;
import retrofit.http.Path;
import retrofit.http.Query;

public interface OneBrickService {
    @GET("/chapters.json")
    Map<String, Chapter> getAllChapters();

    @GET("/event.json")
    List<Event> getAllEvents(@Query("chapter") int chapterId);

    @GET("/event/{eventId}.json")
    Response eventInfo(@Query("ukey") String ukey, @Path("eventId") int eventId);

//    @POST("/user/login.json")
//    @FormUrlEncoded
//    Response login(@Field(""));

    @POST("/event/{eventId}/rsvp.json")
    Response rsvp(@Query("ukey") String ukey, @Path("eventId") int eventId);

    @PUT("/event/{eventId}/unrsvp.json")
    Response unrsvp(@Query("ukey") String ukey);

    /**
     *
     * @param includePastEvents 1 or 0
     */
    @GET("/event.json")
    Response myEvents(@Query("ukey") String ukey, @Query("includePastEvents") int includePastEvents);

    @GET("/event.json")
    Response search(@Query("chapter") int chapterId, @Query("search") String search);
}
