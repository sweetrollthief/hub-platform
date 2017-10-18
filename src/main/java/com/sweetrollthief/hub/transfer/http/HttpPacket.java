package com.sweetrollthief.hub.transfer.http;

import java.io.ByteArrayOutputStream;
import java.util.Map;
import java.util.HashMap;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.LinkedList;

import com.sweetrollthief.hub.transfer.IPacket;

// TODO: create HttpRequest, HttpResponse
public class HttpPacket implements IPacket {
    enum PARSING_STAGE {
        START,
        FIRST_LINE,
        HEADER,
        BODY,
        END
    }
    enum SEGMENT_TYPE {
        METHOD,
        CODE,
        CODE_DESCRIPTION,
        PROTOCOL,
        URI,
        HEADER_KEY,
        HEADER_VALUE,
        BODY_SEGMENT_LENGTH,
        BODY_SEGMENT_CONTENT
    }
    class Segment {
        private int position;
        private int length;
        private SEGMENT_TYPE type;

        public Segment(int position, int length, SEGMENT_TYPE type) {
            this.position = position;
            this.length = length;
            this.type = type;
        }
        public int getLength() {
            return length;
        }
        public int getPosition() {
            return position;
        }
        public SEGMENT_TYPE getType() {
            return type;
        }
    }

    private PARSING_STAGE currentParsingStage = PARSING_STAGE.START;
    private byte[] rawData;
    private List<Segment> segmentList = new LinkedList<>();

    @Override
    public void setRawData(byte[] data) {
        this.rawData = data;
    }
    @Override
    public void appendRawData(byte[] data) throws Exception {
        if (rawData == null) {
            rawData = data;
        } else {
            ByteArrayOutputStream content =
                new ByteArrayOutputStream(rawData.length + data.length);
            content.write(rawData);
            content.write(data);

            rawData = content.toByteArray();
        }
    }
    @Override
    public byte[] getRawData() {
        return rawData;
    }
    public String getMethod() {
        return findSegment(SEGMENT_TYPE.METHOD);
    }
    public int getCode() throws NullPointerException {
        return Integer.parseInt(findSegment(SEGMENT_TYPE.CODE));
    }
    public String getProtocol() {
        return findSegment(SEGMENT_TYPE.PROTOCOL);
    }
    public String getURI() {
        return findSegment(SEGMENT_TYPE.URI);
    }
    public String getHeader(String headerKey) {
        Iterator<Segment> it = segmentList.iterator();
        Segment key, value;

        while (it.hasNext()) {
            key = it.next();

            // TODO: ikr
            if (key.getType() == SEGMENT_TYPE.HEADER_KEY
                && headerKey.equals(getSegmentValue(key))) {
                value = it.next();
                return getSegmentValue(value);
            }
        }

        return null;
    }
    public Map<String, String> getAllHeaders() {
        Map<String, String> headerList = new HashMap<>();

        Iterator<Segment> it = segmentList.iterator();
        Segment key, value;

        while (it.hasNext()) {
            key = it.next();

            if (key.getType() == SEGMENT_TYPE.HEADER_KEY) {
                value = it.next();
                headerList.put(
                    getSegmentValue(key),
                    getSegmentValue(value));
            }
        }

        return headerList;
    }
    private String findSegment(SEGMENT_TYPE type) {
        Iterator<Segment> it = segmentList.iterator();
        Segment segment;

        while (it.hasNext()) {
            segment = it.next();
            if (segment.getType() == type) {
                return getSegmentValue(segment);
            }
        }

        return null;
    }
    private String getSegmentValue(Segment segment) {
        return new String(Arrays.copyOfRange(
            rawData, segment.getPosition(), segment.getPosition() + segment.getLength()));
    }
    void createSegment(int position, int length, SEGMENT_TYPE type) {
        segmentList.add(new Segment(position, length, type));
    }
    List<Segment> getSegmentList() {
        return segmentList;
    }
    int getBodyLength() {
        int length = 0;

        Iterator<Segment> it = segmentList.iterator();
        Segment segment;

        while (it.hasNext()) {
            segment = it.next();

            if (segment.getType() == SEGMENT_TYPE.BODY_SEGMENT_CONTENT) {
                length += segment.getLength();
            }
        }

        return length;
    }
    PARSING_STAGE getParsingStage() {
        return currentParsingStage;
    }
    void setParsingStage(PARSING_STAGE stage) {
        currentParsingStage = stage;
    }
    /**
    * @return position of last byte of last pasrsed element
    *
    */
    int getCurrentPosition() {
        if (segmentList.size() == 0) {
            return 0;
        } else {
            Segment lastSegment = segmentList.get(segmentList.size() - 1);
            return lastSegment.getPosition() + lastSegment.getLength();
        }
    }
}
