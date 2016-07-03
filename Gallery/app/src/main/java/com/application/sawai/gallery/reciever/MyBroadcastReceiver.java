package com.application.sawai.gallery.reciever;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.application.sawai.gallery.UtilClass;
import com.application.sawai.gallery.enums.Type;

public class MyBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        UtilClass.scheduleJob(context, UtilClass.getMiliseconds(10 , Type.MINUTES));
    }
}