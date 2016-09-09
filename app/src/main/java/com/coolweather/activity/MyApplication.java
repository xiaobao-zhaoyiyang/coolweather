package com.coolweather.activity;

import android.app.Application;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.os.Handler;

import com.coolweather.model.City;

/**
 * Created by yo on 2016/6/17.
 */
public class MyApplication extends Application {
    public static City currentCity;
    public static Handler handler;

    public SharedPreferences getSP(){
        SharedPreferences spf = getSharedPreferences("", MODE_PRIVATE);
        return spf;
    }

    public AssetManager getAssetsManager(){
        AssetManager assets = getAssets();
        return assets;
    }
}
