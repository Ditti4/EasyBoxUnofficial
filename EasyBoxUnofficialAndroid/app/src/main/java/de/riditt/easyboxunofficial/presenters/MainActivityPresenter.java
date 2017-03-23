package de.riditt.easyboxunofficial.presenters;

import de.riditt.easyboxunofficial.api.EasyBoxApi;
import de.riditt.easyboxunofficial.activities.LoginActivity;
import de.riditt.easyboxunofficial.views.MainView;

public class MainActivityPresenter {
    private MainView mainActivityView;
    private EasyBoxApi easyBoxApi;

    public MainActivityPresenter(MainView mainActivityView, EasyBoxApi easyBoxApi) {
        this.mainActivityView = mainActivityView;
        this.easyBoxApi = easyBoxApi;
    }

    public void performLogin() {
        mainActivityView.launchActivity(LoginActivity.class);
    }
}
