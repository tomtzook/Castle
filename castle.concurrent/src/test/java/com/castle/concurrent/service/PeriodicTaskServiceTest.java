package com.castle.concurrent.service;

import com.castle.time.Time;
import org.junit.jupiter.api.Test;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class PeriodicTaskServiceTest {

    @Test
    public void start_notRunning_callsExecutor() throws Exception {
        ScheduledExecutorService executorService = mock(ScheduledExecutorService.class);
        Runnable task = mock(Runnable.class);
        Time time = Time.milliseconds(12);

        PeriodicTaskService periodicTaskService = new PeriodicTaskService(executorService, task, () -> time);
        periodicTaskService.start();

        verify(executorService, times(1))
                .scheduleAtFixedRate(eq(task), eq(time.value()), eq(time.value()), eq(time.unit()));
    }

    @Test
    public void isRunning_afterStart_returnsTrue() throws Exception {
        ScheduledExecutorService executorService = mock(ScheduledExecutorService.class);
        when(executorService.scheduleAtFixedRate(any(Runnable.class), anyLong(), anyLong(), any(TimeUnit.class))).thenReturn(mock(ScheduledFuture.class));
        Runnable task = mock(Runnable.class);
        Time time = Time.milliseconds(12);

        PeriodicTaskService periodicTaskService = new PeriodicTaskService(executorService, task, () -> time);
        periodicTaskService.start();

        assertTrue(periodicTaskService.isRunning());
    }

    @Test
    public void isRunning_afterStop_returnsFalse() throws Exception {
        ScheduledExecutorService executorService = mock(ScheduledExecutorService.class);
        when(executorService.scheduleAtFixedRate(any(Runnable.class), anyLong(), anyLong(), any(TimeUnit.class))).thenReturn(mock(ScheduledFuture.class));
        Runnable task = mock(Runnable.class);
        Time time = Time.milliseconds(12);

        PeriodicTaskService periodicTaskService = new PeriodicTaskService(executorService, task, () -> time);
        periodicTaskService.start();
        periodicTaskService.stop();

        assertFalse(periodicTaskService.isRunning());
    }

    @Test
    public void stop_isRunning_cancelsFuture() throws Exception {
        ScheduledExecutorService executorService = mock(ScheduledExecutorService.class);
        ScheduledFuture future = mock(ScheduledFuture.class);
        when(executorService.scheduleAtFixedRate(any(Runnable.class), anyLong(), anyLong(), any(TimeUnit.class))).thenReturn(future);
        Runnable task = mock(Runnable.class);
        Time time = Time.milliseconds(12);

        PeriodicTaskService periodicTaskService = new PeriodicTaskService(executorService, task, () -> time);
        periodicTaskService.start();
        periodicTaskService.stop();

        verify(future, times(1)).cancel(eq(true));
    }
}