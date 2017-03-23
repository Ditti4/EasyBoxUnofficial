package de.riditt.easyboxunofficial.services;

import de.riditt.easyboxunofficial.api.EasyBoxApi;
import de.riditt.easyboxunofficial.api.EasyBoxApiListener;
import de.riditt.easyboxunofficial.models.responses.LoginResponse;

public class EasyBoxService {
    private EasyBoxApi easyBoxApi;

    public EasyBoxService(EasyBoxApi easyBoxApi) {
        this.easyBoxApi = easyBoxApi;
    }

    public void checkForValidEasyBoxUrl(String serverUrl, EasyBoxApiListener.OnApiResultListener listener) {
        easyBoxApi.checkForValidEasyBoxUrl(serverUrl, listener);
    }

    public void establishConnection(String serverUrl, final EasyBoxApiListener.OnApiResultListener listener) {
        if (!easyBoxApi.isInitialized()) {
            easyBoxApi.initialize(serverUrl, new EasyBoxApiListener.OnApiResultListener() {
                @Override
                public void onApiResult(boolean success, Object... results) {
                    if (success) {
                        if (!easyBoxApi.isConnectionEstablished()) {
                            easyBoxApi.establishConnection(listener);
                        }
                    } else {
                        listener.onApiResult(false);
                    }
                }
            });
        }
    }

    public void establishConnection(final EasyBoxApiListener.OnApiResultListener listener) {
        establishConnection("", listener);
    }

    public void login(String password, final EasyBoxApiListener.OnLoginResponseListener listener) {
        easyBoxApi.login(password, new EasyBoxApiListener.OnLoginResponseListener() {
            @Override
            public void onLoginResponse(final LoginResponse loginResponse) {
                if (loginResponse.getSoapBody().getSoapEnvFault() == null) {
                    // seems like the login was successful
                    // thus, the EasyBoxApi needs a new dm_cookie
                    easyBoxApi.fetchDmCookie("app.html", new EasyBoxApiListener.OnApiResultListener() {
                        @Override
                        public void onApiResult(boolean success, Object... results) {
                            // now that we hopefully have the dm_cookie, we want to signal to the
                            // listener that the login has been successful
                            listener.onLoginResponse(loginResponse);
                        }
                    });
                } else {
                    listener.onLoginResponse(loginResponse);
                }
            }
        });
    }

    public boolean isInitialized() {
        return easyBoxApi.isInitialized();
    }

    public boolean isConnectionEstablished() {
        return easyBoxApi.isConnectionEstablished();
    }
}
