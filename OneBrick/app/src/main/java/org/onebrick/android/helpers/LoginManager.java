package org.onebrick.android.helpers;

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

    public void setCurrentUser(@NonNull User user) {
        currentUser = user;
    }

    @Nullable
    public User getCurrentUser() {
        return currentUser;
    }
}
