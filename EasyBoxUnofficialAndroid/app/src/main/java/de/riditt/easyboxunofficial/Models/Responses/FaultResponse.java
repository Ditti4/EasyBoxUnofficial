package de.riditt.easyboxunofficial.Models.Responses;

import okhttp3.Response;

public class FaultResponse {
    private int faultCode;
    private String faultString;
    private String faultLong; // Vodafone calls this "FaultLang"
    private String faultMsgCode;

    public int getFaultCode() {
        return faultCode;
    }

    public void setFaultCode(int faultCode) {
        this.faultCode = faultCode;
    }

    public String getFaultString() {
        return faultString;
    }

    public void setFaultString(String faultString) {
        this.faultString = faultString;
    }

    public String getFaultLong() {
        return faultLong;
    }

    public void setFaultLong(String faultLong) {
        this.faultLong = faultLong;
    }

    public String getFaultMsgCode() {
        return faultMsgCode;
    }

    public void setFaultMsgCode(String faultMsgCode) {
        this.faultMsgCode = faultMsgCode;
    }
}
