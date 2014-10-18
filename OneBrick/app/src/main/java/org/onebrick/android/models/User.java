package org.onebrick.android.models;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import org.json.JSONException;
import org.json.JSONObject;

@Table(name="User")
public class User extends Model {

    private static final String TAG = User.class.getName().toString();

    @Column(name="FirstName")
    public String firstName;
    @Column(name="LastName")
    public String lastName;
    @Column(name="Email")
    public String email;
    @Column(name="ChapterId")
    public int chapterId = -1; // San Francisco
    @Column(name="SessionId")
    public String sessionId;
    @Column(name="SessionName")
    public String sessionName;
    /*
    This is value that will be used in RVP API. Needs to set to a valid
    needs to be set to a valid UID after successful authentication
    if this value is -1 ==> un authenticated. And login activity will be prompted.
     */
    @Column(name="UID")
    public int UID = -1;

    private String profileImageUrl;

    public static User fromJSON(JSONObject jsonObject){
        User user = new User();
        try{
            user.uId = jsonObject.getJSONObject("user").optString("uid");
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
    public String getUId() {
        return uId;
    }
    public void setUId(String uId) {
        this.uId = uId;
    }
    public String getProfileImageUrl() {
        return profileImageUrl;
    }
    public void setProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }

//    public String getFirstName() {
//        return firstName;
//    }
//
//    public void setFirstName(String firstName) {
//        this.firstName = firstName;
//    }
//
//    public String getLastName() {
//        return lastName;
//    }
//
//    public void setLastName(String lastName) {
//        this.lastName = lastName;
//    }

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

}
