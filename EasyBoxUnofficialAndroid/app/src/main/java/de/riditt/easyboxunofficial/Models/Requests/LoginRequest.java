package de.riditt.easyboxunofficial.Models.Requests;

import de.riditt.easyboxunofficial.Utilities;
import okhttp3.Request;

public class LoginRequest extends DmSoapRequest {
    private String username;
    private String password;
    private boolean allowRelogin;

    @SuppressWarnings("SameParameterValue")
    public LoginRequest(String serverUrl, String dmCookie, String username, String password, boolean allowRelogin) {
        super(serverUrl, "cwmp:Login", dmCookie);
        this.username = username;
        this.password = password;
        this.allowRelogin = allowRelogin;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public boolean isAllowRelogin() {
        return allowRelogin;
    }

    @Override
    protected String getBodyContent() {
        return "            <ParameterList>\n" +
                "                <Username>" + username + "</Username>\n" +
                "                <Password>" + password + "</Password>\n" +
                "                <AllowRelogin>" + (allowRelogin ? 1 : 0) + "</AllowRelogin>\n" +
                "            </ParameterList>\n";
    }
}
