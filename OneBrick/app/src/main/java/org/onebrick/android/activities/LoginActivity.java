package org.onebrick.android.activities;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBarActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.onebrick.android.R;
import org.onebrick.android.core.OneBrickCrypt;
import org.onebrick.android.core.OneBrickRESTClient;
import org.onebrick.android.helpers.LoginManager;

import butterknife.ButterKnife;
import butterknife.InjectView;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class LoginActivity extends ActionBarActivity {

    private static final String TAG = "LoginActivity";
    private static final String SUCCESS = "1";

    // UI references.
    @InjectView(R.id.email)
    EditText mEmailView;
    @InjectView(R.id.password)
    EditText mPasswordView;
    @InjectView(R.id.email_sign_in_button)
    Button mEmailSignInButton;
    private long userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.inject(this);

        mEmailSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = mEmailView.getText().toString();
                String password = mPasswordView.getText().toString();
                if (email.length() == 0
                        || email.equalsIgnoreCase("")
                        || !email.contains("@")
                        || password.length() == 0
                        || password.equalsIgnoreCase("")) {
                    Toast.makeText(getApplicationContext(),
                            R.string.error_login_general,
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
            getAuthentication(email.trim(), password);
        }
    }

    private void getAuthentication(@NonNull String username, @NonNull String password) {
        byte[] encrypt = null;
        try {
            Log.d(TAG, "getAuthenticated: user provided username and password: " + username + " -- " + password);
            encrypt = OneBrickCrypt.encrypt(username, password);
        } catch (Exception e) {
            Log.e(TAG, "can't get an encrypted key.");
            e.printStackTrace();
        }
        final String finalEncrypted = OneBrickCrypt.bytesToHex(encrypt);
        OneBrickRESTClient.getInstance().verifyLogin(finalEncrypted, new Callback<String[]>() {
            @Override
            public void success(String[] strings, Response response) {
                if (strings != null && strings.length > 0) {
                    if (SUCCESS.equals(strings[0])) {
                        // successful
                        saveKey(finalEncrypted);
                        LoginManager manager = LoginManager.getInstance(LoginActivity.this);
                        manager.setCurrentUserKey(finalEncrypted);
                        updateMyEvents();
                        finish();

                    } else {
                        // invalid credential
                        Log.d(TAG, "invalid credential: " + strings[0]);
                    }
                } else {
                    // invalid json response
                    Log.d(TAG, "invalid json response");
                }
            }

            @Override
            public void failure(RetrofitError error) {
                // rest API call failed
                Log.e(TAG, "login failure: " + error.toString());
            }
        });
    }

    private void saveKey(@NonNull String key) {
        Context context = this.getApplicationContext();
        SharedPreferences sharedPref = context.getSharedPreferences(
                getString(R.string.preference_file_ukey), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(getString(R.string.user_key), key);
        editor.apply();
    }

    /*
    After the user has logged in, this method is called to update the event table
    on the users rsvp events
     */
    private void updateMyEvents() {
        Log.d(TAG, "update my events.");
    }

    private boolean isPasswordValid(String password) {
        return password.length() > 4;
    }

    private boolean isEmailValid(String email) {
        return email.contains("@");
    }

}
