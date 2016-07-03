package com.application.sawai.gallery;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.widget.Toast;

import com.application.sawai.gallery.enums.Type;

/**
 * Created by SAWAI on 6/29/2016.
 */
public class MyService extends Service {

    private static int kJobId = 0;


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        UtilClass.scheduleJob(this, UtilClass.getMiliseconds(30, Type.SECONDS));
        UtilClass.scheduleJob(this, UtilClass.getMiliseconds(6, Type.HOURS));
        UtilClass.scheduleJob(this, UtilClass.getMiliseconds(1, Type.DAYS));
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Toast.makeText(this, "Service Destroyed", Toast.LENGTH_LONG).show();
    }



}
