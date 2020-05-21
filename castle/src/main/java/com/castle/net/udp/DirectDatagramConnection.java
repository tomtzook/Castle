package com.castle.net.udp;

import com.castle.net.PacketConnection;
import com.castle.time.exceptions.TimeoutException;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketAddress;
import java.net.SocketTimeoutException;

public class DirectDatagramConnection implements PacketConnection {

    private final DatagramSocket mDatagramSocket;
    private final SocketAddress mDestination;

    public DirectDatagramConnection(DatagramSocket datagramSocket, SocketAddress destination) {
        mDatagramSocket = datagramSocket;
        mDestination = destination;
    }

    @Override
    public void write(byte[] bytes, int start, int length) throws IOException {
        mDatagramSocket.send(new DatagramPacket(bytes, start, length, mDestination));
    }

    @Override
    public void write(byte[] bytes) throws IOException {
        write(bytes, 0, bytes.length);
    }

    @Override
    public int readInto(byte[] buffer) throws IOException, TimeoutException {
        try {
            DatagramPacket datagramPacket = new DatagramPacket(buffer, 0, buffer.length);
            mDatagramSocket.receive(datagramPacket);

            return datagramPacket.getLength();
        } catch (SocketTimeoutException e) {
            throw new TimeoutException(e);
        }
    }

    @Override
    public byte[] read(int amount) throws IOException, TimeoutException {
        byte[] buffer = new byte[amount];
        readInto(buffer);
        return buffer;
    }

    @Override
    public void close() throws IOException {
        mDatagramSocket.close();
    }
}
