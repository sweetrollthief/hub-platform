package com.sweetrollthief.hub.gate;

import java.util.Queue;
import java.util.LinkedList;
import java.util.Iterator;
import java.net.InetSocketAddress;
import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SelectionKey;

import org.springframework.context.ApplicationContext;

import com.sweetrollthief.hub.Gate;
import com.sweetrollthief.hub.Router;
import com.sweetrollthief.hub.transfer.ConnectionProvider;
import com.sweetrollthief.hub.transfer.ConnectionProvider.STATUS;

/**
* Network I/O Handling bean
*
**/
public class DefaultGateImpl implements Gate {
    private final static int bufferSize = 1024;
    private ApplicationContext context;

    private Selector selector;
    private ByteBuffer buffer = ByteBuffer.allocate(bufferSize);

    private Queue<Integer> listenersQueue = new LinkedList<>();

    @Override
    public void setApplicationContext(ApplicationContext context) {
        this.context = context;
    }

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
        connectionProvider.setLocalAddress((InetSocketAddress) sc.socket().getLocalSocketAddress());
        connectionProvider.setRemoteAddress((InetSocketAddress) sc.socket().getRemoteSocketAddress());

        sc.register(selector, SelectionKey.OP_READ, connectionProvider);
    }
    private void connect(SelectionKey key) throws Exception {

    }
    private void read(SelectionKey key) throws Exception {
        ConnectionProvider provider = (ConnectionProvider) key.attachment();

        if (provider.status().equals(STATUS.OP_READ)) {
            byte[] tempBuffer;
            ByteArrayOutputStream temp = new ByteArrayOutputStream();
            SocketChannel sc = (SocketChannel) key.channel();
            int read = -1;

            while ((read = sc.read(buffer)) > 0) {
                buffer.flip();
                tempBuffer = new byte[read];
                buffer.get(tempBuffer);
                temp.write(tempBuffer);
            }

            if (temp.size() > 0) {
                context.getBean(Router.class).forwardPacket(temp.toByteArray(), provider);
            }
        }
    }
    private void write(SelectionKey key) throws Exception {
        ConnectionProvider provider = (ConnectionProvider) key.attachment();

        if (provider.status().equals(STATUS.OP_WRITE)) {

        }
    }
}
