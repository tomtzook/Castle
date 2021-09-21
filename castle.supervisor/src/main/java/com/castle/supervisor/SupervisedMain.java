package com.castle.supervisor;

import java.util.function.Consumer;

public class SupervisedMain {

    public static void start(Consumer<Supervisor> main) {
        SupervisorControl supervisor = new SupervisorImpl();
        supervisor.onStart();
        try {
            main.accept(supervisor);
        } finally {
            supervisor.onShutdown();
        }
    }
}
