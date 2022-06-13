package com.castle.net.tcp;

import com.castle.annotations.NotThreadSafe;
import com.castle.net.StreamConnection;
import com.castle.net.messaging.MessageSerializer;
import com.castle.net.messaging.MessagingChannel;
import com.castle.net.messaging.RawMessage;
import com.castle.time.exceptions.TimeoutException;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

@NotThreadSafe
public class TcpSocketConnection implements StreamConnection, MessagingChannel {

    private final Socket mSocket;
    private final MessageSerializer mSerializer;

    public TcpSocketConnection(Socket socket) {
        mSocket = socket;
        mSerializer = new MessageSerializer();
    }

    @Override
    public InputStream inputStream() throws IOException {
        return mSocket.getInputStream();
    }

    @Override
    public OutputStream outputStream() throws IOException {
        return mSocket.getOutputStream();
    }

    @Override
    public void close() throws IOException {
        mSocket.close();
    }

    @Override
    public void write(RawMessage message) throws IOException {
        DataOutputStream outputStream = new DataOutputStream(outputStream());
        mSerializer.serialize(outputStream, message);
    }

    @Override
    public RawMessage read() throws IOException, TimeoutException {
        DataInputStream inputStream = new DataInputStream(inputStream());
        return mSerializer.deserialize(inputStream);
    }
}
