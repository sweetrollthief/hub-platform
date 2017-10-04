package com.sweetrollthief.hub.gate;

import java.nio.channels.Selector;

import com.sweetrollthief.hub.HubBean;
import com.sweetrollthief.hub.Gate;
/**
* Network I/O Handling bean
*
**/
public class DefaultGateImpl extends HubBean implements Gate {
    @Override
    public void open() {
        try (Selector selector = Selector.open()) {

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @Override
    public void close() {
        System.out.println("close");
    }
}
