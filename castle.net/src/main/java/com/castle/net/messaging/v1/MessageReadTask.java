package com.castle.net.messaging.v1;

import com.castle.net.messaging.Message;
import com.castle.net.messaging.MessageListener;
import com.castle.net.messaging.MessagingChannel;
import com.castle.net.messaging.NewMessageEvent;
import com.castle.time.exceptions.TimeoutException;
import com.notifier.EventController;

import java.io.IOException;

public class MessageReadTask implements Runnable {

    private final MessagingChannel mChannel;
    private final EventController mEventController;

    public MessageReadTask(MessagingChannel channel, EventController eventController) {
        mChannel = channel;
        mEventController = eventController;
    }

    @Override
    public void run() {
        while (true) {
            try {
                Message message = mChannel.read();
                mEventController.fire(
                        new NewMessageEvent(message),
                        NewMessageEvent.class,
                        MessageListener.class,
                        MessageListener::onNewMessage);
            } catch (IOException | TimeoutException e) {
                // TODO: HANDLE
            } catch (InterruptedException e) {
                break;
            }
        }
    }
}
