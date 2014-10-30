package org.onebrick.android;

import android.content.Context;
import android.util.Log;

import com.codepath.oauth.OAuthBaseClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.scribe.builder.api.Api;
import org.scribe.builder.api.TwitterApi;

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
public class OneBrickClient extends OAuthBaseClient {

    private static final String TAG = "OneBrickClient";

    public static final Class<? extends Api> REST_API_CLASS = TwitterApi.class; // Change this
    public static final String REST_URL = "http://dev-v3.gotpantheon.com/noauth"; // Change this, base API URL
    public static final String REST_CONSUMER_KEY = "CrsegfjpmNEX30TFdt6TEGXcS";       // Change this
    public static final String REST_CONSUMER_SECRET = "m3ZrCoSElkF2QuPQJBG0cF9YDkouIWCYtmCUpfdVAu6EnHbWiP"; // Change this
    public static final String REST_CALLBACK_URL = "oauth://onebrick-android"; // Change this (here and in manifest)

    public OneBrickClient(Context context) {
        super(context, REST_API_CLASS, REST_URL, REST_CONSUMER_KEY, REST_CONSUMER_SECRET, REST_CALLBACK_URL);
    }

    /*
        This Function access the endpoint to get the list of chapters.
     */
    public void getChapters(AsyncHttpResponseHandler handler) {
        String apiUri = getApiUrl("/chapters.json");
        Log.d(TAG, "getChapters params");
        client.get(apiUri, null, handler);
    }
    public void getEventsList(int chapterId, long userId, AsyncHttpResponseHandler handler){
        String apiUri = getApiUrl("/event.json");
        RequestParams params = new RequestParams();
        params.put("chapter", chapterId);
        if(userId > 1) {
            params.put("uid", userId);
        }
        client.get(apiUri, params, handler);
    }

    public void getUserLogin(String username, String password, AsyncHttpResponseHandler handler){
        String apiUri = getApiUrl("/user/login.json");
        apiUri = apiUri + "?username=" + username.trim() + "&password=" + password.trim();
//        String apiUri = "http://dev-v3.gotpantheon.com/noauth/user/login.json?username=" + username + "&password=" + password;
//        RequestParams params = new RequestParams();
//        params.put("username", username);
//        params.put("password", password);
//        Log.i(TAG,"get request for user login URL: "+apiUri);
        client.post(apiUri, null, handler);
    }

    public void getEventInfo(String eventId, long userId,AsyncHttpResponseHandler handler){
        String apiUri = getApiUrl("/event/"+eventId+".json");
        if(userId > 1) {
            apiUri = apiUri+"&uid="+userId;
        }

        Log.i(TAG,"get request for URL"+apiUri);
        client.get(apiUri, null, handler);
    }

    public void getLatLongFromAddress(String address, AsyncHttpResponseHandler handler){
        //String apiUri = getApiUrl("/event/"+eventId+".json");
        String apiUrl = "https://maps.googleapis.com/maps/api/geocode/json?address=1600+Amphitheatre+Parkway,+Mountain+View,+CA";
        //Log.i(TAG,"get request for URL"+apiUrl);
        client.get(apiUrl, null, handler);
    }

    /*
    This function is called to post rsvp request to an event
     */
    public void postRsvpToEvent(long eventId, long userId, AsyncHttpResponseHandler handler) {
        String apiUri = getApiUrl("/event/"+eventId+"/rsvp.json");
        RequestParams params = new RequestParams();
        params.put("uid", userId);
        client.post(apiUri, params, handler);
    }

    public void postUnRsvpToEvent(long eventId, long userId, AsyncHttpResponseHandler handler) {
        String apiUri = getApiUrl("/event/"+eventId+"/unrsvp.json");
        RequestParams params = new RequestParams();
        params.put("uid", userId);
        client.post(apiUri, params, handler);
    }

    public void getMyEvents(long userId, boolean isPastEvent, AsyncHttpResponseHandler handler){
        String apiUri = getApiUrl("/event.json");
        RequestParams params = new RequestParams();
        params.put("uid", userId);
        if (isPastEvent){
            params.put("includePastEvents", 1);
        }else{
            params.put("includePastEvents", 0);
        }
        client.get(apiUri, params, handler);
    }

    public void searchForEvents(int chapterId, String query, AsyncHttpResponseHandler handler) {
        String apiUri = getApiUrl("/event.json?chapter="+chapterId+"&"+"search="+query);
        Log.i("API","Calling"+apiUri);
        client.get(apiUri, null, handler);
    }
}