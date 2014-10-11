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

    public void getChapters(AsyncHttpResponseHandler handler) {
        String apiUri = getApiUrl("/chapters.json");
        //RequestParams params = new RequestParams();
        //params.put("since_id", "1");
        Log.d(TAG, "getChapters params");
        client.get(apiUri, null, handler);
    }
    public static void getEventsList(String chapterId, AsyncHttpResponseHandler handler){
        //String apiUri = getApiUrl("/event.json");
        String apiUri = "http://dev-v3.gotpantheon.com/noauth/event.json";
        RequestParams params = new RequestParams();
        if (chapterId == null && chapterId.isEmpty()){
            chapterId = "101";
        }
        params.put("chapter", chapterId);
        params.put("uid", "1");
        //client.get(apiUri, params, handler);
    }
}