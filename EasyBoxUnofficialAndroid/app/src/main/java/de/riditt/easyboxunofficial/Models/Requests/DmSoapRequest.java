package de.riditt.easyboxunofficial.Models.Requests;

import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;

public class DmSoapRequest extends SoapRequest {
    private String dmCookie;

    public DmSoapRequest(String serverUrl, String soapAction, String dmCookie) {
        super(serverUrl, soapAction);
        this.dmCookie = dmCookie;
    }

    public String getDmCookie() {
        return dmCookie;
    }

    protected String getBodyContent() {
        return "";
    }

    protected String getSoapEnvelope() {
        return "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\">\n" +
                "    <soapenv:Header>\n" +
                "        <DMCookie>" + dmCookie + "</DMCookie>\n" +
                "        <SessionNotRefresh>0</SessionNotRefresh>\n" +
                "    </soapenv:Header>\n" +
                "    <soapenv:Body>\n" +
                "        <" + getSoapAction() + " xmlns=\"\">\n" +
                getBodyContent() +
                "        </" + getSoapAction() + ">\n" +
                "    </soapenv:Body>\n" +
                "</soapenv:Envelope>";
    }

    public Request getRequest() {
        return new Request.Builder()
                .url("http://" + getServerUrl() + "/data_model.cgi")
                .addHeader("SOAPServer", "")
                .addHeader("SOAPAction", getSoapAction())
                .post(RequestBody.create(MediaType.parse("text/xml"), getSoapEnvelope()))
                .build();
    }
}
