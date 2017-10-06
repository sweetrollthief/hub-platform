package com.sweetrollthief.hub;

public interface Gate {
    void addListener(int port);
    void open();
    void close();
}
