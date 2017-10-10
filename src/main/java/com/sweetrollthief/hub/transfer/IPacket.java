package com.sweetrollthief.hub.transfer;

public interface IPacket {
    void setRawData(byte[] data);
    byte[] getRawData();
}
