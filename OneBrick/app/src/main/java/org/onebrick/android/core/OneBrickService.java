package org.onebrick.android.core;

import org.onebrick.android.models.Chapter;
import org.onebrick.android.models.Event;

import java.util.List;
import java.util.Map;

import retrofit.Callback;
import retrofit.client.Response;
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

//    @POST("/user/login.json")
//    @FormUrlEncoded
//    Response login(@Field("username") String username, @Field("password") String password);

    @POST("/event/{eventId}/rsvp.json")
    Response rsvp(@Query("ukey") String ukey, @Path("eventId") int eventId);

    @PUT("/event/{eventId}/unrsvp.json")
    Response unrsvp(@Query("ukey") String ukey, @Path("eventId") int eventId);

    /**
     *
     * @param includePastEvents 1 or 0
     */
    @GET("/event.json")
    Response myEvents(@Query("ukey") String ukey, @Query("includePastEvents") int includePastEvents);

    @GET("/event.json")
    Response search(@Query("chapter") int chapterId, @Query("search") String search);
}
