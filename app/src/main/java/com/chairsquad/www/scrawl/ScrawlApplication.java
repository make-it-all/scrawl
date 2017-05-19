package com.chairsquad.www.scrawl;

import android.app.Application;

import com.chairsquad.www.scrawl.jobs.ScrawlJobCreator;
import com.evernote.android.job.JobManager;

/**
 * Created by henry on 18/05/17.
 */

public class ScrawlApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        JobManager.create(this).addJobCreator(new ScrawlJobCreator());
    }


}
