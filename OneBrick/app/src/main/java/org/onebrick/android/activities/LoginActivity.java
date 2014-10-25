package org.onebrick.android.activities;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.onebrick.android.OneBrickApplication;
import org.onebrick.android.OneBrickClient;
import org.onebrick.android.R;
import org.onebrick.android.helpers.LoginManager;
import org.onebrick.android.models.Event;
import org.onebrick.android.models.User;

import java.util.ArrayList;

//public class LoginActivity extends OAuthLoginActivity<OneBrickClient> {
public class LoginActivity extends Activity{

    // UI references.
    private EditText mEmailView;
    private EditText mPasswordView;
    private long userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Set up the login form.
        mEmailView = (EditText) findViewById(R.id.email);
        mPasswordView = (EditText) findViewById(R.id.password);
        Button mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
        mEmailSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = mEmailView.getText().toString();
                String password = mPasswordView.getText().toString();
                if(email.length() == 0
                        || email.equalsIgnoreCase("")
                        || !email.contains("@")
                        || password.length()== 0
                        || password.equalsIgnoreCase("")) {
                    Toast.makeText(getApplicationContext(),
                            "Email or Password cannot be null or empty string",
                            Toast.LENGTH_SHORT).show();

                } else {
                    attemptLogin();
                }
            }
        });


    }

    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    public void attemptLogin() {
        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;


        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        } else if (!isEmailValid(email)) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            getAuthentication(email, password);
        }
    }
    private void getAuthentication(String username, String password) {
        OneBrickClient client = OneBrickApplication.getRestClient();
        client.getUserLogin(username, password, new JsonHttpResponseHandler() {

            @Override
            public void onStart() {
                super.onStart();
                //mProgressView.setVisibility(ProgressBar.VISIBLE);
            }

            @Override
            public void onFinish() {
                super.onFinish();
                //mProgressView.setVisibility(ProgressBar.GONE);
                finish();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                //super.onSuccess(statusCode, headers, response);
                //Log.i("login success1", response.toString());
                try {
                    Log.i("id", response.getJSONObject("user").optString("uid"));
                    User user = User.fromJSON(response);
                    userId = user.getUId();
                    LoginManager manager = LoginManager.getInstance(LoginActivity.this);
                    manager.requestLogin(user);
                    //Toast.makeText(getApplicationContext(), "login status: " + manager.isLoggedIn(), Toast.LENGTH_SHORT).show();
                    /*
                    Calling method to update rsvp info on methods
                     */
                    updateMyEvents();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Log.e("login failure1", responseString);
                Log.e("login failure1", throwable.toString());
                //Toast.makeText(getApplicationContext(), "Couldn't login", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.e("login failure2", errorResponse.toString());
                Log.e("login failure2", throwable.toString());
                //Toast.makeText(getApplicationContext(), errorResponse.toString(), Toast.LENGTH_SHORT).show();
            }

        });

    }


    /*
    After the user has logged in, this method is called to update the event table
    on the users rsvp events
     */
    private void updateMyEvents() {
        OneBrickClient client = OneBrickApplication.getRestClient();
        client.getMyEvents(userId,true,new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                int chapterId = OneBrickApplication
                        .getApplicationSharedPreference()
                        .getInt("MyChapterId", -1);
                if (response != null) {
                    ArrayList<Event> arrayOfEvents = Event.fromJSONArray(response, chapterId);
                    for (int i=0;i<arrayOfEvents.size();i++) {
                        Event e = arrayOfEvents.get(i);
                        e.rsvp = true;
                        Event.updateEvent(e);
                    }
                }

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Log.e("login failure1", responseString);
                Log.e("login failure1", throwable.toString());
                //Toast.makeText(getApplicationContext(), "Couldn't login", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.e("login failure2", errorResponse.toString());
                Log.e("login failure2", throwable.toString());
                //Toast.makeText(getApplicationContext(), errorResponse.toString(), Toast.LENGTH_SHORT).show();
            }

        });
    }

    private boolean isPasswordValid(String password) {
        return password.length() > 4;
    }

    private boolean isEmailValid(String email) {
        return email.contains("@");
    }

}
