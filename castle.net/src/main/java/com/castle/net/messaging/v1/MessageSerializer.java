package com.castle.net.messaging.v1;

import com.castle.net.messaging.Message;
import com.castle.net.messaging.MessageType;
import com.castle.net.messaging.RawMessage;
import com.castle.net.messaging.data.KnownMessageTypes;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class MessageSerializer {

    private final KnownMessageTypes mMessageTypes;

    public MessageSerializer(KnownMessageTypes messageTypes) {
        mMessageTypes = messageTypes;
    }

    public void write(DataOutput output, Message message) throws IOException {
        RawMessage rawMessage = message.getType().toRaw(message);
        int typeKey = rawMessage.getType().getKey();
        byte[] content = rawMessage.getContent();

        MessageHeader header = new MessageHeader(typeKey, content.length);
        header.writeTo(output);
        output.write(content);
    }

    public Message read(DataInput dataInput) throws IOException {
        MessageHeader header = new MessageHeader(dataInput);
        byte[] content = new byte[header.getContentSize()];
        dataInput.readFully(content);

        MessageType type = mMessageTypes.get(header.getMessageType());
        RawMessage rawMessage = new RawMessage(type, content);
        return type.fromRaw(rawMessage);
    }
}
