package com.sweetrollthief.hub.router;

import java.util.Map;
import java.util.HashMap;
import java.nio.ByteBuffer;

import com.sweetrollthief.hub.Router;
import com.sweetrollthief.hub.transfer.ProtocolProvider;
import com.sweetrollthief.hub.transfer.ConnectionProvider;
import com.sweetrollthief.hub.transfer.IPacket;

public class DefaultRouterImpl implements Router {
    private Map<Integer, String> registeredPorts = new HashMap();

    @Override
    public void registerPort(int port, String protocolName) {
        registeredPorts.put(port, protocolName);
    }

    @Override
    public void forwardPacket(byte[] socketData, ConnectionProvider connectionProvider) throws Exception {
        String protocolName = registeredPorts.get(connectionProvider.getLocalAddress().getPort());
        IPacket packet = connectionProvider.getPacket();

        if (packet == null) {
            packet = ProtocolProvider.getEmptyPacket(protocolName);
            connectionProvider.setPacket(packet);
        }

        packet.appendRawData(socketData);

        ProtocolProvider.getParser(protocolName).parse(packet);
    }
}
