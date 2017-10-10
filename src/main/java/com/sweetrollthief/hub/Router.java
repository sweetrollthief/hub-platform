package com.sweetrollthief.hub;

import com.sweetrollthief.hub.transfer.ConnectionProvider;
import com.sweetrollthief.hub.transfer.ProtocolProvider;

/**
* Handles routing layer
*
*
*/
public interface Router {
    void registerProtocol(int port, ProtocolProvider protocolProvider);
    /**
    * Forwards packet with net data and connection provider attached to this connection
    *
    */
    void forwardPacket(byte[] packet, ConnectionProvider connectionProvider) throws Exception;
}
