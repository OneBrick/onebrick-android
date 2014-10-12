package org.onebrick.android;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

public class OneBrickApplication extends com.activeandroid.app.Application {
    private static final String TAG = "OneBrickApplication";

    private static Context context;
    @Override
    public void onCreate() {
        super.onCreate();
        OneBrickApplication.context = this;

        // Create global configuration and initialize ImageLoader with this configuration
        DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder().
                cacheInMemory(true).cacheOnDisk(true).build();
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(getApplicationContext())
                .defaultDisplayImageOptions(defaultOptions)
                .build();
        ImageLoader.getInstance().init(config);
    }

    public static OneBrickClient getRestClient() {
        return (OneBrickClient) OneBrickClient.getInstance(OneBrickClient.class, OneBrickApplication.context);
    }
}