package com.castle.concurrent.service;

public abstract class SingleUseService extends TerminalService {

    @Override
    public synchronized void stop() {
        super.stop();
        markTerminated();
    }
}
