package de.riditt.easyboxunofficial.presenters;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.riditt.easyboxunofficial.api.EasyBoxApiListener;
import de.riditt.easyboxunofficial.models.responses.common.CwmpFault;
import de.riditt.easyboxunofficial.models.responses.LoginResponse;
import de.riditt.easyboxunofficial.services.EasyBoxService;
import de.riditt.easyboxunofficial.views.LoginView;

public class LoginActivityPresenter {
    private LoginView loginActivityView;
    private EasyBoxService easyBoxService;

    public LoginActivityPresenter(LoginView loginActivityView, EasyBoxService easyBoxService) {
        this.loginActivityView = loginActivityView;
        this.easyBoxService = easyBoxService;
    }

    public void onCreateView() {
        if (!easyBoxService.isInitialized()) {
            easyBoxService.establishConnection(new EasyBoxApiListener.OnApiResultListener() {
                @Override
                public void onApiResult(boolean success, Object... results) {
                    if (success) {
                        // one of the default values worked, we don't need to bother the user
                        loginActivityView.hideServerUrlField();
                        easyBoxService.establishConnection(new EasyBoxApiListener.OnApiResultListener() {
                            @Override
                            public void onApiResult(boolean success, Object... results) {
                                // nothing should go wrong from here on
                            }
                        });
                    }
                }
            });
        }
    }

    public void attemptLogin(final String serverUrl, final String password) {
        loginActivityView.resetErrors();

        loginActivityView.showProgress(true);
        if (easyBoxService.isConnectionEstablished()) {
            easyBoxService.login(password, new EasyBoxApiListener.OnLoginResponseListener() {
                @Override
                public void onLoginResponse(LoginResponse loginResponse) {
                    loginActivityView.showProgress(false);
                    if(loginResponse.getSoapBody() != null) {
                        if (loginResponse.getSoapBody().getLoginResponse() != null) {
                            loginActivityView.handleSuccessfulLogin();
                        } else {
                            CwmpFault fault = loginResponse.getSoapBody().getSoapEnvFault().getSoapEnvFaultDetail().getCwmpFault();
                            Pattern loginBlockedPattern = Pattern.compile("Tauth_blocked_for_security;(\\d+)");
                            if (fault != null) {
                                Matcher loginBlockedMatcher = loginBlockedPattern.matcher(fault.getFaultMsgCode());
                                if (loginBlockedMatcher.find()) {
                                    loginActivityView.showLoginBlockedError(Integer.parseInt(loginBlockedMatcher.group(1)));
                                } else {
                                    loginActivityView.showPasswordError();
                                }
                            }
                        }
                    } else {
                        // TODO: handle an HTTP error (like 403 in case of the session dying before the user logs in)
                        // showing an error like "Session ended, please press login again" could work
                    }
                }
            });
        } else {
            easyBoxService.checkForValidEasyBoxUrl(serverUrl, new EasyBoxApiListener.OnApiResultListener() {
                @Override
                public void onApiResult(boolean success, Object... results) {
                    if (!success) {
                        loginActivityView.showProgress(false);
                        loginActivityView.showServerUrlError();
                    } else {
                        easyBoxService.establishConnection(new EasyBoxApiListener.OnApiResultListener() {
                            @Override
                            public void onApiResult(boolean success, Object... results) {
                                if (success) {
                                    attemptLogin(serverUrl, password);
                                }
                            }
                        });
                    }
                }
            });
        }
    }
}
