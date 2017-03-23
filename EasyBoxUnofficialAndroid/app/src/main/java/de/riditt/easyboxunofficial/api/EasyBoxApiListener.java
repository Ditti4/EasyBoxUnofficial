package de.riditt.easyboxunofficial.api;

import de.riditt.easyboxunofficial.models.responses.LoginResponse;

public class EasyBoxApiListener {
    public interface OnLoginResponseListener {
        void onLoginResponse(LoginResponse loginResponse);
    }

    public interface OnSessionKeepAliveResponseListener {
        void onSessionKeepAliveResponse();
    }

    public interface OnApiResultListener {
        void onApiResult(boolean success, Object... results);
    }
}
