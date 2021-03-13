package com.castle.actions;

import com.castle.time.Time;

public class ConfigurationImpl implements Configuration {

    private Time mTimeout;

    public ConfigurationImpl() {
        mTimeout = Time.INVALID;
    }

    @Override
    public Time getTimeout() {
        return mTimeout;
    }

    @Override
    public void setTimeout(Time timeout) {
        mTimeout = timeout;
    }
}
