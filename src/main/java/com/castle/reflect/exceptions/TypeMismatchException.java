package com.castle.reflect.exceptions;

public class TypeMismatchException extends RuntimeException {

    private final Class<?> mWantedType;
    private final Class<?> mActualType;

    public TypeMismatchException(Class<?> wantedType, Class<?> actualType, Throwable cause) {
        super(cause);
        mWantedType = wantedType;
        mActualType = actualType;
    }

    public TypeMismatchException(Class<?> wantedType, Class<?> actualType, String message) {
        super(message);
        mWantedType = wantedType;
        mActualType = actualType;
    }

    public TypeMismatchException(Class<?> wantedType, Class<?> actualType, String message, Throwable cause) {
        super(message, cause);
        mWantedType = wantedType;
        mActualType = actualType;
    }

    public TypeMismatchException(Class<?> wantedType, Class<?> actualType) {
        this(wantedType, actualType, String.format("Mismatch: wanted %s, actual %s",
                wantedType.getName(),
                actualType == null ?  "NullType" : actualType.getName()));
    }

    public TypeMismatchException(Class<?> wantedType) {
        this(wantedType, null);
    }

    public Class<?> getWantedType() {
        return mWantedType;
    }

    public Class<?> getActualType() {
        return mActualType;
    }
}
