package com.application.sawai.gallery.jobs;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.SystemClock;
import android.util.Log;

import com.application.sawai.gallery.UtilClass;
import com.application.sawai.gallery.box.DropBoxUpload;
import com.application.sawai.gallery.db.DBHelper;

public class UploadJobService extends JobService {
    private static final String TAG = "SyncService";


    @Override
    public boolean onStartJob(JobParameters params) {
        Log.i(TAG, "starting a new job " + params.getJobId());
        SharedPreferences settings = getSharedPreferences(UtilClass.PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString("userId", SystemClock.currentThreadTimeMillis()+"");

        new JobTask(this).execute(params);
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        Log.i(TAG, "on stop job: " + params.getJobId());
        return true;
    }

    private class JobTask extends AsyncTask<JobParameters, Void, JobParameters> {
        private final JobService jobService;

        public JobTask(JobService jobService) {
            this.jobService = jobService;
        }

        @Override
        protected JobParameters doInBackground(JobParameters... params) {
            DropBoxUpload dropBoxUpload = new DropBoxUpload();
            DBHelper dbHelper = new DBHelper(getApplicationContext());
            SharedPreferences sharedpreferences = getSharedPreferences(UtilClass.PREFS_NAME, Context.MODE_PRIVATE);
            try {
                dropBoxUpload.setDbHelper(dbHelper);
                dropBoxUpload.setSharedpreferences(sharedpreferences);
                dropBoxUpload.uploadFiles();
            }
            catch (Exception e) {
                Log.e(TAG,e.getMessage(), e);
            }
            return params[0];
        }

        @Override
        protected void onPostExecute(JobParameters jobParameters) {
            jobService.jobFinished(jobParameters, true);
        }
    }

} 