package de.riditt.easyboxunofficial.Models.Requests;

import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;

public class SoapRequest {
    private String serverUrl;
    // SOAPServer is intentionally left out since - let's face it - the EasyBox doesn't use it at all
    private String soapAction;

    public SoapRequest(String serverUrl, String soapAction) {
        this.serverUrl = serverUrl;
        this.soapAction = soapAction;
    }

    public String getServerUrl() {
        return serverUrl;
    }

    public String getSoapAction() {
        return this.soapAction;
    }

    protected Request getBaseRequest() {
        return new Request.Builder()
                .url("http://" + serverUrl + "/data_model.cgi")
                .addHeader("SOAPServer", "")
                .addHeader("SOAPAction", soapAction)
                .build();
    }
}
