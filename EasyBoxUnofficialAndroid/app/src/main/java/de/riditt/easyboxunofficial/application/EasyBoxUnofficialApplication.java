package de.riditt.easyboxunofficial.application;

import android.app.Application;

import de.riditt.easyboxunofficial.components.DaggerNetworkComponent;
import de.riditt.easyboxunofficial.components.NetworkComponent;

public class EasyBoxUnofficialApplication extends Application {

    private NetworkComponent mNetworkComponent;

    @Override
    public void onCreate() {
        super.onCreate();

        // Dagger network component
        mNetworkComponent = DaggerNetworkComponent.create();
    }

    public NetworkComponent getNetworkComponent() {
        return mNetworkComponent;
    }
}
