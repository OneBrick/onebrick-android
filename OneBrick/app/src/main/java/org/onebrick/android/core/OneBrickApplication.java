package org.onebrick.android.core;

import android.app.Application;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.newrelic.agent.android.NewRelic;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

public class OneBrickApplication extends Application {
    private static final String TAG = "OneBrickApplication";

    private static final String PREF_CHAPTER_NAME = "CHAPTER_NAME";
    private static final String PREF_CHAPTER_ID = "CHAPTER_ID";

    private static OneBrickApplication sInstance;

    public static OneBrickApplication getInstance() {
        return sInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        sInstance = this;

        OneBrickRESTClient.init(this);

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
    }

    public OneBrickClient getRestClient() {
        return (OneBrickClient) OneBrickClient.getInstance(OneBrickClient.class, this);
    }


    public void setChapterName(@NonNull String chapterName) {
        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        prefs.edit().putString(PREF_CHAPTER_NAME, chapterName).apply();
    }

    @Nullable
    public String getChapterName() {
        return PreferenceManager.getDefaultSharedPreferences(this).getString(PREF_CHAPTER_NAME, null);
    }

    public void setChapterId(int chapterId) {
        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        prefs.edit().putInt(PREF_CHAPTER_ID, chapterId).apply();
    }

    @Nullable
    public int getChapterId() {
        return PreferenceManager.getDefaultSharedPreferences(this).getInt(PREF_CHAPTER_ID, -1);
    }
}