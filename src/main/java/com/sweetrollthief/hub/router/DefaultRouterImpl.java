package com.sweetrollthief.hub.router;

import java.util.Map;
import java.util.HashMap;
import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;

import com.sweetrollthief.hub.Router;
import com.sweetrollthief.hub.transfer.ProtocolProvider;
import com.sweetrollthief.hub.transfer.ConnectionProvider;
import com.sweetrollthief.hub.transfer.IPacket;

public class DefaultRouterImpl implements Router {
    private Map<Integer, ProtocolProvider> registeredProtocols = new HashMap();

    @Override
    public void registerProtocol(int port, ProtocolProvider protocolProvider) {
        registeredProtocols.put(port, protocolProvider);
    }

    @Override
    public void forwardPacket(byte[] socketData, ConnectionProvider connectionProvider) throws Exception {
        ProtocolProvider protocolProvider = registeredProtocols.get(
            connectionProvider.getLocalAddress().getPort());

        IPacket packet = connectionProvider.getPacket();

        if (packet == null) {
            packet = protocolProvider.getEmptyPacket();
            connectionProvider.setPacket(packet);
        }

        byte[] previousContent = packet.getRawData();

        if (previousContent == null) {
            packet.setRawData(socketData);
        } else {
            ByteArrayOutputStream content = new ByteArrayOutputStream(
                previousContent.length + socketData.length);
            content.write(previousContent);
            content.write(socketData);

            packet.setRawData(content.toByteArray());
        }

        protocolProvider.parse(packet);
    }
}
