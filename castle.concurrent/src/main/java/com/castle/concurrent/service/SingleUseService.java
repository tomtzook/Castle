package com.castle.concurrent.service;

import com.castle.annotations.ThreadSafe;

@ThreadSafe
public abstract class SingleUseService extends TerminalServiceBase {

    @Override
    public synchronized void stop() {
        super.stop();
        super.close();
    }
}
