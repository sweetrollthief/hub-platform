package com.sweetrollthief.hub.transfer.http;

import com.sweetrollthief.hub.transfer.IPacket;

public class HttpPacket implements IPacket {
    private byte[] rawData;

    @Override
    public void setRawData(byte[] data) {
        this.rawData = data;
    }
    @Override
    public byte[] getRawData() {
        return rawData;
    }
}
