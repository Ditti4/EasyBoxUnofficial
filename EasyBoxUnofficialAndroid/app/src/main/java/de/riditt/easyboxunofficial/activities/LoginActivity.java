package de.riditt.easyboxunofficial.activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.riditt.easyboxunofficial.R;
import de.riditt.easyboxunofficial.application.EasyBoxUnofficialApplication;
import de.riditt.easyboxunofficial.presenters.LoginPresenter;
import de.riditt.easyboxunofficial.services.EasyBoxService;
import de.riditt.easyboxunofficial.views.LoginView;

/**
 * A login screen that offers login to the EasyBox using the device's password.
 */
public class LoginActivity extends AppCompatActivity implements LoginView {
    // EasyBoxService instance, provided by Dagger
    @Inject
    EasyBoxService easyBoxService;

    // UI references
    @BindView(R.id.server_url)
    EditText serverUrlView;
    @BindView(R.id.password)
    EditText passwordView;
    @BindView(R.id.login_progress)
    View progressView;
    @BindView(R.id.login_form)
    View loginFormView;
    private LoginPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        ((EasyBoxUnofficialApplication) getApplication()).getNetworkComponent().inject(this);
        presenter = new LoginPresenter(this, easyBoxService);

        setupView();

        presenter.onCreateView();
    }

    private void setupView() {
        if(getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

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
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    public void showProgress(final boolean show) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
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
        });
    }

    @OnClick(R.id.sign_in_button)
    public void attemptLogin() {
        final String serverUrl = serverUrlView.getText().toString();
        final String password = passwordView.getText().toString();

        presenter.attemptLogin(serverUrl, password);
    }

    @Override
    public void resetErrors() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                serverUrlView.setError(null);
                passwordView.setError(null);
            }
        });
    }

    @Override
    public void hideServerUrlField() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                serverUrlView.setVisibility(View.GONE);
            }
        });
    }

    @Override
    public void showServerUrlError() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                serverUrlView.setError(getString(R.string.error_invalid_server_url));
                serverUrlView.requestFocus();
            }
        });
    }

    @Override
    public void showPasswordError() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                passwordView.setError(getString(R.string.error_incorrect_password));
                passwordView.requestFocus();
            }
        });
    }

    @Override
    public void showLoginBlockedError(final int seconds) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                passwordView.setError(getResources().getQuantityString(R.plurals.error_login_blocked, seconds, seconds));
                passwordView.requestFocus();
            }
        });
    }

    @Override
    public void handleSuccessfulLogin() {
        finish();
    }
}

