package de.riditt.easyboxunofficial.views;

public interface ILoginActivityView {
    void showProgress(boolean show);
    void resetErrors();
    void hideServerUrlField();
    void showServerUrlError();
    void showPasswordError();
    void showLoginBlockedError(int seconds);
    void handleSuccessfulLogin();
}
