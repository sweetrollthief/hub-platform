package com.sweetrollthief.hub.transfer;

public interface IPacketWrapper<T extends IPacket> {
    byte[] wrap(T packet);
}
