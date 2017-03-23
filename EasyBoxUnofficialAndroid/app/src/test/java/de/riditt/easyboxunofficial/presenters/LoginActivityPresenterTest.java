package de.riditt.easyboxunofficial.presenters;

import org.junit.Test;

import de.riditt.easyboxunofficial.views.ILoginActivityView;

public class LoginActivityPresenterTest {
    private class MockView implements ILoginActivityView {
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
        ILoginActivityView loginActivityView = new MockView();

        LoginActivityPresenter presenter = new LoginActivityPresenter(loginActivityView, null);
    }
}