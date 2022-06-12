package com.castle.io;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.HashMap;
import java.util.List;
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
        } else if (value instanceof Byte) {
            output.writeInt(SerializedType.BYTE.intValue());
            output.writeByte((Byte) value);
        } else if (value instanceof Short) {
            output.writeInt(SerializedType.SHORT.intValue());
            output.writeShort((Short) value);
        } else if (value instanceof Integer) {
            output.writeInt(SerializedType.INTEGER.intValue());
            output.writeInt((Integer) value);
        } else if (value instanceof Long) {
            output.writeInt(SerializedType.LONG.intValue());
            output.writeLong((Long) value);
        } else if (value instanceof Float) {
            output.writeInt(SerializedType.FLOAT.intValue());
            output.writeFloat((Float) value);
        } else if (value instanceof Double) {
            output.writeInt(SerializedType.DOUBLE.intValue());
            output.writeDouble((Double) value);
        } else if (value instanceof Character) {
            output.writeInt(SerializedType.CHAR.intValue());
            output.writeChar((Character) value);
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
        } else if (value.getClass().isArray()) {
            output.writeInt(SerializedType.ARRAY.intValue());
            int length = Array.getLength(value);
            output.writeInt(length);
            SerializedType innerType = SerializedType.fromJavaType(value.getClass().getComponentType());
            output.writeInt(innerType.intValue());

            for (int i = 0; i < length; i++) {
                writeTyped(output, Array.get(value, i));
            }
        } else if (value instanceof List) {
            //noinspection rawtypes
            writeTyped(output, ((List)value).toArray());
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
            case BYTE: return input.readByte();
            case SHORT: return input.readShort();
            case INTEGER: return input.readInt();
            case LONG: return input.readLong();
            case FLOAT: return input.readFloat();
            case DOUBLE: return input.readDouble();
            case CHAR: return input.readChar();
            case STRING: return input.readUTF();
            case BOOLEAN: return input.readBoolean();
            case BLOB: {
                int length = input.readInt();
                byte[] data = new byte[length];
                input.readFully(data);
                return data;
            }
            case ARRAY: {
                int length = input.readInt();
                int innerTypeInt = input.readInt();
                SerializedType innerType = SerializedType.fromInt(innerTypeInt);

                Object arr = Array.newInstance(innerType.javaType(), length);
                for (int i = 0; i < length; i++) {
                    Object value = readTyped(input);
                    Array.set(arr, i, value);
                }

                return arr;
            }
            default: throw new IOException("unsupported type: " + type.name());
        }
    }
}
