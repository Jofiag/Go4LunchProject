package com.example.go4lunchproject777;

import static android.os.Build.VERSION_CODES.LOLLIPOP;
import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertFalse;
import static junit.framework.TestCase.assertTrue;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;

import androidx.test.runner.AndroidJUnit4;

import com.example.go4lunchproject777.data.googleplace.RestaurantListManager;
import com.example.go4lunchproject777.services.MyJobService;
import com.example.go4lunchproject777.util.Constants;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;
@RunWith(AndroidJUnit4.class)
@Config(minSdk = LOLLIPOP)


public class MyServiceUnitTest {
    private JobInfo restaurantListJobInfo;
    private JobScheduler jobScheduler;

    @Before
    public void setUp() {
        Context context = RuntimeEnvironment.getApplication();
        jobScheduler = new RestaurantListManager(context).getJobScheduler();
//        jobScheduler = (JobScheduler) context.getSystemService(Context.JOB_SCHEDULER_SERVICE);

        restaurantListJobInfo = new JobInfo.Builder(Constants.JOB_ID, new ComponentName(context.getApplicationContext(), MyJobService.class))
                .setRequiredNetworkType(JobInfo.NETWORK_TYPE_UNMETERED) //The action will stop if there is no more internet
                .setMinimumLatency(1)
                .build();
    }

    @After
    public void tearDown() {
        jobScheduler.cancelAll();
        restaurantListJobInfo = null;
    }

    @Test
    public void schedulingJob(){
        int result = jobScheduler.schedule(restaurantListJobInfo);
        assertFalse(jobScheduler.getAllPendingJobs().isEmpty());

        assertTrue(jobScheduler.getAllPendingJobs().contains(restaurantListJobInfo));
        assertEquals(result, JobScheduler.RESULT_SUCCESS);
    }

    @Test
    public void cancellingJob(){
        jobScheduler.schedule(restaurantListJobInfo);
        assertFalse(jobScheduler.getAllPendingJobs().isEmpty());

        jobScheduler.cancel(Constants.JOB_ID);
        assertFalse(jobScheduler.getAllPendingJobs().contains(restaurantListJobInfo));
    }

}
