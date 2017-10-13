package com.sweetrollthief.hub.transfer;

public interface IPacketParser<T extends IPacket> {
    void parse(T packet) throws Exception ;
}
