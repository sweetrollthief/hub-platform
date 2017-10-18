package com.sweetrollthief.hub.transfer.http;

import com.sweetrollthief.hub.transfer.IPacketWrapper;

public class HttpPacketWrapper implements IPacketWrapper<HttpPacket> {
    @Override
    public byte[] wrap(HttpPacket packet) {
        return null;
    }
}
