package org.onebrick.android.core;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.ContentResolver;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.onebrick.android.BuildConfig;
import org.onebrick.android.helpers.Utils;
import org.onebrick.android.models.Chapter;
import org.onebrick.android.models.Event;
import org.onebrick.android.models.RSVP;
import org.onebrick.android.providers.OneBrickContentProvider;
import org.onebrick.android.sync.SyncAdapter;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.client.OkClient;
import retrofit.converter.GsonConverter;
import retrofit.client.Response;
import se.akerfeldt.signpost.retrofit.RetrofitHttpOAuthConsumer;
import se.akerfeldt.signpost.retrofit.SigningOkClient;

public class OneBrickRESTClient {
    private static OneBrickRESTClient sInstance;

    // The authority for the sync adapter's content provider
    // An account type, in the form of a domain name
    public static final String ACCOUNT_TYPE = "onebrick.org";
    // The account name
    public static final String ACCOUNT = "SyncAdapterAccount";

    private Account mAccount;

    private OneBrickService mRestService;

    private OneBrickRESTClient(Context appContext) {
        mAccount = createSyncAccount(appContext);

        final Gson gson = new GsonBuilder()
                .registerTypeAdapter(Chapter.class, new Chapter.ChapterJsonDeserializer())
                .registerTypeAdapter(Event.class, new Event.EventJsonDeserializer())
                .create();

        RetrofitHttpOAuthConsumer oAuthConsumer = new RetrofitHttpOAuthConsumer(
                BuildConfig.CONSUMER_KEY, BuildConfig.CONSUMER_SECRET);
        oAuthConsumer.setTokenWithSecret("", "");
        OkClient client = new SigningOkClient(oAuthConsumer);

        final RestAdapter.Builder builder = new RestAdapter.Builder()
                .setEndpoint("http://dev-v3.gotpantheon.com/auth")
                .setConverter(new GsonConverter(gson))
                .setClient(client);

        if (Utils.isDebug()) {
            builder.setLogLevel(RestAdapter.LogLevel.FULL);
        }
        mRestService = builder.build().create(OneBrickService.class);
    }

    public static void init(Context appContext) {
        if (sInstance != null) {
            throw new IllegalStateException("OneBrickRESTClient is already initialized");
        }
        sInstance = new OneBrickRESTClient(appContext);
    }

    public static OneBrickRESTClient getInstance() {
        if (sInstance == null) {
            throw new IllegalStateException("OneBrickRESTClient is not initialized, call init() first");
        }
        return sInstance;
    }

    /**
     * Create a new dummy account for the sync adapter
     *
     * @param context The application context
     */
    private Account createSyncAccount(Context context) {
        // Create the account type and default account
        Account newAccount = new Account(ACCOUNT, ACCOUNT_TYPE);
        AccountManager accountManager = (AccountManager) context.getSystemService(Context.ACCOUNT_SERVICE);
        /*
         * Add the account and account type, no password or user data
         * If successful, return the Account object, otherwise report an error.
         */
        accountManager.addAccountExplicitly(newAccount, null, null);
        return newAccount;
    }

    public OneBrickService getRestService() {
        return mRestService;
    }

    public void requestChapters() {
        final Bundle settingsBundle = new Bundle();
        settingsBundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        settingsBundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        settingsBundle.putInt(SyncAdapter.EXTRA_SYNC_TYPE, SyncAdapter.SYNC_CHAPTERS);
        ContentResolver.requestSync(mAccount, OneBrickContentProvider.AUTHORITY, settingsBundle);
    }

    public void requestEvents(int chapterId) {
        final Bundle settingsBundle = new Bundle();
        settingsBundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        settingsBundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        settingsBundle.putInt(SyncAdapter.EXTRA_SYNC_TYPE, SyncAdapter.SYNC_EVENTS);
        settingsBundle.putInt(SyncAdapter.EXTRA_CHAPTER_ID, chapterId);
        ContentResolver.requestSync(mAccount, OneBrickContentProvider.AUTHORITY, settingsBundle);
    }

    /**
     * Call retrofit asynchronously
     */
    public void verifyLogin(@NonNull String ukey, Callback<String[]> cb) {
        mRestService.verify(ukey, cb);
    }

    public void eventInfo(int eventId) {

    }

    public void rsvp(@NonNull String ukey, @NonNull long eventId, Callback<RSVP> cb) {
        mRestService.rsvp(ukey, eventId, cb);
    }

    public void unrsvp(@NonNull String ukey, @NonNull long eventId, Callback<RSVP> cb) {
        mRestService.unrsvp(ukey, eventId, cb);
    }

    public void myEvents(int includePastEvents) {

    }

    public void search(int chapterId, String search) {

    }
}
