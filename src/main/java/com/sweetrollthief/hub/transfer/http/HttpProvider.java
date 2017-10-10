package com.sweetrollthief.hub.transfer.http;

import com.sweetrollthief.hub.transfer.ProtocolProvider;

public class HttpProvider extends ProtocolProvider<HttpPacket> {
    public HttpProvider() {
        this.parser = new HttpPacketParser();
        this.wrapper = new HttpPacketWrapper();
    }

    @Override
    public HttpPacket getEmptyPacket() {
        return new HttpPacket();
    }
}
