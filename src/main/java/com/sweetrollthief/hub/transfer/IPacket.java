package com.sweetrollthief.hub.transfer;

public interface IPacket {
    void setRawData(byte[] data);
    void appendRawData(byte[] data) throws Exception;
    byte[] getRawData();
}
