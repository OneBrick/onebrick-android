package org.onebrick.android.models;

import android.provider.BaseColumns;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import org.json.JSONException;
import org.json.JSONObject;

@Table(name="user", id = BaseColumns._ID)
public class User extends Model {

    private static final String TAG = User.class.getName().toString();

    @Column(name="name")
    public String name;
    @Column(name="email")
    public String email;
    @Column(name="chapter_id")
    public int chapterId = -1; // San Francisco
    @Column(name="session_id")
    public String sessionId;
    @Column(name="session_name")
    public String sessionName;

    @Column(name="profile_image_uri")
    public String profileImageUri;

    /*
    This is value that will be used in RVP API. Needs to set to a valid
    needs to be set to a valid UID after successful authentication
    if this value is -1 ==> un authenticated. And login activity will be prompted.
     */
    @Column(name="uid")
    public long UID = -1;

    public static User fromJSON(JSONObject jsonObject){
        User user = new User();
        try{
            user.UID = jsonObject.getJSONObject("user").optLong("uid");
            user.name = jsonObject.getJSONObject("user").optString("signature");
            // regular user doesn't have mail object - probably only onebrick.org domanin
            user.email = jsonObject.getJSONObject("user").optString("name");
            //user.email = jsonObject.getJSONObject("user").optString("mail");
        }catch(JSONException e){
            e.printStackTrace();
            return null;
        }
        return user;
    }
    public long getUId() {
        return UID;
    }
    public void setUId(long uId) {
        this.UID = uId;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getChapterId() {
        return chapterId;
    }

    public void setChapterId(int chapterId) {
        this.chapterId = chapterId;
    }

    public String getProfileImageUri() {
        return "assets://images/profile1.png";
    }
}
