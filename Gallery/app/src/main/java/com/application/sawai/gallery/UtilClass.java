package com.application.sawai.gallery;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;

import com.application.sawai.gallery.enums.Type;
import com.application.sawai.gallery.jobs.UploadJobService;

import java.io.File;
import java.util.Date;

/**
 * Created by SAWAI on 6/27/2016.
 */
public class UtilClass {

    public static final String TAG = "UtilClass";
    public static final String PREFS_NAME = "pref";
    public static final String USER_ID = "user_id";
    private static  String userId = null;
    private static final String[] okFileExtensions =  new String[] {"jpg", "png", "gif","jpeg", "bmp", "img", "pcd"};
    private static int kJobId = 0;


    public static boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            return true;
        }
        return false;
    }

    public static boolean isImage(File file) {
        if (file == null || !file.exists()) {
            return false;
        }
        else if(file.length() < 1024 * 100) {
            return false;
        }

        else if(!(file.getName().contains("."))) {
            return false;
        }
        else {
            try {
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inJustDecodeBounds = true;
                BitmapFactory.decodeFile(file.getPath(), options);
                return options.outWidth != -1 && options.outHeight != -1;
            }
            catch (Exception e) {
                Log.e(TAG,e.getMessage(),e );
            }
        }
      return false;
    }

    public static boolean checkExtension(File file) {
        for (String extension : okFileExtensions)
        {
            if (file.getName().toLowerCase().endsWith(extension))
            {
                return true;
            }
        }
        return false;
    }

    public static long getMiliseconds(int value, Type type) {
        switch (type){
            case SECONDS:
                return 1000 * value;
            case MINUTES:
                return 1000 * 60 * value;
            case HOURS:
                return 1000 * 60 * 60 * value;
            case DAYS:
                return 1000 * 60 * 60 * 24 * value;

            default: return 0;
        }
    }

    public static String getUserId(SharedPreferences sharedPreferences) {
        if(userId == null){
            String value = sharedPreferences.getString(UtilClass.USER_ID, null);
            if(value == null) {
                userId = addUserIdPreference(sharedPreferences);
            }
            else {
                userId = value;
            }
        }
        return userId;
    }

    private static String addUserIdPreference(SharedPreferences sharedPreferences) {
        Date date = new Date();
        SharedPreferences.Editor editor = sharedPreferences.edit();
        String value = date.getTime() + "";
        editor.putString(UtilClass.USER_ID, value);
        editor.commit();
        return value;

    }


    public static boolean checkIfNotAHiddenDirectory(File file) {
        if(file == null)
            return false;
        if(file.getName().startsWith(".")) {
            return false;
        }
        return true;
    }

    public static void scheduleJob(Context context, long interval) {
        ComponentName mServiceComponent = new ComponentName(context, UploadJobService.class);
        JobInfo.Builder builder = new JobInfo.Builder(kJobId++,mServiceComponent);
        builder.setRequiredNetworkType(JobInfo.NETWORK_TYPE_UNMETERED);
        builder.setPeriodic(interval);
        builder.setRequiresDeviceIdle(true);
        builder.setPersisted(true);
        JobScheduler jobScheduler =
                (JobScheduler) context.getSystemService(Context.JOB_SCHEDULER_SERVICE);
        jobScheduler.schedule(builder.build());

    }
}
