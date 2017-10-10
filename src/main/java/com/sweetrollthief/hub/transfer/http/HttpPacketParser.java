package com.sweetrollthief.hub.transfer.http;

import com.sweetrollthief.hub.transfer.IPacketParser;

class HttpPacketParser implements IPacketParser<HttpPacket> {
    @Override
    public void parse(HttpPacket packet) {
        byte[] packetData = packet.getRawData();
        int size = packetData.length;

        System.out.println();

        for (int i = 0; i < size; i++) {
            System.out.print((char) packetData[i]);
        }

        System.out.println();
    }
}
