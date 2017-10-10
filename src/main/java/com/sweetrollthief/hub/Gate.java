package com.sweetrollthief.hub;

import org.springframework.context.ApplicationContextAware;

/**
* Handles IO layer
*
*
**/
public interface Gate extends Runnable, ApplicationContextAware {
    void addListener(int port);
    void open() throws Exception;
    void close();
}
