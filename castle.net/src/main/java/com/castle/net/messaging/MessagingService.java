package com.castle.net.messaging;

import com.castle.concurrent.service.TaskService;
import com.castle.io.streams.IoStreams;
import com.castle.time.exceptions.TimeoutException;

import java.io.IOException;
import java.io.InputStream;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;

public class MessagingService extends TaskService implements MessageQueue {

    private final Queue<Message> mMessageQueue;

    MessagingService(ExecutorService executorService,
                     ChannelOpener opener,
                     Queue<Message> messageQueue,
                     MessageHandler<? super Message> messageHandler,
                     MessageParser messageParser) {
        super(executorService, new Task(opener, messageQueue, messageHandler, messageParser));
        mMessageQueue = messageQueue;
    }

    public MessagingService(ExecutorService executorService,
                            ChannelOpener opener,
                            MessageParser messageParser) {
        this(executorService, opener,
                new ConcurrentLinkedQueue<>(), null,
                messageParser);
    }

    @Override
    public void queue(Message message) throws IOException {
        mMessageQueue.add(message);
    }

    private static class Task implements Runnable {

        private final ChannelOpener mOpener;
        private final Queue<Message> mMessageQueue;
        private final MessageHandler<? super Message> mMessageHandler;
        private final MessageParser mMessageParser;

        private Task(ChannelOpener opener,
                     Queue<Message> messageQueue,
                     MessageHandler<? super Message> messageHandler,
                     MessageParser messageParser) {
            mOpener = opener;
            mMessageQueue = messageQueue;
            mMessageHandler = messageHandler;
            mMessageParser = messageParser;
        }

        @Override
        public void run() {
            try {
                while (!Thread.interrupted()) {
                    readMessages();
                    writeMessages();

                    Thread.sleep(500);
                }
            } catch (InterruptedException e) {
            }
        }

        private void readMessages() {
            try (MessagingChannel channel = mOpener.open()) {
                while (!Thread.interrupted()) {
                    RawMessage rawMessage = channel.read();
                    Message message = mMessageParser.parse(rawMessage);
                    mMessageHandler.handle(message);
                }
            } catch (IOException | TimeoutException e) {

            }
        }

        private void writeMessages() {
            try (MessagingChannel channel = mOpener.open()) {
                while (!mMessageQueue.isEmpty() && !Thread.interrupted()) {
                    Message message = mMessageQueue.remove();
                    if (message == null) {
                        return;
                    }

                    RawMessage rawMessage = toRaw(message);
                    channel.write(rawMessage);
                }
            } catch (IOException e) {

            }
        }

        private RawMessage toRaw(Message message) throws IOException {
            try (InputStream inputStream = message.openContent()) {
                byte[] data = IoStreams.readAll(inputStream);

                MessageHeader header = new MessageHeader(
                        0, message.type().key(), data.length);
                return new RawMessage(header, data);
            }
        }
    }
}
