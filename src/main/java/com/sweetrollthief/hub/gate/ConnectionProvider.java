package com.sweetrollthief.hub.gate;

import java.nio.ByteBuffer;

import com.sweetrollthief.hub.router.IPacket;

public class ConnectionProvider {
    enum STATUS {
        OP_READ, OP_WRITE, OP_SWITCH, OP_LOCK, OP_CLOSE
    }

    private STATUS currentStatus;
    private IPacket packet;

    public ConnectionProvider(STATUS status) {
        currentStatus = status;
    }

    public STATUS status() {
        return currentStatus;
    }
}
