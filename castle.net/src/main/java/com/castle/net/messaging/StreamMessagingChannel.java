package com.castle.net.messaging;

import com.castle.net.StreamConnection;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class StreamMessagingChannel implements MessagingChannel {

    private final DataOutputStream mOutputStream;
    private final DataInputStream mInputStream;
    private final MessageSerializer mSerializer;

    public StreamMessagingChannel(DataOutputStream outputStream, DataInputStream inputStream,
                                  MessageSerializer serializer) {
        mOutputStream = outputStream;
        mInputStream = inputStream;
        mSerializer = serializer;
    }

    public StreamMessagingChannel(DataOutputStream outputStream, DataInputStream inputStream) {
        this(outputStream, inputStream, new MessageSerializer());
    }

    public StreamMessagingChannel(StreamConnection connection) throws IOException {
        this(new DataOutputStream(connection.outputStream()), new DataInputStream(connection.inputStream()));
    }

    @Override
    public void send(RawMessage message) throws IOException {
        mSerializer.serialize(mOutputStream, message);
    }

    @Override
    public RawMessage read() throws IOException {
        return mSerializer.deserialize(mInputStream);
    }
}
