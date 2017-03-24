package de.riditt.easyboxunofficial.data;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class SharedPreferencesApplicationSettings implements ApplicationSettings {
    private final SharedPreferences sharedPreferences;

    public SharedPreferencesApplicationSettings(Context context) {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    @Override
    public String getServerUrl() {
        return sharedPreferences.getString("server_url", "");
    }

    @Override
    public String getPassword() {
        return sharedPreferences.getString("password", "");
    }

    @Override
    public void setServerUrl(String serverUrl) {
        sharedPreferences.edit().putString("server_url", serverUrl).apply();
    }

    @Override
    public void setPassword(String password) {
        sharedPreferences.edit().putString("password", password).apply();
    }
}
