package com.castle.io.streams;

import com.castle.util.throwables.ThrowableHandler;
import com.castle.util.throwables.Throwables;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.function.BiConsumer;

public class IoStreams {

    private static final int BUFFER_SIZE = 1024;

    private IoStreams() {}

    public static void copy(InputStream source, OutputStream destination) throws IOException {
        byte[] buffer = new byte[BUFFER_SIZE];

        int readBytes;
        while ((readBytes = source.read(buffer)) > 0) {
            destination.write(buffer, 0, readBytes);
        }
    }

    public static BiConsumer<InputStream, OutputStream> copier(ThrowableHandler throwableHandler) {
        return (inputStream, outputStream) ->  {
            try {
                copy(inputStream, outputStream);
            } catch (Throwable t) {
                throwableHandler.handle(t);
            }
        };
    }

    public static BiConsumer<InputStream, OutputStream> silentCopier() {
        return copier(Throwables.silentHandler());
    }

    public static byte[] readAll(InputStream source) throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        try {
            copy(source, buffer);
        } finally {
            buffer.close();
        }

        return buffer.toByteArray();
    }
}
