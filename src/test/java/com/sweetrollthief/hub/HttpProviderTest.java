package com.sweetrollthief.hub;

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

            IPacketWrapper wrapper = ProtocolProvider.getWrapper("http");
            assertTrue(wrapper instanceof HttpPacketWrapper);
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

        return true;
    }

    private byte[] getRawData() {
        return new StringBuilder(method)
            .append(" ")
            .append(uri)
            .append(" ")
            .append(protocol)
            .append("\r\n")
            .append("Header1: HeaderValue1\r\n")
            .append("Header2: HeaderValue2\r\n")
            .append("Content-Length: 4\r\n")
            .append("Header3: HeaderValue3\r\n")
            .append("Header4: HeaderValue4\r\n")
            .append("\r\n")
            .append("body")
            .toString()
            .getBytes();
    }
}
