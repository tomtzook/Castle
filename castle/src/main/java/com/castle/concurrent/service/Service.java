package com.castle.concurrent.service;

import com.castle.exceptions.ServiceException;

public interface Service {

    void start() throws ServiceException;
    void stop();

    boolean isRunning();
}
