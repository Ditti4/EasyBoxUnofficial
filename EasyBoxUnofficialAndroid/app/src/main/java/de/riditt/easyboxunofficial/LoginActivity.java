package de.riditt.easyboxunofficial;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.app.Activity;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.ContentResolver;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.riditt.easyboxunofficial.Api.EasyBoxApi;

import static android.Manifest.permission.READ_CONTACTS;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends Activity {
    // EasyBoxApi instance, provided by Dagger
    @Inject
    EasyBoxApi mEasyBoxApi;

    // UI references
    @BindView(R.id.server_url)
    EditText mServerUrlView;
    @BindView(R.id.password)
    EditText mPasswordView;
    @BindView(R.id.login_progress)
    View mProgressView;
    @BindView(R.id.login_form)
    View mLoginFormView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        setupActionBar();

        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        if(!mEasyBoxApi.isInitialized()) {
            mEasyBoxApi.Initialize("", new EasyBoxApi.OnApiResultListener() {
                @Override
                public void onApiResult(boolean success, Object... results) {
                    if(success) {
                        // one of the default values worked, we don't need to bother the user
                        mServerUrlView.setVisibility(View.GONE);
                        if(!mEasyBoxApi.isConnectionEstablished()) {
                            mEasyBoxApi.EstablishConnection(new EasyBoxApi.OnApiResultListener() {
                                @Override
                                public void onApiResult(boolean success, Object... results) {
                                    // nothing should go wrong from here on
                                }
                            });
                        }
                    }
                }
            });
        }
    }

    /**
     * Set up the {@link android.app.ActionBar}, if the API is available.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private void setupActionBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            // Show the Up button in the action bar.
            getActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    @OnClick(R.id.sign_in_button)
    public void attemptLogin() {
        // Reset errors.
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        final String serverUrl = mServerUrlView.getText().toString();
        final String password = mPasswordView.getText().toString();

        // TODO: move all of this to the presenter which still has to be created

        showProgress(true);
        mEasyBoxApi.CheckForValidEasyBoxUrl(password, new EasyBoxApi.OnApiResultListener() {
            @Override
            public void onApiResult(boolean success, Object... results) {
                if(!success) {
                    showProgress(false);
                    mServerUrlView.setError(getString(R.string.error_invalid_server_url));
                    mServerUrlView.requestFocus();
                } else {
                    mEasyBoxApi.Initialize(serverUrl, new EasyBoxApi.OnApiResultListener() {
                        @Override
                        public void onApiResult(boolean success, Object... results) {
                            mEasyBoxApi.EstablishConnection(new EasyBoxApi.OnApiResultListener() {
                                @Override
                                public void onApiResult(boolean success, Object... results) {
                                    mEasyBoxApi.Login(password, new EasyBoxApi.OnApiResultListener() {
                                        @Override
                                        public void onApiResult(boolean success, Object... results) {
                                            showProgress(false);
                                            if(success) {
                                                finish();
                                            } else {
                                                mPasswordView.setError(getString(R.string.error_incorrect_password));
                                                mPasswordView.requestFocus();
                                            }
                                        }
                                    });
                                }
                            });
                        }
                    });
                }
            }
        });
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }
}

