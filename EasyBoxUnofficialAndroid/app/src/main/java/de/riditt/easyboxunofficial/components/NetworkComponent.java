package de.riditt.easyboxunofficial.components;

import javax.inject.Singleton;

import dagger.Component;
import de.riditt.easyboxunofficial.views.LoginActivity;
import de.riditt.easyboxunofficial.views.MainActivity;
import de.riditt.easyboxunofficial.modules.NetworkModule;

@Singleton
@Component(modules={NetworkModule.class})
public interface NetworkComponent {
    void inject(MainActivity activity);
    void inject(LoginActivity activity);
}
