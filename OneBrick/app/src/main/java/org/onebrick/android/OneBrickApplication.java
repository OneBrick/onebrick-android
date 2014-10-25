package org.onebrick.android;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.newrelic.agent.android.NewRelic;

public class OneBrickApplication extends com.activeandroid.app.Application {
    private static final String TAG = "OneBrickApplication";
    private static Context context;
    private static SharedPreferences sharedPref;


    @Override
    public void onCreate() {
        super.onCreate();
        OneBrickApplication.context = this;

        NewRelic.withApplicationToken(
                "AAd5aec03c54ce6bd6d21ae5b4168b5342bf276e97"
        ).start(this);

        // Create global configuration and initialize ImageLoader with this configuration
        DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder().
                cacheInMemory(true).cacheOnDisk(true).build();
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(getApplicationContext())
                .defaultDisplayImageOptions(defaultOptions)
                .build();
        ImageLoader.getInstance().init(config);
        sharedPref = this.getSharedPreferences("OneBrickSharedPref", Context.MODE_PRIVATE);

    }

    public static OneBrickClient getRestClient() {
        return (OneBrickClient) OneBrickClient.getInstance(OneBrickClient.class,
                OneBrickApplication.context);
    }

    public static SharedPreferences getApplicationSharedPreference() {
        return sharedPref;
    }

    public static Context getContext() {
        return OneBrickApplication.context;
    }



}