package com.sweetrollthief.hub.transfer.http;

import java.util.Map;
import java.util.HashMap;
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
                case 2:
                    position = parseHeaderSegment(position, data, packet);
                    break;
                default:
                    position++;
            }
        }

        System.out.println("METHOD |" + packet.getMethod() + "|");
        System.out.println("URI |" + packet.getURI() + "|");
        System.out.println("PROTOCOL |" + packet.getProtocol() + "|");

        Map<String, String> header = packet.getHeader();

        for (Map.Entry<String, String> entry : header.entrySet()) {
            System.out.println("HEADER_LINE |" + entry.getKey() + ":" + entry.getValue() + "|");
        }
    }

    private int parseHeaderSegment(int position, byte[] data, HttpPacket packet) {
        int size = data.length;
        int lastPosition = position;
        boolean keyFound = false;

        while (position < size) {
            if (data[position] == 58 && !keyFound) {
                packet.appendElement(lastPosition, position - lastPosition, SEGMENT_TYPE.HEADER_KEY);
                lastPosition = position + 1;
                keyFound = true;
            } else if (data[position] == 13) {
                packet.appendElement(lastPosition, position - lastPosition, SEGMENT_TYPE.HEADER_VALUE);
                lastPosition = position + 2;
                keyFound = false;
            }

            position++;
        }

        return ++position;
    }
    private int parseFirstString(int position, byte[] data, HttpPacket packet) {
        int size = data.length;
        boolean isRequest = false;
        int lastPosition = position;

        while (position < size) {
            if (data[position] == 32) {
                if (lastPosition == 0) {
                    packet.appendElement(lastPosition, position - lastPosition, SEGMENT_TYPE.METHOD);
                    lastPosition = position + 1;
                } else {
                    packet.appendElement(lastPosition, position - lastPosition, SEGMENT_TYPE.URI);
                    lastPosition = position + 1;
                }
            } else if (data[position] == 13) {
                packet.appendElement(lastPosition, position - lastPosition, SEGMENT_TYPE.PROTOCOL);
                packet.setParsingPosition(2);
                return position + 2;
            }

            position++;
        }

        return position;
    }
}
