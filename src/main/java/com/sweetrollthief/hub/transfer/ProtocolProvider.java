package com.sweetrollthief.hub.transfer;

public abstract class ProtocolProvider<T extends IPacket> {
    protected IPacketParser<T> parser;
    protected IPacketWrapper<T> wrapper;

    public abstract T getEmptyPacket();

    public void parse(T packet) throws Exception {
        parser.parse(packet);
    }

    public byte[] wrap(T packet) {
        return wrapper.wrap(packet);
    }
}
