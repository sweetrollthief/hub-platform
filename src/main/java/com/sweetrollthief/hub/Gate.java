package com.sweetrollthief.hub;

public interface Gate extends Runnable {
    void addListener(int port);
    void open() throws Exception;
    void close();
}
