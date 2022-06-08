package com.castle.io;

public enum SerializedType {
    NULL(0),
    INTEGER(1),
    DOUBLE(2),
    STRING(3),
    BOOLEAN(4),
    BLOB(5)
    ;

    private final int mIntValue;

    SerializedType(int intValue) {
        mIntValue = intValue;
    }

    public int intValue() {
        return mIntValue;
    }

    public static SerializedType fromInt(int typeInt) {
        for (SerializedType type : values()) {
            if (type.intValue() == typeInt) {
                return type;
            }
        }

        throw new EnumConstantNotPresentException(SerializedType.class, String.valueOf(typeInt));
    }
}
