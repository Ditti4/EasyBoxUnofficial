package de.riditt.easyboxunofficial.models.requests;

public class SessionKeepAliveRequest extends DmSoapRequest {
    public SessionKeepAliveRequest(String serverUrl, String dmCookie) {
        super(serverUrl, "cwmp:SessionKeepAlive", dmCookie);
    }

    @Override
    protected String getBodyContent() {
        return "            <SessionKeepAlive></SessionKeepAlive>\n";
    }
}
