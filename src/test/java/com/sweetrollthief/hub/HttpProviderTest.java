package com.sweetrollthief.hub;

import java.util.Arrays;
import java.util.Map;
import java.util.HashMap;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import com.sweetrollthief.hub.transfer.ProtocolProvider;
import com.sweetrollthief.hub.transfer.IPacket;
import com.sweetrollthief.hub.transfer.IPacketParser;
import com.sweetrollthief.hub.transfer.IPacketWrapper;
import com.sweetrollthief.hub.transfer.http.*;

public class HttpProviderTest extends TestCase {
    private static final String method = "GET";
    private static final String uri = "/URI";
    private static final String protocol = "HTTP/1.1";
    private static final Map<String,String> header = new HashMap<String,String>() {
            {
                put("HeaderKey1", "HeaderValue1");
                put("HeaderKey2", "HeaderValue2");
                put("Content-Length", "4");
                put("HeaderKey3", "HeaderValue3");
            }
    };

    public HttpProviderTest(String testName) {
        super(testName);
    }

    public static Test suite() {
        return new TestSuite(HttpProviderTest.class);
    }

    public void testProvider() {
        try {
            HttpPacket packet = (HttpPacket) ProtocolProvider.getEmptyPacket("http");
            assertTrue(packet instanceof HttpPacket);

            assertTrue(testParser(packet));
            assertTrue(testWrapper(packet));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean testParser(HttpPacket packet) throws Exception {
        IPacketParser parser = ProtocolProvider.getParser("http");
        assertTrue(parser instanceof HttpPacketParser);

        packet.appendRawData(getRawData());
        parser.parse(packet);

        assertTrue(method.equals(packet.getMethod()));
        assertTrue(uri.equals(packet.getURI()));
        assertTrue(protocol.equals(packet.getProtocol()));

        for(Map.Entry<String,String> entry : header.entrySet()) {
            assertTrue(packet.getHeader(entry.getKey()).equals(entry.getValue()));
        }

        assertTrue(Arrays.equals(getBody().getBytes(), packet.getBody()));

        return true;
    }
    public boolean testWrapper(HttpPacket packet) throws Exception {
        IPacketWrapper wrapper = ProtocolProvider.getWrapper("http");
        assertTrue(wrapper instanceof HttpPacketWrapper);

        assertTrue(Arrays.equals(wrapper.wrap(packet), getRawData()));

        return true;
    }

    private byte[] getRawData() {
        StringBuilder builder = new StringBuilder(method)
            .append(" ")
            .append(uri)
            .append(" ")
            .append(protocol)
            .append("\r\n");

        for(Map.Entry<String,String> entry : header.entrySet()) {
            builder.append(entry.getKey())
                .append(":")
                .append(entry.getValue())
                .append("\r\n");
        }

        builder.append("\r\n")
            .append(getBody());

        return builder.toString().getBytes();
    }

    private String getBody() {
        return "body";
    }
}
