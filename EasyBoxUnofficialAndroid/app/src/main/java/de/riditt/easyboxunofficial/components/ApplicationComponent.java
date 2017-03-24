package de.riditt.easyboxunofficial.components;

import javax.inject.Singleton;

import dagger.Component;
import de.riditt.easyboxunofficial.activities.LoginActivity;
import de.riditt.easyboxunofficial.activities.MainActivity;
import de.riditt.easyboxunofficial.modules.ApplicationModule;

@Singleton
@Component(modules={ApplicationModule.class})
public interface ApplicationComponent {
    void inject(MainActivity activity);
    void inject(LoginActivity activity);
}
