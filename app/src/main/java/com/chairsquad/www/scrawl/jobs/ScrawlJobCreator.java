package com.chairsquad.www.scrawl.jobs;

import com.evernote.android.job.Job;
import com.evernote.android.job.JobCreator;

/**
 * Created by henry on 18/05/17.
 */

public class ScrawlJobCreator implements JobCreator {

    @Override
    public Job create(String tag) {
        switch (tag) {
            case ScrawlSyncJob.TAG:
                return new ScrawlSyncJob();
        }
        return null;
    }
}
