package com.castle.actions;

import com.castle.scheduling.ExecutionContext;
import com.castle.scheduling.InnerStatus;
import com.castle.time.Clock;
import com.castle.util.dependencies.DependencyContainer;

public class ActionContext<R> implements ExecutionContext {

    private final Clock mClock;
    private final Action<R> mAction;
    private final InnerStatus<R> mStatus;
    private final Configuration mConfiguration;

    private final InnerControl<R> mControl;
    private ActionStep<R> mCurrentStep;

    public ActionContext(Clock clock, Action<R> action, InnerStatus<R> status, Configuration configuration) {
        mClock = clock;
        mAction = action;
        mStatus = status;
        mConfiguration = configuration;

        mControl = new ControlImpl<>(status);
        mCurrentStep = ActionStep.firstStep(mClock, mAction, mControl, mStatus, mConfiguration);
    }

    @Override
    public boolean execute(DependencyContainer dependencyContainer) {
        if (mStatus.isCanceled()) {
            onCancel();
            return true;
        }

        try {
            mCurrentStep = mCurrentStep.execute();
            if (mCurrentStep == null) {
                if (mControl.isFinished()) {
                    mStatus.markFinished(mControl.getResult());
                }

                return true;
            }
        } catch (Throwable t) {
            mStatus.markErrored(t);
            return false;
        }

        return false;
    }

    private void onCancel() {
        mControl.markInterrupted();
        mCurrentStep = ActionStep.endStep(mClock, mAction, mControl, mStatus, mConfiguration);

        try {
            mCurrentStep.execute();
        } catch (Throwable t) {
            mStatus.markErrored(t);
        }
    }
}
