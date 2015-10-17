package org.onebrick.android.core;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.activeandroid.app.Application;
//import com.newrelic.agent.android.NewRelic;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.path.android.jobqueue.JobManager;
import com.path.android.jobqueue.config.Configuration;
import com.path.android.jobqueue.log.CustomLogger;
import com.squareup.otto.Bus;

import org.onebrick.android.helpers.Utils;

public class OneBrickApplication extends Application {
    private static final String TAG = "OneBrickApplication";

    private static final String PREF_CHAPTER_NAME = "CHAPTER_NAME";
    private static final String PREF_CHAPTER_ID = "CHAPTER_ID";

    private static OneBrickApplication sInstance;

    public static OneBrickApplication getInstance() {
        return sInstance;
    }

    private JobManager mJobManager;
    private Bus mBus;

    @Override
    public void onCreate() {
        super.onCreate();

        sInstance = this;

        OneBrickRESTClient.init();
        OneBrickMapRESTClient.init();

//        NewRelic.withApplicationToken(
//                "AAd5aec03c54ce6bd6d21ae5b4168b5342bf276e97"
//        ).start(this);

        // Create global configuration and initialize ImageLoader with this configuration
        DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder().
                cacheInMemory(true).cacheOnDisk(true).build();
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(getApplicationContext())
                .defaultDisplayImageOptions(defaultOptions)
                .build();
        ImageLoader.getInstance().init(config);

        mBus = new Bus();
        initJobManager();
    }

    private void initJobManager() {
        final Configuration config = new Configuration.Builder(this)
                .minConsumerCount(1)
                .consumerKeepAlive(120) // 2 minutes
                .customLogger(new CustomLogger() {
                    private static final String TAG = "JOBS";

                    @Override
                    public boolean isDebugEnabled() {
                        return Utils.isDebug();
                    }

                    @Override
                    public void d(String text, Object... args) {
                        Log.d(TAG, String.format(text, args));
                    }

                    @Override
                    public void e(Throwable t, String text, Object... args) {
                        Log.e(TAG, String.format(text, args), t);
                    }

                    @Override
                    public void e(String text, Object... args) {
                        Log.e(TAG, String.format(text, args));
                    }
                })
                .build();

        mJobManager = new JobManager(this, config);
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

    public int getChapterId() {
        return PreferenceManager.getDefaultSharedPreferences(this).getInt(PREF_CHAPTER_ID, -1);
    }

    public JobManager getJobManager() {
        return mJobManager;
    }

    public Bus getBus() {
        return mBus;
    }
}