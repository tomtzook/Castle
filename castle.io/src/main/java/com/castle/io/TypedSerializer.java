package com.castle.io;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class TypedSerializer {

    public void writeTypedMap(DataOutput output, Map<String, Object> map) throws IOException {
        output.writeInt(map.size());

        for (Map.Entry<String, Object> entry : map.entrySet()) {
            output.writeUTF(entry.getKey());
            writeTyped(output, entry.getValue());
        }
    }

    public void writeTyped(DataOutput output, Object value) throws IOException {
        if (value == null) {
            output.writeInt(SerializedType.NULL.intValue());
        } else if (value instanceof Integer) {
            output.writeInt(SerializedType.INTEGER.intValue());
            output.writeInt((Integer) value);
        } else if (value instanceof Double) {
            output.writeInt(SerializedType.DOUBLE.intValue());
            output.writeDouble((Double) value);
        } else if (value instanceof String) {
            output.writeInt(SerializedType.STRING.intValue());
            output.writeUTF((String) value);
        } else if (value instanceof Boolean) {
            output.writeInt(SerializedType.BOOLEAN.intValue());
            output.writeBoolean((Boolean) value);
        } else if (value instanceof byte[]) {
            output.writeInt(SerializedType.BLOB.intValue());
            output.writeInt(((byte[])value).length);
            output.write((byte[]) value);
        } else {
            throw new IOException("unsupported type: " + value.getClass().getName());
        }
    }

    public Map<String, Object> readTypedMap(DataInput input) throws IOException {
        Map<String, Object> map = new HashMap<>();
        int size = input.readInt();
        for (int i = 0; i < size; i++) {
            String name = input.readUTF();
            Object value = readTyped(input);

            map.put(name, value);
        }

        return map;
    }

    public Object readTyped(DataInput input) throws IOException {
        int typeInt = input.readInt();
        SerializedType type = SerializedType.fromInt(typeInt);

        switch (type) {
            case NULL: return null;
            case INTEGER: return input.readInt();
            case DOUBLE: return input.readDouble();
            case STRING: return input.readUTF();
            case BOOLEAN: return input.readBoolean();
            case BLOB: {
                int length = input.readInt();
                byte[] data = new byte[length];
                input.readFully(data);
                return data;
            }
            default: throw new IOException("unsupported type: " + type.name());
        }
    }
}
