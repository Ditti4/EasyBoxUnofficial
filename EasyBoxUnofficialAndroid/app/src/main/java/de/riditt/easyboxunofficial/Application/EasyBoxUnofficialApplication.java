package de.riditt.easyboxunofficial.Application;

import android.app.Application;

import de.riditt.easyboxunofficial.Components.DaggerNetworkComponent;
import de.riditt.easyboxunofficial.Components.NetworkComponent;
import de.riditt.easyboxunofficial.Modules.ApplicationModule;
import de.riditt.easyboxunofficial.Modules.NetworkModule;

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
