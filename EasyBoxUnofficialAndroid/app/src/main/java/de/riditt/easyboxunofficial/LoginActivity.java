package de.riditt.easyboxunofficial;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.riditt.easyboxunofficial.Api.EasyBoxApi;
import de.riditt.easyboxunofficial.Application.EasyBoxUnofficialApplication;
import de.riditt.easyboxunofficial.Models.Requests.SessionKeepAliveRequest;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity {
    // EasyBoxApi instance, provided by Dagger
    @Inject
    EasyBoxApi easyBoxApi;

    // UI references
    @BindView(R.id.server_url)
    EditText serverUrlView;
    @BindView(R.id.password)
    EditText passwordView;
    @BindView(R.id.login_progress)
    View progressView;
    @BindView(R.id.login_form)
    View loginFormView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        ((EasyBoxUnofficialApplication) getApplication()).getNetworkComponent().inject(this);

        setupActionBar();

        passwordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        if(!easyBoxApi.isInitialized()) {
            easyBoxApi.initialize("", new EasyBoxApi.OnApiResultListener() {
                @Override
                public void onApiResult(boolean success, Object... results) {
                    if(success) {
                        // one of the default values worked, we don't need to bother the user
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                serverUrlView.setVisibility(View.GONE);
                            }
                        });
                        if(!easyBoxApi.isConnectionEstablished()) {
                            easyBoxApi.establishConnection(new EasyBoxApi.OnApiResultListener() {
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
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
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
        serverUrlView.setError(null);
        passwordView.setError(null);

        // Store values at the time of the login attempt.
        final String serverUrl = serverUrlView.getText().toString();
        final String password = passwordView.getText().toString();

        // TODO: move all of this to the presenter which still has to be created
        showProgress(true);
        if(easyBoxApi.isConnectionEstablished()) {
            easyBoxApi.login(password, new EasyBoxApi.OnApiResultListener() {
                @Override
                public void onApiResult(final boolean success, Object... results) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            showProgress(false);
                            if(success) {
                                finish();
                            } else {
                                passwordView.setError(getString(R.string.error_incorrect_password));
                                passwordView.requestFocus();
                            }
                        }
                    });
                }
            });
        } else {
            easyBoxApi.checkForValidEasyBoxUrl(serverUrl, new EasyBoxApi.OnApiResultListener() {
                @Override
                public void onApiResult(boolean success, Object... results) {
                    if (!success) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                showProgress(false);
                                serverUrlView.setError(getString(R.string.error_invalid_server_url));
                                serverUrlView.requestFocus();
                            }
                        });
                    } else {
                        easyBoxApi.initialize(serverUrl, new EasyBoxApi.OnApiResultListener() {
                            @Override
                            public void onApiResult(boolean success, Object... results) {
                                easyBoxApi.establishConnection(new EasyBoxApi.OnApiResultListener() {
                                    @Override
                                    public void onApiResult(boolean success, Object... results) {
                                        easyBoxApi.login(password, new EasyBoxApi.OnApiResultListener() {
                                            @Override
                                            public void onApiResult(final boolean success, Object... results) {
                                                runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        showProgress(false);
                                                        if (success) {
                                                            finish();
                                                        } else {
                                                            passwordView.setError(getString(R.string.error_incorrect_password));
                                                            passwordView.requestFocus();
                                                        }
                                                    }
                                                });
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

            loginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            loginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    loginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            progressView.setVisibility(show ? View.VISIBLE : View.GONE);
            progressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    progressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            progressView.setVisibility(show ? View.VISIBLE : View.GONE);
            loginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }
}

