package org.onebrick.android.core;

import org.onebrick.android.models.Chapter;
import org.onebrick.android.models.Event;
import org.onebrick.android.models.RSVP;

import java.util.List;
import java.util.Map;

import retrofit.Callback;
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

    @GET("/verify.json")
    void verify(@Query("ukey") String ukey, Callback<String[]> cb);

    @FormUrlEncoded
    @POST("/event/{eventId}/rsvp.json")
    void rsvp(@Field("ukey") String ukey, @Path("eventId") long eventId, Callback<RSVP> cb);

    @FormUrlEncoded
    @POST("/event/{eventId}/unrsvp.json")
    void unrsvp(@Field("ukey") String ukey, @Path("eventId") long eventId, Callback<RSVP> cb);

    /**
     *
     * @param includePastEvents 1 or 0
     */
    @GET("/event.json")
    List<Event> myEvents(@Query("ukey") String ukey, @Query("includePastEvents") int includePastEvents);

    @GET("/event.json")
    Response search(@Query("chapter") int chapterId, @Query("search") String search);
}
