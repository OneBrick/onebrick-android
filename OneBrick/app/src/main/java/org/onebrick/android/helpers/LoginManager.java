package org.onebrick.android.helpers;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

public class LoginManager {
    private static final String PREF_ENCRYPTED_KEY = "encrupted_key";

    private static LoginManager instance;
    private Context mContext;
    private String mCurrentUserKey;

    private LoginManager(Context context) {
        mContext = context.getApplicationContext();
    }

    public static LoginManager getInstance(Context context) {
        if (instance == null) {
            if (context == null) {
                throw new IllegalStateException("Login manager should have initialized by now!");
            }
            instance = new LoginManager(context.getApplicationContext());
        }
        return instance;
    }

    public boolean isLoggedIn() {
        return !TextUtils.isEmpty(getCurrentUserKey());
    }

    public void setCurrentUserKey(@NonNull String key) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(mContext);
        sharedPref.edit().putString(PREF_ENCRYPTED_KEY, key).apply();
        mCurrentUserKey = key;
    }

    @Nullable
    public String getCurrentUserKey() {
        if (TextUtils.isEmpty(mCurrentUserKey)) {
            SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(mContext);
            mCurrentUserKey = sharedPref.getString(PREF_ENCRYPTED_KEY, "");
        }
        return mCurrentUserKey;
    }
}
