package de.riditt.easyboxunofficial.presenters;

import org.junit.Test;

import de.riditt.easyboxunofficial.views.LoginView;

public class LoginPresenterTest {
    private class MockView implements LoginView {
        boolean showProgressCalled;
        boolean showServerUrlErrorCalled;

        @Override
        public void showProgress(boolean show) {
            showProgressCalled = true;
        }

        @Override
        public void resetErrors() {

        }

        @Override
        public void hideServerUrlField() {

        }

        @Override
        public void showServerUrlError() {

        }

        @Override
        public void showPasswordError() {

        }

        @Override
        public void showLoginBlockedError(int seconds) {

        }

        @Override
        public void handleSuccessfulLogin() {

        }
    }

    @Test
    public void shouldEstablishConnection() {
        LoginView loginActivityView = new MockView();

        LoginPresenter presenter = new LoginPresenter(loginActivityView, null);
    }
}