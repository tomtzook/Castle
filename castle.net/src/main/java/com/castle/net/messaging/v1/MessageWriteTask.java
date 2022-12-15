package com.castle.net.messaging.v1;

import com.castle.net.messaging.Message;
import com.castle.net.messaging.MessagingChannel;

import java.io.IOException;
import java.util.concurrent.BlockingQueue;

public class MessageWriteTask implements Runnable {

    private final MessagingChannel mChannel;
    private final BlockingQueue<Message> mMessageQueue;

    public MessageWriteTask(MessagingChannel channel, BlockingQueue<Message> messageQueue) {
        mChannel = channel;
        mMessageQueue = messageQueue;
    }

    @Override
    public void run() {
        while (true) {
            try {
                Message message = mMessageQueue.take();
                mChannel.write(message);
            } catch (IOException e) {
                // TODO: HANDLE
            } catch (InterruptedException e) {
                break;
            }
        }
    }
}
