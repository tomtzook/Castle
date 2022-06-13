package com.castle.net.messaging;

public interface MessageHandler<T extends Message> {

    void handle(T message);
}
