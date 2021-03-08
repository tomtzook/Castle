package com.castle.net.messaging;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class MessageSerializer {

    public void serialize(DataOutput output, RawMessage message) throws IOException {
        MessageHeader header = message.getHeader();
        output.writeInt(header.getVersion());
        output.writeInt(header.getType());
        output.writeInt(header.getDataSize());

        output.write(message.getData());
    }

    public RawMessage deserialize(DataInput input) throws IOException {
        int version = input.readInt();
        int type = input.readInt();
        int dataSize = input.readInt();

        byte[] data = new byte[dataSize];
        input.readFully(data);

        MessageHeader header = new MessageHeader(version, type, dataSize);
        return new RawMessage(header, data);
    }
}
