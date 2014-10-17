package org.onebrick.android;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.onebrick.android.models.User;

public final class LoginManager {
    private static LoginManager instance;

    private Context context;
    private User currentUser;

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
        return currentUser != null;
    }

    public void requestLogin() {
        // 1. start activity/webview to login
        // 2. create currentUser object with all the information

        // dummy user for now
        currentUser = new User();
        currentUser.setFirstName("Brad");
        currentUser.setLastName("Pitt");
        currentUser.setEmail("brad@angelina.com");
    }

    @Nullable
    public User getCurrentUser() {
        return currentUser;
    }
}
