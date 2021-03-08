package com.castle.net.messaging;

import com.castle.concurrent.service.TaskService;

import java.util.concurrent.ExecutorService;

public class MessagingService extends TaskService {

    public MessagingService(ExecutorService executorService, Runnable task) {
        super(executorService, task);
    }
}
