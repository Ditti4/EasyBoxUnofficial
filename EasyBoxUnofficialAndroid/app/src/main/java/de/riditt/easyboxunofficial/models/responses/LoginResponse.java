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
public class LoginResponse extends SoapEnvelope {
    @Root
    public static class LoginResponseSoapBody extends SoapBody {
        @Element(name = "LoginResponse", required = false)
        @Convert(SimpleXmlEmptyStringConverter.class)
        private String loginResponse;

        public String getLoginResponse() {
            return loginResponse;
        }
    }

    @Element(name = "Body")
    @Namespace(reference = "http://schemas.xmlsoap.org/soap/envelope/")
    private LoginResponseSoapBody soapBody;

    public LoginResponseSoapBody getSoapBody() {
        return soapBody;
    }
}
