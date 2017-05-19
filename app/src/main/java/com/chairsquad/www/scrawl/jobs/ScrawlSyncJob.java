package com.chairsquad.www.scrawl.jobs;

import android.content.Context;
import android.support.annotation.NonNull;

import com.chairsquad.www.scrawl.sync.ScrawlNotesSyncTask;
import com.chairsquad.www.scrawl.utilities.PreferenceUtils;
import com.chairsquad.www.scrawl.utilities.ScrawlConnection;
import com.evernote.android.job.Job;
import com.evernote.android.job.JobRequest;

import java.util.concurrent.TimeUnit;

/**
 * Created by henry on 18/05/17.
 */

public class ScrawlSyncJob extends Job {

    public static final String TAG="scawl_sync_job";

    private static final long EXECUTE_TIME = TimeUnit.SECONDS.toMillis(10);
    private static final long FLEX_TIME = TimeUnit.SECONDS.toMillis(10);

    @NonNull
    @Override
    protected Job.Result onRunJob(Params params) {
        Context context = getContext();
        if (ScrawlConnection.isLoggedIn(context)) {
            ScrawlNotesSyncTask.execute(context);
            try {
                return Result.SUCCESS;
            } finally {
                schedule(context);
            }
        }
        schedule(context);
        return Result.RESCHEDULE;
    }

    public static void schedule(Context context) {
        JobRequest.Builder builder = new JobRequest.Builder(TAG)
                .setExecutionWindow(EXECUTE_TIME, FLEX_TIME)
                .setBackoffCriteria(FLEX_TIME, JobRequest.BackoffPolicy.EXPONENTIAL)
                .setRequirementsEnforced(true)
                .setPersisted(true)
                .setUpdateCurrent(true);


        if (PreferenceUtils.getWifiOnly(context)) {
            builder.setRequiredNetworkType(JobRequest.NetworkType.UNMETERED);
        }

        builder.build().schedule();
    }

}
