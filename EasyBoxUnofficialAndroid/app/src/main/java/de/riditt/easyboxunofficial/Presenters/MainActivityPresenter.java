package de.riditt.easyboxunofficial.Presenters;

import android.content.Intent;

import de.riditt.easyboxunofficial.Api.EasyBoxApi;
import de.riditt.easyboxunofficial.LoginActivity;
import de.riditt.easyboxunofficial.MainActivity;
import de.riditt.easyboxunofficial.Views.IMainActivityView;

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
