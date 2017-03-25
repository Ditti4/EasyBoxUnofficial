package de.riditt.easyboxunofficial.application;

import android.app.Application;

import de.riditt.easyboxunofficial.components.DaggerNetworkComponent;
import de.riditt.easyboxunofficial.components.NetworkComponent;
import de.riditt.easyboxunofficial.modules.ApplicationModule;
import de.riditt.easyboxunofficial.modules.NetworkModule;

public class EasyBoxUnofficialApplication extends Application {
    private NetworkComponent networkComponent;

    @Override
    public void onCreate() {
        super.onCreate();

        // Dagger network component
        networkComponent = DaggerNetworkComponent.builder()
                .applicationModule(new ApplicationModule(this))
                .networkModule(new NetworkModule())
                .build();
    }

    public NetworkComponent getNetworkComponent() {
        return networkComponent;
    }
}
