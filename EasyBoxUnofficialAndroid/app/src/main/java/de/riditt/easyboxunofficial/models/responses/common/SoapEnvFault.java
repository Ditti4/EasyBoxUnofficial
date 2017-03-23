package de.riditt.easyboxunofficial.models.responses.common;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Namespace;
import org.simpleframework.xml.Root;

@Root
public class SoapEnvFault {
    @Root
    public static class SoapEnvFaultDetail {
        @Element(name = "Fault")
        @Namespace(reference = "urn:dslforum-org:cwmp-1-0", prefix = "cwmp")
        private CwmpFault cwmpFault;

        public CwmpFault getCwmpFault() {
            return cwmpFault;
        }
    }

    @Element(name = "faultcode")
    private String faultCode;

    @Element(name = "faultstring")
    private String faultString;

    @Element(name = "detail")
    private SoapEnvFaultDetail soapEnvFaultDetail;

    public String getFaultCode() {
        return faultCode;
    }

    public String getFaultString() {
        return faultString;
    }

    public SoapEnvFaultDetail getSoapEnvFaultDetail() {
        return soapEnvFaultDetail;
    }
}
