package com.castle.io;

public enum SerializedType {
    NULL(0, null),
    BYTE(1, Byte.class),
    SHORT(2, Short.class),
    INTEGER(3, Integer.class),
    LONG(4, Long.class),
    FLOAT(5, Float.class),
    DOUBLE(6, Double.class),
    CHAR(7, Character.class),
    STRING(8, String.class),
    BOOLEAN(9, Boolean.class),
    BLOB(10, byte[].class),
    ARRAY(11, null),
    OBJECT(12, Object.class)
    ;

    private final int mIntValue;
    private final Class<?> mJavaType;

    SerializedType(int intValue, Class<?> javaType) {
        mIntValue = intValue;
        mJavaType = javaType;
    }

    public int intValue() {
        return mIntValue;
    }

    public Class<?> javaType() {
        return mJavaType;
    }

    public static SerializedType fromInt(int typeInt) {
        for (SerializedType type : values()) {
            if (type.intValue() == typeInt) {
                return type;
            }
        }

        throw new EnumConstantNotPresentException(SerializedType.class, String.valueOf(typeInt));
    }

    public static SerializedType fromJavaType(Class<?> javaType) {
        for (SerializedType type : values()) {
            if (type.javaType() != null && type.javaType().isAssignableFrom(javaType)) {
                return type;
            }
        }

        throw new EnumConstantNotPresentException(SerializedType.class, javaType.getName());
    }
}
