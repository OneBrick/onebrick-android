package org.onebrick.android.activities;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.onebrick.android.R;
import org.onebrick.android.core.OneBrickApplication;
import org.onebrick.android.core.OneBrickCrypt;
import org.onebrick.android.core.OneBrickRESTClient;
import org.onebrick.android.events.LoginStatusEvent;
import org.onebrick.android.events.Status;
import org.onebrick.android.helpers.LoginManager;

import butterknife.Bind;
import butterknife.ButterKnife;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = LoginActivity.class.getSimpleName();

    // UI references.
    @Bind(R.id.email)
    EditText mEmailView;
    @Bind(R.id.password)
    EditText mPasswordView;
    @Bind(R.id.email_sign_in_button)
    Button mEmailSignInButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

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
            Log.e(TAG, "can't get an encrypted key", e);
        }
        final String finalEncrypted = OneBrickCrypt.bytesToHex(encrypt);
        OneBrickRESTClient.getInstance().verifyLogin(finalEncrypted, new Callback<String[]>() {
            @Override
            public void success(String[] strings, Response response) {
                if (strings != null && strings.length > 0) {
                    try{
                        // as long as return value is number, it's valid.
                        long userId = Long.parseLong(strings[0]);
                        // successful
                        LoginManager manager = LoginManager.getInstance(LoginActivity.this);
                        manager.setCurrentUserKey(finalEncrypted);
                        OneBrickApplication.getInstance().getBus().post(
                                new LoginStatusEvent(Status.SUCCESS));
                        updateMyEvents();
                        finish();
                    } catch (NumberFormatException e) {
                        Log.e(TAG, "login failed due to invalid key.");
                        OneBrickApplication.getInstance().getBus().post(
                                new LoginStatusEvent(Status.FAILED));
                        Toast.makeText(getApplicationContext(),
                                R.string.error_invalid_credentials,
                                Toast.LENGTH_SHORT).show();
                    }
                } else {
                    OneBrickApplication.getInstance().getBus().post(
                            new LoginStatusEvent(Status.FAILED));
                    Log.d(TAG, "invalid json response");
                }
            }

            @Override
            public void failure(RetrofitError error) {
                OneBrickApplication.getInstance().getBus().post(
                        new LoginStatusEvent(Status.FAILED));
                Log.e(TAG, "login failure: " + error.toString());
            }
        });
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
