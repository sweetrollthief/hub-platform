package com.sweetrollthief.hub.transfer.http;

import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.io.ByteArrayInputStream;

import com.sweetrollthief.hub.transfer.IPacketParser;
import com.sweetrollthief.hub.transfer.http.HttpPacket.PARSING_STAGE;
import com.sweetrollthief.hub.transfer.http.HttpPacket.SEGMENT_TYPE;
import com.sweetrollthief.hub.transfer.http.HttpPacket.Segment;


class HttpPacketParser implements IPacketParser<HttpPacket> {
    private final static int SPACE_OFFSET = 1;
    private final static int NEW_LINE_OFFSET = 2;

    @Override
    public void parse(HttpPacket packet) throws Exception {
        if (packet.getParsingStage() == PARSING_STAGE.END) return;

        byte[] data = packet.getRawData();
        int position = packet.getCurrentPosition();
        int size = data.length;

        while (position < size) {
            switch (packet.getParsingStage()) {
                case START:
                case FIRST_LINE:
                    position = parseFirstString(position, data, packet);
                    break;
                case HEADER:
                    position = parseHeaderSegment(position, data, packet);
                    break;
                case BODY:
                    position = parseBody(position, data, packet);
            }
        }

        if (packet.getParsingStage() == PARSING_STAGE.END) {
            System.out.println("METHOD |" + packet.getMethod() + "|");
            System.out.println("URI |" + packet.getURI() + "|");
            System.out.println("PROTOCOL |" + packet.getProtocol() + "|");

            Map<String, String> header = packet.getAllHeaders();

            for (Map.Entry<String, String> entry : header.entrySet()) {
                System.out.println("HEADER_LINE |" + entry.getKey() + ":" + entry.getValue() + "|");
            }

            System.out.println("PARSED SUCCESSFULLY");
        }
    }

    private int parseBody(int position, byte[] data, HttpPacket packet) throws Exception {
        int size = data.length;

        while (position < size) {
            System.out.println("new char");
            position++;
        }

        // EOF check TODO: handle chunked encoding
        int requiredLength;
        String lengthEntry = packet.getHeader("Content-Length");
        if (lengthEntry == null) {
            packet.setParsingStage(PARSING_STAGE.END); // no length entry for request
            return position;
        } else if (packet.getBodyLength() == Integer.parseInt(lengthEntry)) {
            packet.setParsingStage(PARSING_STAGE.END);
        }

        return position;
    }

    private int parseHeaderSegment(int position, byte[] data, HttpPacket packet) throws Exception {
        int size = data.length;
        int lastPosition = position;
        boolean keyFound = false;

        while (position < size) {
            if (data[position] == (byte) ':' && !keyFound) {
                packet.createSegment(lastPosition, position - lastPosition, SEGMENT_TYPE.HEADER_KEY);
                lastPosition = position + SPACE_OFFSET;
                keyFound = true;
            } else if (data[position] == (byte) '\n') {
                if (position - lastPosition == 0) {
                    packet.setParsingStage(PARSING_STAGE.BODY);
                    return parseBody(position + NEW_LINE_OFFSET, data, packet);
                }

                packet.createSegment(lastPosition, position - lastPosition, SEGMENT_TYPE.HEADER_VALUE);
                lastPosition = position + NEW_LINE_OFFSET;
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
            if (data[position] == (byte) ' ') {
                if (lastPosition == 0) {
                    packet.createSegment(lastPosition, position - lastPosition, SEGMENT_TYPE.METHOD);
                    lastPosition = position + SPACE_OFFSET;
                } else {
                    packet.createSegment(lastPosition, position - lastPosition, SEGMENT_TYPE.URI);
                    lastPosition = position + SPACE_OFFSET;
                }
            } else if (data[position] == (byte) '\n') {
                packet.createSegment(lastPosition, position - lastPosition, SEGMENT_TYPE.PROTOCOL);
                packet.setParsingStage(PARSING_STAGE.HEADER);
                return position + NEW_LINE_OFFSET;
            }

            position++;
        }

        return position;
    }
}
