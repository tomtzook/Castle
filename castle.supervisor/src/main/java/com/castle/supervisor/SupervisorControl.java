package com.castle.supervisor;

public interface SupervisorControl extends Supervisor {

    void onStart();
    void onShutdown();
}
