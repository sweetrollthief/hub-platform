package com.sweetrollthief.hub.gate;

import java.util.Queue;
import java.util.LinkedList;
import java.util.Iterator;
import java.net.InetSocketAddress;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SelectionKey;

import com.sweetrollthief.hub.HubBean;
import com.sweetrollthief.hub.Hub;
import com.sweetrollthief.hub.Gate;
/**
* Network I/O Handling bean
*
**/
public class DefaultGateImpl extends HubBean implements Gate {
    private boolean isOpen;
    private Queue<Integer> listenersQueue = new LinkedList<>();

    @Override
    public void addListener(int port) {
        synchronized (listenersQueue) {
            listenersQueue.add(port);
        }
    }
    @Override
    public void open() {
        isOpen = true;
        try (Selector selector = Selector.open()) {
            Iterator<SelectionKey> it;
            SelectionKey key;

            while (isOpen) {
                check(selector);
                it = selector.selectedKeys().iterator();

                while (it.hasNext()) {


                    it.remove();
                }
            }
        } catch (Exception e) {
            isOpen = false;
            e.printStackTrace();
        }
    }
    @Override
    public void close() {
        isOpen = false;
    }

    private void check(Selector selector) throws Exception {
        Integer port;
        ServerSocketChannel ssc;

        while ((port = listenersQueue.poll()) != null) {
            ssc = ServerSocketChannel.open();
            ssc.bind(new InetSocketAddress(port));
            ssc.configureBlocking(false);
            ssc.register(selector, SelectionKey.OP_ACCEPT);
        }
    }
    private void accept() throws Exception {

    }
    private void connect() throws Exception {

    }
    private void read() throws Exception {

    }
    private void write() throws Exception {

    }
}
