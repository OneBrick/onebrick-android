package org.onebrick.android.core;

import android.os.AsyncTask;
import android.util.Log;

import org.scribe.builder.ServiceBuilder;
import org.scribe.builder.api.SimpleGeoApi;
import org.scribe.model.OAuthConstants;
import org.scribe.model.OAuthRequest;
import org.scribe.model.Response;
import org.scribe.model.SignatureType;
import org.scribe.model.Verb;
import org.scribe.oauth.OAuthService;

public class OneBrickClient2
{
    public static void getChapters() {
        final OAuthService service = new ServiceBuilder()
                .provider(SimpleGeoApi.class)
                .apiKey("")
                .apiSecret("")
                .signatureType(SignatureType.Header)
                .build();

        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                OAuthRequest request = new OAuthRequest(Verb.GET, "http://dev-v3.gotpantheon.com/auth//chapters.json");
                service.signRequest(OAuthConstants.EMPTY_TOKEN, request);
                Response response = request.send();
                Log.w("PRAKASH0: ", "Got it! Lets see what we found...");
                Log.w("PRAKASH1: ", response.getCode() + " ");
                Log.w("PRAKASH2: ", response.getBody());
                return null;
            }
        }.execute();
    }
}