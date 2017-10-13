package com.sweetrollthief.hub.transfer.http;

import java.util.Map;
import java.util.HashMap;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.LinkedList;

import com.sweetrollthief.hub.transfer.IPacket;

public class HttpPacket implements IPacket {
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
    class Element {
        private int position;
        private int length;
        private SEGMENT_TYPE type;

        public Element(int position, int length, SEGMENT_TYPE type) {
            this.position = position;
            this.length = length;
            this.type = type;
        }
        public int length() {
            return length;
        }
        public int position() {
            return position;
        }
        public SEGMENT_TYPE type() {
            return type;
        }
    }

    private int parsingPosition;

    private byte[] rawData;
    private List<Element> elementList = new LinkedList<>();

    @Override
    public void setRawData(byte[] data) {
        this.rawData = data;
    }
    @Override
    public byte[] getRawData() {
        return rawData;
    }
    public String getMethod() {
        return getEssential(SEGMENT_TYPE.METHOD);
    }
    public int getCode() throws NullPointerException {
        String code = getEssential(SEGMENT_TYPE.CODE);

        if (code == null) {
            throw new NullPointerException();
        }

        return Integer.parseInt(code);
    }
    public String getProtocol() {
        return getEssential(SEGMENT_TYPE.PROTOCOL);
    }
    public String getURI() {
        return getEssential(SEGMENT_TYPE.URI);
    }
    public String getHeader(String headerKey) {
        Iterator<Element> it = elementList.iterator();
        Element tempKey, tempValue;

        while (it.hasNext()) {
            tempKey = it.next();

            // TODO: ikr
            if (tempKey.type() == SEGMENT_TYPE.HEADER_KEY
                && headerKey.equals(
                    new String (
                        Arrays.copyOfRange(
                            rawData,
                            tempKey.position(),
                            tempKey.position() + tempKey.length())))) {
                tempValue = it.next();
                return new String(Arrays.copyOfRange(
                    rawData, tempValue.position(), tempValue.position() + tempValue.length()));
            }
        }

        return null;
    }
    public Map<String, String> getHeader() {
        Map<String, String> header = new HashMap<>();

        Iterator<Element> it = elementList.iterator();
        Element tempKey, tempValue;

        while (it.hasNext()) {
            tempKey = it.next();

            if (tempKey.type() == SEGMENT_TYPE.HEADER_KEY) {
                tempValue = it.next();
                header.put(
                    new String(Arrays.copyOfRange(
                        rawData, tempKey.position(), tempKey.position() + tempKey.length())),
                    new String(Arrays.copyOfRange(
                        rawData, tempValue.position(), tempValue.position() + tempValue.length())));
            }
        }

        return header;
    }
    private String getEssential(SEGMENT_TYPE type) {
        Iterator<Element> it = elementList.iterator();
        Element temp;

        while (it.hasNext()) {
            temp = it.next();
            if (temp.type() == type) {
                return new String(Arrays.copyOfRange(
                    rawData, temp.position(), temp.position() + temp.length()));
            }
        }

        return null;
    }
    void appendElement(int position, int length, SEGMENT_TYPE type) {
        elementList.add(new Element(position, length, type));
    }
    List<Element> getElementList() {
        return elementList;
    }
    int getBodyLength() {
        int length = 0;

        Iterator<Element> it = elementList.iterator();
        Element temp;

        while (it.hasNext()) {
            temp = it.next();

            if (temp.type() == SEGMENT_TYPE.BODY_SEGMENT_CONTENT) {
                length += temp.length();
            }
        }

        return length;
    }
    /**
    * @return current logical parsing part of message
    * 0 - before first line
    * 1 - first line
    * 2 - header
    * 3 - content
    * 4 - after content
    **/
    int getParsingPosition() {
        return parsingPosition;
    }
    void setParsingPosition(int position) {
        this.parsingPosition = position;
    }
    /**
    * @return position of last byte of last pasrsed element
    *
    */
    int getCurrentPosition() {
        if (elementList.size() == 0) {
            return 0;
        } else {
            Element lastElement = elementList.get(elementList.size() - 1);
            return lastElement.position() + lastElement.length();
        }
    }
}
