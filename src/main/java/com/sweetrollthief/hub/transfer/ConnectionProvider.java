package com.sweetrollthief.hub.transfer;

import java.net.InetSocketAddress;

public class ConnectionProvider {
    public enum STATUS {
        OP_READ, OP_WRITE, OP_SWITCH, OP_LOCK, OP_CLOSE
    }

    private InetSocketAddress localAddress;
    private InetSocketAddress remoteAddress;

    private STATUS currentStatus;
    private IPacket packet;

    public ConnectionProvider(STATUS status) {
        currentStatus = status;
    }

    public STATUS status() {
        return currentStatus;
    }

    public void setLocalAddress(InetSocketAddress address) {
        this.localAddress = address;
    }
    public InetSocketAddress getLocalAddress() {
        return localAddress;
    }
    public void setRemoteAddress(InetSocketAddress address) {
        this.remoteAddress = address;
    }

    public void setPacket(IPacket packet) {
        this.packet = packet;
    }
    public IPacket getPacket() {
        return packet;
    }
}
