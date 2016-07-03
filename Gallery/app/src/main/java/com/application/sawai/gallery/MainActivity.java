package com.application.sawai.gallery;

import android.Manifest;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;

import com.application.sawai.gallery.enums.Type;
import com.application.sawai.gallery.jobs.UploadJobService;

public class MainActivity extends AppCompatActivity {

    private static final int INT = 10000;
    private int kJobId = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        checkAndgrantPermission();
        scheduleJob();
        setContentView(R.layout.activity_main);
    }

    private void scheduleJob() {
        ComponentName mServiceComponent = new ComponentName(this, UploadJobService.class);
        JobInfo.Builder builder = new JobInfo.Builder(kJobId++,mServiceComponent);
        builder.setRequiredNetworkType(JobInfo.NETWORK_TYPE_UNMETERED);
        builder.setPeriodic(UtilClass.getMiliseconds(10 , Type.SECONDS));
        builder.setRequiresDeviceIdle(true);
        builder.setPersisted(true);
        JobScheduler jobScheduler =
                (JobScheduler) getApplication().getSystemService(Context.JOB_SCHEDULER_SERVICE);
        jobScheduler.schedule(builder.build());

    }

    private void checkAndgrantPermission() {

        int permissionCheck = ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE);
        if(permissionCheck != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    INT);
        }

         permissionCheck = ContextCompat.checkSelfPermission(this,
                Manifest.permission.RECEIVE_BOOT_COMPLETED);
        if(permissionCheck != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.RECEIVE_BOOT_COMPLETED},
                    INT);
        }
    }


}
