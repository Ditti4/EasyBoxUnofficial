package de.riditt.easyboxunofficial.models.responses.common;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Namespace;
import org.simpleframework.xml.Root;
import org.simpleframework.xml.convert.Convert;

import de.riditt.easyboxunofficial.utilities.SimpleXmlEmptyStringConverter;

@Root
@Namespace
public class CwmpFault {
    @Element(name = "FaultCode")
    private int faultCode;

    @Element(name = "FaultString")
    private String faultString;

    @Element(name = "FaultLang")
    private String faultLong;

    @Element(name = "FaultMsgCode", required = false)
    @Convert(SimpleXmlEmptyStringConverter.class)
    private String faultMsgCode;

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
