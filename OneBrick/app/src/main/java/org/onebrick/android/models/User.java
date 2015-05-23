package org.onebrick.android.models;

import android.support.annotation.Nullable;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

public class User {

    private static final String TAG = "User";
    public String name;
    public String email;
    public String profileImageUri;
    public long userId = -1;

    @Nullable
    public static User fromJSON(JSONObject jsonObject){
        User user = new User();
        try {
            user.userId = jsonObject.getJSONObject("user").optLong("uid");
            user.name = jsonObject.getJSONObject("user").optString("signature");
            user.email = jsonObject.getJSONObject("user").optString("name");
        } catch(JSONException e){
            Log.e(TAG, "unable to create user", e);
            return null;
        }
        return user;
    }
    public long getUserId() {
        return userId;
    }
    public String getName() {
        return name;
    }
    public String getEmail() {
        return email;
    }
    public String getProfileImageUri() {
        return "assets://images/profile1.png";
    }
}
