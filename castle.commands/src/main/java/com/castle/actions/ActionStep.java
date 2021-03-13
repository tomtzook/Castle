package com.castle.actions;

import com.castle.scheduling.InnerStatus;
import com.castle.time.Clock;
import com.castle.time.Time;

public interface ActionStep<R> {

    ActionStep<R> execute();
    void onError();

    static <R> ActionStep<R> firstStep(Clock clock, Action<R> action, InnerControl<R> control,
                                       InnerStatus<R> status, Configuration configuration) {
        return new ConfigurationStep<>(clock, action, control, status, configuration);
    }

    static <R> ActionStep<R> endStep(Clock clock, Action<R> action, InnerControl<R> control,
                                     InnerStatus<R> status, Configuration configuration) {
        return new EndStep<>(clock, action, control, status, configuration);
    }

    abstract class StepBase<R> implements ActionStep<R> {

        protected final Clock mClock;
        protected final Action<R> mAction;
        protected final InnerControl<R> mControl;
        protected final InnerStatus<R> mStatus;
        protected final Configuration mConfiguration;

        public StepBase(Clock clock, Action<R> action, InnerControl<R> control, InnerStatus<R> status,
                        Configuration configuration) {
            mClock = clock;
            mAction = action;
            mControl = control;
            mStatus = status;
            mConfiguration = configuration;
        }

        public StepBase(StepBase<R> other) {
            this(other.mClock, other.mAction, other.mControl, other.mStatus, other.mConfiguration);
        }
    }

    class ConfigurationStep<R> extends StepBase<R> {

        public ConfigurationStep(Clock clock, Action<R> action, InnerControl<R> control, InnerStatus<R> status,
                                 Configuration configuration) {
            super(clock, action, control, status, configuration);
        }

        public ConfigurationStep(StepBase<R> other) {
            super(other);
        }

        @Override
        public ActionStep<R> execute() {
            mAction.configure(mConfiguration);
            return new InitializationStep<R>(this);
        }

        @Override
        public void onError() {
            new EndStep<>(this).execute();
        }
    }

    class InitializationStep<R> extends StepBase<R> {

        public InitializationStep(StepBase<R> other) {
            super(other);
        }

        @Override
        public ActionStep<R> execute() {
            mStatus.markStarted(mClock.currentTime());
            mAction.initialize(mControl);
            return new ExecutionStep<>(this);
        }

        @Override
        public void onError() {
            new EndStep<>(this).execute();
        }
    }

    class ExecutionStep<R> extends StepBase<R> {

        public ExecutionStep(StepBase<R> other) {
            super(other);
        }

        @Override
        public ActionStep<R> execute() {
            Time now = mClock.currentTime();
            Time timeout = mConfiguration.getTimeout();

            if (timeout.isValid() && now.sub(mStatus.getStartTime()).after(timeout)) {
                mControl.markInterrupted();
                mStatus.cancel();
                return new EndStep<>(this);
            }

            mAction.execute(mControl);
            return mControl.isFinished() ? new EndStep<>(this) : this;
        }

        @Override
        public void onError() {
            new EndStep<>(this).execute();
        }
    }

    class EndStep<R> extends StepBase<R> {

        public EndStep(Clock clock, Action<R> action, InnerControl<R> control, InnerStatus<R> status,
                       Configuration configuration) {
            super(clock, action, control, status, configuration);
        }

        public EndStep(StepBase<R> other) {
            super(other);
        }

        @Override
        public ActionStep<R> execute() {
            mAction.end(mControl);
            return null;
        }

        @Override
        public void onError() {
        }
    }
}
