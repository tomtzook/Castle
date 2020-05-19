package com.castle.concurrent.service;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

class SingleUseServiceTest {

    @Test
    public void start_notRunning_callsStartRunning() throws Exception {
        SingleUseService singleUseService = spy(SingleUseService.class);
        singleUseService.start();

        verify(singleUseService, times(1)).startRunning();
        assertTrue(singleUseService.isRunning());
    }

    @Test
    public void start_isRunning_throwsIllegalStateException() throws Exception {
        SingleUseService singleUseService = spy(SingleUseService.class);
        singleUseService.start();

        assertThrows(IllegalStateException.class, singleUseService::start);
    }

    @Test
    public void start_wasStopped_throwsIllegalStateException() throws Exception {
        SingleUseService singleUseService = spy(SingleUseService.class);
        singleUseService.start();
        singleUseService.stop();

        assertThrows(IllegalStateException.class, singleUseService::start);
    }

    @Test
    public void stop_isRunning_stopsRunning() throws Exception {
        SingleUseService singleUseService = spy(SingleUseService.class);
        singleUseService.start();

        singleUseService.stop();

        verify(singleUseService, times(1)).stopRunning();
        assertFalse(singleUseService.isRunning());
    }

    @Test
    public void stop_notRunning_throwsIllegalStateException() throws Exception {
        SingleUseService singleUseService = spy(SingleUseService.class);

        assertThrows(IllegalStateException.class, singleUseService::stop);
    }

    @Test
    public void stop_isRunning_nowTerminated() throws Exception {
        SingleUseService singleUseService = spy(SingleUseService.class);
        singleUseService.start();

        singleUseService.stop();

        assertTrue(singleUseService.isTerminated());
    }
}