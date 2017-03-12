package de.riditt.easyboxunofficial.Presenters;

import de.riditt.easyboxunofficial.MainActivity;
import de.riditt.easyboxunofficial.Views.IMainActivityView;

/**
 * Created by Rico on 3/12/2017.
 */

public class MainActivityPresenter {
    private IMainActivityView mainActivityView;

    public MainActivityPresenter(IMainActivityView mainActivityView) {
        this.mainActivityView = mainActivityView;
    }
}
