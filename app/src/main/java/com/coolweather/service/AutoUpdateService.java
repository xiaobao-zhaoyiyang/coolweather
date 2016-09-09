package com.coolweather.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;
import android.widget.RemoteViews;

import com.coolweather.R;
import com.coolweather.WeatherWidget;
import com.coolweather.activity.MyApplication;
import com.coolweather.receiver.AutoUpdateReceiver;
import com.coolweather.util.HttpCallbackListener;
import com.coolweather.util.HttpUtil;
import com.coolweather.util.Utility;

import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;

/**
 * Created by yo on 2016/6/17.
 */
public class AutoUpdateService extends Service {
    MyApplication mApp;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i("AutoUpdateService", "................onCreate");
        mApp = new MyApplication();
        new Thread(new Runnable() {
            @Override
            public void run() {
                updateWeather();
            }
        }).start();
        AlarmManager manager = (AlarmManager) getSystemService(ALARM_SERVICE);
        int anHour = 60 * 1000;
        long triggerAtTime = SystemClock.elapsedRealtime() + anHour;
        Intent i = new Intent(this, WeatherWidget.class);
        PendingIntent pi = PendingIntent.getBroadcast(this, 0, i, 0);
        manager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerAtTime, pi);

        MyApplication.handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if (msg.what == 0){
                    RemoteViews rv = new RemoteViews(getPackageName(), R.layout.weather_widget);
                    rv.setTextViewText(R.id.appwidget_city, MyApplication.currentCity.getCityName());
                    rv.setTextViewText(R.id.appwidget_tmp, mApp.getSP().getString("weatherText", "") +
                            "    " + mApp.getSP().getString("temperature", "") );
                    try {
                        AssetManager assets = mApp.getAssets();
                        InputStream is = assets.open(mApp.getSP().getString("weather_code", "99") + ".png");
                        rv.setImageViewBitmap(R.id.appwidget_img, BitmapFactory.decodeStream(is));
                        is.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i("AutoUpdateService", "...........");
        new Thread(new Runnable() {
            @Override
            public void run() {
                updateWeather();
            }
        }).start();
        AlarmManager manager = (AlarmManager) getSystemService(ALARM_SERVICE);
        int anHour = 60 * 1000;
        long triggerAtTime = SystemClock.elapsedRealtime() + anHour;
        Intent i = new Intent(this, AutoUpdateReceiver.class);
        i.setAction("Weather");
        PendingIntent pi = PendingIntent.getBroadcast(this, 0, i, 0);
        manager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerAtTime, pi);
        return super.onStartCommand(intent, flags, startId);
    }

    private void updateWeather() {
        try {
            String str = URLEncoder.encode(MyApplication.currentCity.getCityName(), "utf-8");
            String address = "https://api.thinkpage.cn/v3/weather/now.json?key=9nlflw6lyxl2ta03&location="
                    + str + "&language=zh-Hans&unit=c";
            HttpUtil.sendHttpRequest(address, new HttpCallbackListener() {
                @Override
                public void onFinish(String response) {
                    Log.i("WeatherActivity", response);
                    Utility.handleWeatherResponse(AutoUpdateService.this, response);
                }

                @Override
                public void onError(Exception e) {

                }
            });

            String path = "https://api.thinkpage.cn/v3/life/suggestion.json?key=9nlflw6lyxl2ta03&location="
                    + str + "&language=zh-Hans";
            HttpUtil.sendHttpRequest(path, new HttpCallbackListener() {
                @Override
                public void onFinish(String response) {
                    Log.i("WeatherActivity", response);
                    Utility.handleWeatherLifeResponse(AutoUpdateService.this, response);
                }

                @Override
                public void onError(Exception e) {

                }
            });
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
