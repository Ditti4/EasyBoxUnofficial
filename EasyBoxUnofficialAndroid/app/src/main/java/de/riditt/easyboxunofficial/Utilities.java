package de.riditt.easyboxunofficial;

import java.io.IOException;
import java.security.MessageDigest;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okio.Buffer;

public class Utilities {
    public static String md5(final String toEncrypt) {
        try {
            final MessageDigest digest = MessageDigest.getInstance("md5");
            digest.update(toEncrypt.getBytes());
            final byte[] bytes = digest.digest();
            final StringBuilder sb = new StringBuilder();
            for (byte aByte : bytes) {
                sb.append(String.format("%02X", aByte));
            }
            return sb.toString().toLowerCase();
        } catch (Exception exc) {
            return "";
        }
    }

    public static RequestBody createRequestBodyFromSoapEnvelope(String soapEnvelope) {
        return RequestBody.create(MediaType.parse("text/xml"), soapEnvelope);
    }

    public static String getStringFromRequestBody(RequestBody requestBody) {
        try {
            Buffer buffer = new Buffer();
            requestBody.writeTo(buffer);
            return buffer.readUtf8();
        } catch (IOException e) {
            return "";
        }
    }
}
