package com.sweetrollthief.hub.transfer;

import com.sweetrollthief.hub.transfer.http.*;

public class ProtocolProvider {
    public static IPacketParser getParser(String protocolName) throws Exception {
        if (protocolName.equals("http")) {
            return new HttpPacketParser();
        }

        throw new Exception("Unsupported protocol");
    }
    public static IPacketWrapper getWrapper(String protocolName) throws Exception {
        if (protocolName.equals("http")) {
            return new HttpPacketWrapper();
        }

        throw new Exception("Unsupported protocol");
    }
    public static IPacket getEmptyPacket(String protocolName) throws Exception {
        if (protocolName.equals("http")) {
            return new HttpPacket();
        }

        throw new Exception("Unsupported protocol");
    }
}
