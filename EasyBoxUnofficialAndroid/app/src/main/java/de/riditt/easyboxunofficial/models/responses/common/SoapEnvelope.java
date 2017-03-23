package de.riditt.easyboxunofficial.models.responses.common;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Namespace;
import org.simpleframework.xml.NamespaceList;
import org.simpleframework.xml.Root;

@Root(name = "Envelope")
@NamespaceList({
        @Namespace(reference = "http://schemas.xmlsoap.org/soap/envelope/", prefix = "SOAP-ENV")
})
@Namespace(reference = "http://schemas.xmlsoap.org/soap/envelope/")
public class SoapEnvelope {
    @Element(name = "Header", required = false)
    @Namespace(reference = "http://schemas.xmlsoap.org/soap/envelope/")
    private SoapHeader soapHeader;

    public SoapHeader getSoapHeader() {
        return soapHeader;
    }
}
