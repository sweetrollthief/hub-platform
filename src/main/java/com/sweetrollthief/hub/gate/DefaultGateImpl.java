package com.sweetrollthief.hub.gate;

import java.util.Queue;
import java.util.LinkedList;
import java.util.Iterator;
import java.net.InetSocketAddress;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SelectionKey;

import com.sweetrollthief.hub.Gate;
import com.sweetrollthief.hub.gate.ConnectionProvider.STATUS;

/**
* Network I/O Handling bean
*
**/
public class DefaultGateImpl implements Gate {
    private Selector selector;

    private Queue<Integer> listenersQueue = new LinkedList<>();

    @Override
    public void addListener(int port) {
        synchronized (listenersQueue) {
            listenersQueue.add(port);
        }
    }
    @Override
    public void run() {
        try {
            this.open();
        } catch (Exception e) {
            e.printStackTrace();
        }

        close();
    }
    @Override
    public void open() throws Exception {
        selector = Selector.open();

        Iterator<SelectionKey> it;
        SelectionKey key;

        while (true) {
            check();
            selector.select();

            it = selector.selectedKeys().iterator();

            while (it.hasNext()) {
                key = it.next();
                it.remove();

                if (key.isValid()) {
                    if (key.isReadable()) {
                        read(key);
                    } else if (key.isWritable()) {
                        write(key);
                    } else if (key.isConnectable()) {
                        connect(key);
                    } else if (key.isAcceptable()) {
                        accept(key);
                    }
                }
            }
        }
    }
    @Override
    public void close() {
        if (selector != null) {
            try {
                selector.close();
            } catch (Exception ie) {}
        }
    }

    private void check() throws Exception {
        Integer port;
        ServerSocketChannel ssc;

        synchronized (listenersQueue) {
            while ((port = listenersQueue.poll()) != null) {
                ssc = ServerSocketChannel.open();
                ssc.configureBlocking(false);
                ssc.bind(new InetSocketAddress(port));
                ssc.register(selector, SelectionKey.OP_ACCEPT);
            }
        }
    }
    private void accept(SelectionKey key) throws Exception {
        ServerSocketChannel ssc = (ServerSocketChannel) key.channel();
        SocketChannel sc = ssc.accept();
        sc.configureBlocking(false);

        ConnectionProvider connectionProvider = new ConnectionProvider(STATUS.OP_READ);
        sc.register(selector, SelectionKey.OP_READ, connectionProvider);
    }
    private void connect(SelectionKey key) throws Exception {

    }
    private void read(SelectionKey key) throws Exception {
        ConnectionProvider provider = (ConnectionProvider) key.attachment();

        if (provider.status().equals(STATUS.OP_READ)) {
            System.out.println(((SocketChannel) key.channel()).socket().getLocalPort());
        }
    }
    private void write(SelectionKey key) throws Exception {
        ConnectionProvider provider = (ConnectionProvider) key.attachment();

        if (provider.status().equals(STATUS.OP_WRITE)) {

        }
    }
}
