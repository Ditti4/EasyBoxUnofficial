package de.riditt.easyboxunofficial.presenters;

import de.riditt.easyboxunofficial.api.EasyBoxApi;
import de.riditt.easyboxunofficial.activities.LoginActivity;
import de.riditt.easyboxunofficial.views.IMainActivityView;

public class MainActivityPresenter {
    private IMainActivityView mainActivityView;
    private EasyBoxApi easyBoxApi;

    public MainActivityPresenter(IMainActivityView mainActivityView, EasyBoxApi easyBoxApi) {
        this.mainActivityView = mainActivityView;
        this.easyBoxApi = easyBoxApi;
    }

    public void performLogin() {
        mainActivityView.launchActivity(LoginActivity.class);
    }
}
