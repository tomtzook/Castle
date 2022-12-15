package com.castle.net.messaging.v1;

import com.castle.concurrent.service.TerminalServiceBase;
import com.castle.exceptions.ServiceException;
import com.castle.net.messaging.Message;
import com.castle.net.messaging.MessageHandler;
import com.castle.net.messaging.MessageListener;
import com.castle.net.messaging.MessageQueue;
import com.castle.net.messaging.MessagingChannel;
import com.notifier.EventController;

import java.util.concurrent.BlockingQueue;

public class MessagingService extends TerminalServiceBase implements MessageQueue, MessageHandler {

    private final MessagingChannel mChannel;
    private final EventController mEventController;
    private final BlockingQueue<Message> mOutgoingMessageQueue;

    private Thread mWriteThread;
    private Thread mReadThread;

    private MessagingService(MessagingChannel channel,
                            EventController eventController,
                            BlockingQueue<Message> outgoingMessageQueue) {
        mChannel = channel;
        mEventController = eventController;
        mOutgoingMessageQueue = outgoingMessageQueue;
    }

    @Override
    protected void startRunning() throws ServiceException {
        mWriteThread = new Thread(
                new MessageWriteTask(mChannel, mOutgoingMessageQueue),
                "MessagingService-Write"
        );
        mWriteThread.setDaemon(true);

        mReadThread = new Thread(
            new MessageReadTask(mChannel, mEventController),
            "MessagingService-Read"
        );
        mReadThread.setDaemon(true);

        mWriteThread.start();
        mReadThread.start();
    }

    @Override
    protected void stopRunning() {
        mWriteThread.interrupt();
        mWriteThread = null;

        mReadThread.interrupt();
        mReadThread = null;
    }

    @Override
    public void add(Message message) {
        mOutgoingMessageQueue.add(message);
    }

    @Override
    public void addListener(MessageListener listener) {
        mEventController.registerListener(listener);
    }
}
