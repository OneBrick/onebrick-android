package org.onebrick.android.core;

import android.content.Context;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

/*
 * 
 * This is the object responsible for communicating with a REST API. 
 * Specify the constants below to change the API being communicated with.
 * See a full list of supported API classes: 
 *   https://github.com/fernandezpablo85/scribe-java/tree/master/src/main/java/org/scribe/builder/api
 * Key and Secret are provided by the developer site for the given API i.e dev.twitter.com
 * Add methods for each relevant endpoint in the API.
 * 
 * NOTE: You may want to rename this object based on the service i.e TwitterClient or FlickrClient
 * 
 */
public class OneBrickClient {

    private static final String TAG = "OneBrickClient";

    //public static final Class<? extends Api> REST_API_CLASS = SimpleGeoApi.class; // Change this
    public static final String REST_URL = "http://dev-v3.gotpantheon.com/auth"; // Change this, base API URL
    public static final String REST_CONSUMER_KEY = "";       // Change this
    public static final String REST_CONSUMER_SECRET = ""; // Change this
    public static final String REST_CALLBACK_URL = "oauth://onebrick-android"; // Change this (here and in manifest)

    private static final String PATH_SEPARATOR = "/";
    private static final String GET_CHAPTERS_END_POINT = "chapters.json";
    private static final String GET_EVENT_END_POINT = "event.json";
    private static final String GET_LOGIN_END_POINT = "user/login.json";
    private static final String RSVP_END_POINT = "rsvp.json";
    private static final String UN_RSVP_END_POINT = "unrsvp.json";
    private static final String EVENT_PATH = "event";
    private static final String JSON_FILE_EXTENSION = ".json";

    public OneBrickClient(Context context) {
        //super(context, REST_API_CLASS, REST_URL, REST_CONSUMER_KEY, REST_CONSUMER_SECRET, REST_CALLBACK_URL);
    }

    /*
        This Function access the endpoint to get the list of chapters.
     */
    public void getChapters(AsyncHttpResponseHandler handler) {
//        String apiUri = getApiUrl(PATH_SEPARATOR + GET_CHAPTERS_END_POINT);
//        client.get(apiUri, null, handler);
    }
    public void getEventsList(int chapterId, long userId, AsyncHttpResponseHandler handler){
//        String apiUri = getApiUrl(PATH_SEPARATOR + GET_EVENT_END_POINT);
        RequestParams params = new RequestParams();
        params.put("chapter", Integer.toString(chapterId));
        if(userId > 1) {
            params.put("uid", Long.toString(userId));
        }
//        client.get(apiUri, params, handler);
    }

    public void getUserLogin(String username, String password, AsyncHttpResponseHandler handler){
        StringBuilder apiUri = new StringBuilder();
        //apiUri.append(getApiUrl(PATH_SEPARATOR + GET_LOGIN_END_POINT));
        // TODO redundant parameters?
        apiUri.append("?username=");
        apiUri.append(username.trim());
        apiUri.append("&password=");
        apiUri.append(password.trim());

        RequestParams params = new RequestParams();
        params.put("username", username.trim());
        params.put("password", password.trim());
        //client.post(apiUri.toString(), params, handler);
    }

    public void getEventInfo(String eventId, long userId,AsyncHttpResponseHandler handler){
        StringBuilder apiUri = new StringBuilder();
        //apiUri.append(getApiUrl(PATH_SEPARATOR + EVENT_PATH + PATH_SEPARATOR + eventId + JSON_FILE_EXTENSION));
        RequestParams params = null;
        if(userId > 1) {
            params = new RequestParams();
            params.put("uid", Long.toString(userId));
        }
        //client.get(apiUri.toString(), params, handler);
    }

    /*
    This function is called to post rsvp request to an event
     */
    public void postRsvpToEvent(long eventId, long userId, AsyncHttpResponseHandler handler) {
        //String apiUri = getApiUrl(PATH_SEPARATOR + EVENT_PATH + PATH_SEPARATOR + eventId + PATH_SEPARATOR + RSVP_END_POINT);
        RequestParams params = new RequestParams();
        params.put("uid", Long.toString(userId));
        //client.post(apiUri, params, handler);
    }

    public void postUnRsvpToEvent(long eventId, long userId, AsyncHttpResponseHandler handler) {
        //String apiUri = getApiUrl(PATH_SEPARATOR + EVENT_PATH + PATH_SEPARATOR + eventId + PATH_SEPARATOR + UN_RSVP_END_POINT);
        RequestParams params = new RequestParams();
        params.put("uid", Long.toString(userId));
        //client.post(apiUri, params, handler);
    }

    public void getMyEvents(long userId, boolean isPastEvent, AsyncHttpResponseHandler handler){
        //String apiUri = getApiUrl(PATH_SEPARATOR + GET_EVENT_END_POINT);
        RequestParams params = new RequestParams();
        params.put("uid", Long.toString(userId));
        if (isPastEvent){
            params.put("includePastEvents", Long.toString(1));
        }else{
            params.put("includePastEvents", Long.toString(0));
        }
        //client.get(apiUri, params, handler);
    }

    public void searchForEvents(int chapterId, String query, AsyncHttpResponseHandler handler) {
        //String apiUri = getApiUrl(PATH_SEPARATOR + GET_EVENT_END_POINT);
        RequestParams params = new RequestParams();
        params.put("chapter", chapterId);
        params.put("search", query);
        //client.get(apiUri, params, handler);
    }
}