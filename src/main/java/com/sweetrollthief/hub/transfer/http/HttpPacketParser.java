package com.sweetrollthief.hub.transfer.http;

import java.io.ByteArrayInputStream;
import java.util.Map;
import java.util.HashMap;
import java.util.List;

import com.sweetrollthief.hub.transfer.IPacketParser;
import com.sweetrollthief.hub.transfer.http.HttpPacket.PARSING_STAGE;
import com.sweetrollthief.hub.transfer.http.HttpPacket.SEGMENT_TYPE;
import com.sweetrollthief.hub.transfer.http.HttpPacket.Segment;


public class HttpPacketParser implements IPacketParser<HttpPacket> {
    private final static int SPACE_OFFSET = 1;
    private final static int NEW_LINE_OFFSET = 2;

    private final static byte COLON_BYTE = 58;
    private final static byte CARRIAGERETURN_BYTE = 13;
    private final static byte NEWLINE_BYTE = 10;
    private final static byte SPACE_BYTE = 32;

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
            System.out.println(packet.getMethod() + " " + packet.getURI() + " " + packet.getProtocol());

            Map<String, String> header = packet.getAllHeaders();

            for (Map.Entry<String, String> entry : header.entrySet()) {
                System.out.println(entry.getKey() + ":" + entry.getValue() + "|");
            }

            System.out.println(new String(packet.getBody()));
        }
    }

    private int parseBody(int position, byte[] data, HttpPacket packet) throws Exception {
        int size = data.length;

        // EOF check TODO: handle chunked encoding
        String lengthEntry = packet.getHeader("Content-Length");

        if (lengthEntry == null) {
            packet.setParsingStage(PARSING_STAGE.END); // no length entry for request
            return position;
        } else {
            int requiredLength = Integer.parseInt(lengthEntry);

            if (position + requiredLength == size) {
                packet.createSegment(position, requiredLength, SEGMENT_TYPE.BODY_SEGMENT_CONTENT);
                position = size;
            }
            // handle body writing
            packet.setParsingStage(PARSING_STAGE.END);
        }

        return position;
    }

    private int parseHeaderSegment(int position, byte[] data, HttpPacket packet) throws Exception {
        int size = data.length;
        int lastPosition = position;
        boolean keyFound = false;

        while (position < size) {
            if (data[position] == COLON_BYTE && !keyFound) {
                packet.createSegment(lastPosition, position - lastPosition, SEGMENT_TYPE.HEADER_KEY);
                lastPosition = position + SPACE_OFFSET;
                keyFound = true;
            } else if (data[position] == CARRIAGERETURN_BYTE) {
                while (lastPosition < position) {
                    if (data[lastPosition] == SPACE_BYTE) {
                        lastPosition++;
                        continue;
                    }

                    break;
                }

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
            if (data[position] == SPACE_BYTE) {
                if (lastPosition == 0) {
                    packet.createSegment(lastPosition, position - lastPosition, SEGMENT_TYPE.METHOD);
                    lastPosition = position + SPACE_OFFSET;
                } else {
                    packet.createSegment(lastPosition, position - lastPosition, SEGMENT_TYPE.URI);
                    lastPosition = position + SPACE_OFFSET;
                }
            } else if (data[position] == CARRIAGERETURN_BYTE) {
                packet.createSegment(lastPosition, position - lastPosition, SEGMENT_TYPE.PROTOCOL);
                packet.setParsingStage(PARSING_STAGE.HEADER);
                return position + NEW_LINE_OFFSET;
            }

            position++;
        }

        return position;
    }
}
