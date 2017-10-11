package com.sweetrollthief.hub.transfer.http;

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
    void appendElement(int position, int length, SEGMENT_TYPE type) {
        elementList.add(new Element(position, length, type));
    }
    List<Element> getElementList() {
        return elementList;
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
