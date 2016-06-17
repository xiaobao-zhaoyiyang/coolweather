package com.coolweather.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.coolweather.service.AutoUpdateService;

/**
 * Created by yo on 2016/6/17.
 */
public class AutoUpdateReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (!intent.getAction().equals("Weather"))
            return;
        Intent i = new Intent(context, AutoUpdateService.class);
        context.startService(i);
    }
}
