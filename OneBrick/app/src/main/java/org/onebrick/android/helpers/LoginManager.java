package org.onebrick.android.helpers;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.onebrick.android.R;

public class LoginManager {
    private static LoginManager instance;

    private Context context;
    private String currentUserKey;

    private LoginManager(Context context) {
        this.context = context;
    }

    public static LoginManager getInstance(Context context) {
        if (instance == null) {
            instance = new LoginManager(context.getApplicationContext());
        }
        return instance;
    }

    public boolean isLoggedIn() {
        return !getCurrentUserKey().isEmpty();
    }

    public void setCurrentUserKey(@NonNull String currentUserKey){
        this.currentUserKey = currentUserKey;
    }

    @Nullable
    public String getCurrentUserKey() {
        if (this.currentUserKey != null && !this.currentUserKey.isEmpty()){
            return this.currentUserKey;
        }else{
            this.currentUserKey = getKeyFromSharePreferences();
        }
        return this.currentUserKey;
    }

    private String getKeyFromSharePreferences(){
        SharedPreferences sharedPref = context.getApplicationContext().getSharedPreferences(this.context.getString(R.string.preference_file_ukey), context.MODE_PRIVATE);
        return sharedPref.getString(this.context.getString(R.string.user_key), "");
    }
}
