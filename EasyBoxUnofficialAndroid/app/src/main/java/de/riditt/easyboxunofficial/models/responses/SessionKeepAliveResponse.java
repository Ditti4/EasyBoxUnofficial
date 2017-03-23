package de.riditt.easyboxunofficial.models.responses;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Namespace;
import org.simpleframework.xml.NamespaceList;
import org.simpleframework.xml.Root;
import org.simpleframework.xml.convert.Convert;

import de.riditt.easyboxunofficial.models.responses.common.SoapBody;
import de.riditt.easyboxunofficial.models.responses.common.SoapEnvelope;
import de.riditt.easyboxunofficial.utilities.SimpleXmlEmptyStringConverter;

@Root(name = "Envelope")
@NamespaceList({
        @Namespace(reference = "http://schemas.xmlsoap.org/soap/envelope/", prefix = "SOAP-ENV"),
})
@Namespace(reference = "http://schemas.xmlsoap.org/soap/envelope/")
public class SessionKeepAliveResponse extends SoapEnvelope {
    @Root
    public static class SessionKeepAliveResponseSoapBody extends SoapBody {
        @Element(name = "SessionKeepAliveResponse", required = false)
        @Convert(SimpleXmlEmptyStringConverter.class)
        private String sessionKeepAliveResponse;

        public String getSessionKeepAliveResponse() {
            return sessionKeepAliveResponse;
        }
    }

    @Element(name = "Body")
    @Namespace(reference = "http://schemas.xmlsoap.org/soap/envelope/")
    private SessionKeepAliveResponseSoapBody soapBody;

    public SessionKeepAliveResponseSoapBody getSoapBody() {
        return soapBody;
    }
}
