package de.riditt.easyboxunofficial.Models.Requests;

class SoapRequest {
    private String serverUrl;
    // SOAPServer is intentionally left out since - let's face it - the EasyBox doesn't use it at all
    private String soapAction;

    SoapRequest(String serverUrl, String soapAction) {
        this.serverUrl = serverUrl;
        this.soapAction = soapAction;
    }

    String getServerUrl() {
        return serverUrl;
    }

    String getSoapAction() {
        return this.soapAction;
    }
}
