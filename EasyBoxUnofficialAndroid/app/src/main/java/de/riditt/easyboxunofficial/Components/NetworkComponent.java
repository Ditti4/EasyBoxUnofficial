package de.riditt.easyboxunofficial.Components;

import javax.inject.Singleton;

import dagger.Component;
import de.riditt.easyboxunofficial.LoginActivity;
import de.riditt.easyboxunofficial.MainActivity;
import de.riditt.easyboxunofficial.Modules.ApplicationModule;
import de.riditt.easyboxunofficial.Modules.NetworkModule;

@Singleton
@Component(modules={NetworkModule.class})
public interface NetworkComponent {
    void inject(MainActivity activity);
    void inject(LoginActivity activity);
}
