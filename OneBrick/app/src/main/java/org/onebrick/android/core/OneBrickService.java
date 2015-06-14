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
    /**
     * Get a list of chapters
     *
     * @return a list of chapters
     */
    @GET("/chapters.json")
    Map<String, Chapter> getAllChapters();

    /**
     * get a list of events of a chapter
     *
     * @param chapterId
     * @return all events of a chapter
     */
    @GET("/event.json")
    List<Event> getAllEvents(@Query("chapter") int chapterId, @Query("nphotos") int numOfPhotos);

    /**
     * get an event detail
     *
     * @param eventId
     * @return event detail of a given event
     */
    @GET("/event/{eventId}.json")
    Event getEventDetail(@Path("eventId") long eventId);

    /**
     * get all my events (either past or upcoming)
     *
     * @param includePastEvents 1 or 0
     * @return all my events
     */
    @GET("/event.json")
    List<Event> getMyEvents(@Query("ukey") String ukey, @Query("includePastEvents") int includePastEvents, @Query("nphotos") int numOfPhotos);

    /**
     * verify a user
     *
     * @param ukey
     * @param cb callback return (success/fail) from a server
     */
    @GET("/verify.json")
    void verify(@Query("ukey") String ukey, Callback<String[]> cb);

    /**
     * rsvp to an event
     *
     * @param ukey
     * @param eventId
     * @param cb callback return for rsvp
     */
    @FormUrlEncoded
    @POST("/event/{eventId}/rsvp.json")
    void rsvp(@Field("ukey") String ukey, @Path("eventId") long eventId, Callback<RSVP> cb);

    /**
     * un-rsvp to an event
     *
     * @param ukey
     * @param eventId
     * @param cb callback return for un-rsvp
     */
    @FormUrlEncoded
    @POST("/event/{eventId}/unrsvp.json")
    void unrsvp(@Field("ukey") String ukey, @Path("eventId") long eventId, Callback<RSVP> cb);

    /**
     * search events
     *
     * @param chapterId
     * @param search
     * @return search results
     */
    @GET("/event.json")
    Response search(@Query("chapter") int chapterId, @Query("search") String search);
}
