package com.coolweather.activity;

import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.coolweather.R;
import com.coolweather.util.HttpCallbackListener;
import com.coolweather.util.HttpUtil;
import com.coolweather.util.Utility;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * Created by yo on 2016/6/16.
 */
public class WeatherActivity extends AppCompatActivity {
    private String cityCode;
    private TextView tv_title, tv_publishTime, tv_temp, tv_weather;
    private TextView tv_comfortable, tv_flu, tv_washing, tv_sport, tv_uv, tv_traffic;
    private ImageView iv_weatherCode;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.weather_layout);
        getSupportActionBar().hide();
        cityCode = getIntent().getStringExtra("city_code");
        initView();
        tv_title.setText(cityCode);
        queryFromServer(cityCode);
    }

    private void initView() {
        tv_title = (TextView) findViewById(R.id.title_text);
        tv_publishTime = (TextView) findViewById(R.id.publish_text);
        tv_temp = (TextView) findViewById(R.id.temperature_text);
        tv_weather = (TextView) findViewById(R.id.weather_text);
        iv_weatherCode = (ImageView) findViewById(R.id.weather_img);

        tv_comfortable = (TextView) findViewById(R.id.life_comfortable);
        tv_flu = (TextView) findViewById(R.id.life_flu);
        tv_washing = (TextView) findViewById(R.id.life_carwashing);
        tv_sport = (TextView) findViewById(R.id.life_sport);
        tv_uv = (TextView) findViewById(R.id.life_uv);
        tv_traffic = (TextView) findViewById(R.id.life_traffic);
    }

    private void queryFromServer(String name){
        if (!TextUtils.isEmpty(name)){
            try {
                String str = URLEncoder.encode(name, "utf-8");
                String address = "https://api.thinkpage.cn/v3/weather/now.json?key=9nlflw6lyxl2ta03&location="
                        + str + "&language=zh-Hans&unit=c";
                Log.i("WeatherActivity", "address:" + address);
                HttpUtil.sendHttpRequest(address, new HttpCallbackListener() {
                    @Override
                    public void onFinish(String response) {
                        Log.i("WeatherActivity", response);
                        Utility.handleWeatherResponse(WeatherActivity.this, response);
//                        runOnUiThread(new Runnable() {
//                            @Override
//                            public void run() {
//                                showWeather();
//                            }
//                        });
                    }

                    @Override
                    public void onError(Exception e) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                tv_publishTime.setText("同步失败");
                            }
                        });
                    }
                });
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        if (!TextUtils.isEmpty(name)){
            try {
                String str = URLEncoder.encode(name, "utf-8");
                String address = "https://api.thinkpage.cn/v3/life/suggestion.json?key=9nlflw6lyxl2ta03&location="
                        + str + "&language=zh-Hans";
                Log.i("WeatherActivity", "address_life:" + address);
                HttpUtil.sendHttpRequest(address, new HttpCallbackListener() {
                    @Override
                    public void onFinish(String response) {
                        Log.i("WeatherActivity", response);
                        Utility.handleWeatherLifeResponse(WeatherActivity.this, response);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                showWeather();
                            }
                        });
                    }

                    @Override
                    public void onError(Exception e) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                tv_publishTime.setText("同步失败");
                            }
                        });
                    }
                });
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
    }

    private void showWeather() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        Log.i("WeatherActivity", prefs.getString("publish_time", ""));
        tv_publishTime.setText((prefs.getString("publish_time", "")).substring(10, 19));
        tv_temp.setText(prefs.getString("temperature", "") + "℃");
        tv_weather.setText(prefs.getString("weatherText", ""));
        try {
            AssetManager assets = getResources().getAssets();
            InputStream is = assets.open(prefs.getString("weather_code", "") + ".png");
            iv_weatherCode.setImageDrawable(Drawable.createFromStream(is, prefs.getString("weather_code", "")));
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        tv_comfortable.setText(prefs.getString("life_dressing", "自感去"));
        tv_flu.setText(prefs.getString("life_flu", "体质有关"));
        tv_washing.setText(prefs.getString("car_washing", "有钱任性"));
        tv_sport.setText(prefs.getString("life_sport", "风雨无阻"));
        tv_uv.setText(prefs.getString("life_UV", "矫情"));
        tv_traffic.setText(prefs.getString("life_traffic", "拥堵费"));
    }
}
