package org.onebrick.android.jobs;

import com.path.android.jobqueue.Job;
import com.path.android.jobqueue.Params;

public abstract class OneBrickBaseJob extends Job {

    private static final int DEFAULT_RETRY_LIMIT = 2;

    public OneBrickBaseJob(Params params) {
        super(params);
    }

    @Override
    public void onAdded() {

    }

    @Override
    protected void onCancel() {

    }

    @Override
    protected boolean shouldReRunOnThrowable(Throwable throwable) {
        return true;
    }

    @Override
    protected int getRetryLimit() {
        return DEFAULT_RETRY_LIMIT;
    }
}
