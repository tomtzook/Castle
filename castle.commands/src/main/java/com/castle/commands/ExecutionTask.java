package com.castle.commands;

import java.util.concurrent.BlockingQueue;

public class ExecutionTask implements Runnable {

    private final CommandExecutor mCommandExecutor;
    private final BlockingQueue<CommandContext<?>> mQueue;

    public ExecutionTask(CommandExecutor commandExecutor, BlockingQueue<CommandContext<?>> queue) {
        mCommandExecutor = commandExecutor;
        mQueue = queue;
    }

    @Override
    public void run() {
        while (!Thread.interrupted()) {
            try {
                CommandContext<?> context = mQueue.take();
                ExecutionResult result = mCommandExecutor.execute(context);

                switch (result) {
                    case DONE: break;
                    case REDO:
                        mQueue.add(context);
                        break;
                    default: throw new AssertionError("unexpected ExecutionResult " + result);
                }
            } catch (InterruptedException e) {
                break;
            }
        }
    }
}
