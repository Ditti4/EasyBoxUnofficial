package de.riditt.easyboxunofficial.presenters;

import de.riditt.easyboxunofficial.api.EasyBoxApiListener;
import de.riditt.easyboxunofficial.data.ApplicationSettings;
import de.riditt.easyboxunofficial.models.responses.LoginResponse;
import de.riditt.easyboxunofficial.services.EasyBoxService;
import de.riditt.easyboxunofficial.views.MainView;

public class MainPresenter {
    private MainView mainActivityView;
    private EasyBoxService easyBoxService;
    private ApplicationSettings applicationSettings;

    public MainPresenter(MainView mainActivityView, EasyBoxService easyBoxService, ApplicationSettings applicationSettings) {
        this.mainActivityView = mainActivityView;
        this.easyBoxService = easyBoxService;
        this.applicationSettings = applicationSettings;
    }

    public void performLogin() {
        /*
        1. Check if we already know the user's password. Get it from the application settings as
           well as get the server URL. If the password is unknown, proceed with step 5.
        2. Try to establish a connection to the API. If it worked, proceed with step 4.
        3. Open the LoginActivity and fill in the password from the application settings. Let the
           LoginPresenter handle the rest. Wait for a report from them.
        4. Try logging in using the known password. If it worked, we are done and can report back to
           the view.
        5. Open the LoginActivity. Let the LoginPresenter handle the rest of it. Wait for a report
           from them.
         */
        final String password = applicationSettings.getPassword();
        if(password.isEmpty()) {
            // the user didn't log into the app yet
            mainActivityView.showLoginActivity();
            return;
        }
        String serverUrl = applicationSettings.getServerUrl();
        // let's try establishing a connection to the API
        easyBoxService.establishConnection(serverUrl, new EasyBoxApiListener.OnApiResultListener() {
            @Override
            public void onApiResult(boolean success, Object... results) {
                if(success) {
                    // Whatever server URL we got worked, that's good. Let's try logging in using
                    // the password we got a few lines prior
                    easyBoxService.login(password, new EasyBoxApiListener.OnLoginResponseListener() {
                        @Override
                        public void onLoginResponse(LoginResponse loginResponse) {
                            if(loginResponse.getSoapBody().getSoapEnvFault() != null) {
                                // something went wrong when we tried to login (the password is
                                // probably not correct) so let's let the LoginActivity do the dirty
                                // work
                                mainActivityView.showLoginActivity();
                            }
                        }
                    });
                } else {
                    // we don't know what to do, but the LoginActivity surely does!
                    mainActivityView.showLoginActivity();
                }
            }
        });
    }
}
