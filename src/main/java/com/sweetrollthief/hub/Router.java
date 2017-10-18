package com.sweetrollthief.hub;

import com.sweetrollthief.hub.transfer.ConnectionProvider;
import com.sweetrollthief.hub.transfer.ProtocolProvider;

/**
* Handles routing layer
*
*
*/
public interface Router {
    void registerPort(int port, String protocolName);
    /**
    * Forwards packet with net data and connection provider attached to this connection
    *
    */
    void forwardPacket(byte[] packet, ConnectionProvider connectionProvider) throws Exception;
}
