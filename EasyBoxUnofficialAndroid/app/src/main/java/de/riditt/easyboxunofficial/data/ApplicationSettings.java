package de.riditt.easyboxunofficial.data;

public interface ApplicationSettings {
    String getServerUrl();
    String getPassword();
    void setServerUrl(String serverUrl);
    void setPassword(String password);
}
