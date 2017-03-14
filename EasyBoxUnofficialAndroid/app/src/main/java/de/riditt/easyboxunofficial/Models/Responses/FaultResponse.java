package de.riditt.easyboxunofficial.Models.Responses;

import okhttp3.Response;

public class FaultResponse {
    private int faultCode;
    private String faultString;
    private String faultLong; // Vodafone calls this "FaultLang"
    private String faultMsgCode;

    public FaultResponse(Response response) {

    }

    public int getFaultCode() {
        return faultCode;
    }

    public String getFaultString() {
        return faultString;
    }

    public String getFaultLong() {
        return faultLong;
    }

    public String getFaultMsgCode() {
        return faultMsgCode;
    }
}
