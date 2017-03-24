package de.riditt.easyboxunofficial.modules;

import android.app.Application;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import de.riditt.easyboxunofficial.data.ApplicationSettings;
import de.riditt.easyboxunofficial.data.SharedPreferencesApplicationSettings;

@Module
public class ApplicationModule {
    private Application mApplication;

    public ApplicationModule(Application application) {
        this.mApplication = application;
    }

    @Provides
    @Singleton
    Application provideApplication() {
        return mApplication;
    }

    @Provides
    @Singleton
    ApplicationSettings provideApplicationSettings(Application application) {
        return new SharedPreferencesApplicationSettings(application);
    }
}
