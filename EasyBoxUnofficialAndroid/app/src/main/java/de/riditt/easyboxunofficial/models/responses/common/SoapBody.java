package de.riditt.easyboxunofficial.models.responses.common;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Namespace;
import org.simpleframework.xml.Root;

@Root
public class SoapBody {
    @Attribute(required = false)
    @Namespace(reference = "http://schemas.xmlsoap.org/soap/encoding/", prefix = "SOAP-ENC")
    private String encodingStyle;

    @Element(name = "Fault", required = false)
    @Namespace(reference = "http://schemas.xmlsoap.org/soap/envelope/", prefix = "SOAP-ENV")
    private SoapEnvFault soapEnvFault;

    public String getEncodingStyle() {
        return encodingStyle;
    }

    public SoapEnvFault getSoapEnvFault() {
        return soapEnvFault;
    }
}
