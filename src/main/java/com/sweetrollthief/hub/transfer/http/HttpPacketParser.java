package com.sweetrollthief.hub.transfer.http;

import java.util.Arrays;
import java.util.List;
import java.io.ByteArrayInputStream;

import com.sweetrollthief.hub.transfer.IPacketParser;
import com.sweetrollthief.hub.transfer.http.HttpPacket.SEGMENT_TYPE;
import com.sweetrollthief.hub.transfer.http.HttpPacket.Element;

class HttpPacketParser implements IPacketParser<HttpPacket> {
    @Override
    public void parse(HttpPacket packet) {
        if (packet.getParsingPosition() > 3) return;

        byte[] data = packet.getRawData();
        int position = packet.getCurrentPosition();
        int size = data.length;

        System.out.println(position + " " + size + " " + packet.getParsingPosition());

        while (position < size) {
            switch (packet.getParsingPosition()) {
                case 0:
                case 1:
                    position = parseFirstString(position, data, packet);
                    break;
                default:
                    position++;
            }
        }
    }

    private int parseFirstString(int position, byte[] data, HttpPacket packet) {
        int size = data.length;
        List<Element> elementList = packet.getElementList();
        boolean isRequest = false;
        int lastPosition = position;

        while (position < size) {
            if (data[position] == 32) {
                if (elementList.size() == 0) {
                    System.out.println("FIRST: |" + new String(Arrays.copyOfRange(data, lastPosition, position)) + "|");
                    packet.appendElement(lastPosition, position, SEGMENT_TYPE.METHOD); // if request
                    lastPosition = position + 1;
                } else {
                    System.out.println("SECOND: |" + new String(Arrays.copyOfRange(data, lastPosition, position)) + "|");
                    lastPosition = position + 1;
                }
            } else if (data[position] == 13) {
                System.out.println("THIRD: |" + new String(Arrays.copyOfRange(data, lastPosition, position)) + "|");
                packet.setParsingPosition(2);
                return ++position;
            }

            position++;
        }

        return position;
    }
}
