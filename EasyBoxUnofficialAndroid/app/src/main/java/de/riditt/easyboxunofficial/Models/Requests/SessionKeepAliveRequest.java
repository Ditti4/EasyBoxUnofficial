package de.riditt.easyboxunofficial.Models.Requests;

import okhttp3.Request;

public class SessionKeepAliveRequest extends DmSoapRequest {
    public SessionKeepAliveRequest(String serverUrl, String dmCookie) {
        super(serverUrl, "cwmp:SessionKeepAlive", dmCookie);
    }

    @Override
    protected String getBodyContent() {
        return "            <SessionKeepAlive></SessionKeepAlive>\n";
    }
}
