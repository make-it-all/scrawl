package com.chairsquad.www.scrawl.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.chairsquad.www.scrawl.R;
import com.chairsquad.www.scrawl.utilities.ScrawlConnection;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity {


    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    private UserLoginTask mAuthTask = null;
    private ScrawlConnection mScrawlConnection;

    // UI references.
    private View mProgressBar;
    private View mLoginForm;
    private EditText mEmailEditText;
    private EditText mPasswordEditText;
    private Button mSubmitButton;

    private TextView mSignup;
    private TextView mForgotPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        if (ScrawlConnection.isLoggedIn(this)) onSuccessfulLogin();

        // Get UI references
        mProgressBar = findViewById(R.id.login_progress);
        mLoginForm = findViewById(R.id.login_form);
        mEmailEditText = (EditText) findViewById(R.id.login_form_email_et);
        mPasswordEditText = (EditText) findViewById(R.id.login_form_password_et);
        mSubmitButton = (Button) findViewById(R.id.login_form_submit_btn);

        mSignup = (TextView) findViewById(R.id.tv_sign_up);
        mForgotPassword = (TextView) findViewById(R.id.tv_forgot_password);

        mSignup.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                String url = ScrawlConnection.BASE_URL + "sign_up";
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
            }
        });
        mForgotPassword.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                String url = ScrawlConnection.BASE_URL + "passwords/new";
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
            }
        });

        // Set up the login form.
        mSubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

    }


    private void onSuccessfulLogin() {
        finish();
    }

    private void onFailedLogin() {
        mPasswordEditText.setError(getString(R.string.error_incorrect_password));
        mPasswordEditText.requestFocus();
    }

    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin() {
        if (mAuthTask != null) {
            return;
        }

        // Reset errors.
        mEmailEditText.setError(null);
        mPasswordEditText.setError(null);

        // Store values at the time of the login attempt.
        String email = mEmailEditText.getText().toString();
        String password = mPasswordEditText.getText().toString();

        View errorView = null;

        // Check for a valid password, if the user entered one.
        if (TextUtils.isEmpty(password)) {
            errorView = mPasswordEditText;
        }
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPasswordEditText.setError(getString(R.string.error_invalid_password));
            errorView = mPasswordEditText;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mEmailEditText.setError(getString(R.string.error_field_required));
            errorView = mEmailEditText;
        } else if (!isEmailValid(email)) {
            mEmailEditText.setError(getString(R.string.error_invalid_email));
            errorView = mEmailEditText;
        }

        if (errorView != null) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            errorView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true);
            mAuthTask = new UserLoginTask(email, password);
            mAuthTask.execute();
        }
    }

    private boolean isEmailValid(String email) {
        return email.contains("@");
    }

    private boolean isPasswordValid(String password) {
        return password.length() > 4;
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    private void showProgress(boolean show) {
        mLoginForm.setVisibility(show ? View.GONE : View.VISIBLE);
        mProgressBar.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {

        private final String mEmail;
        private final String mPassword;

        UserLoginTask(String email, String password) {
            mEmail = email;
            mPassword = password;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            return mScrawlConnection.login(LoginActivity.this, mEmail, mPassword);
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mAuthTask = null;
            showProgress(false);

            if (success) {
                onSuccessfulLogin();
            } else {
                onFailedLogin();
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
        }
    }
}

