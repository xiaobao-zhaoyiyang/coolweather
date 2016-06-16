package com.coolweather.activity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.widget.TextView;

import com.coolweather.R;
import com.coolweather.util.HttpCallbackListener;
import com.coolweather.util.HttpUtil;
import com.coolweather.util.Utility;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * Created by yo on 2016/6/16.
 */
public class WeatherActivity extends AppCompatActivity {
    private String cityCode;
    private TextView tv_title, tv_publishTime, tv_temp, tv_weather;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.weather_layout);
        cityCode = getIntent().getStringExtra("city_code");
        initView();
    }

    private void initView() {
        tv_title = (TextView) findViewById(R.id.title_text);
        tv_publishTime = (TextView) findViewById(R.id.temperature_text);
        tv_temp = (TextView) findViewById(R.id.temperature_text);
        tv_weather = (TextView) findViewById(R.id.weather_text);
    }

    private void queryFromServer(String name){
        if (!TextUtils.isEmpty(name)){
            try {
                String str = URLEncoder.encode(name, "utf-8");
                String address = "https://api.thinkpage.cn/v3/weather/now.json?key=9nlflw6lyxl2ta03&location="
                        + str + "&language=zh-Hans&unit=c";
                HttpUtil.sendHttpRequest(address, new HttpCallbackListener() {
                    @Override
                    public void onFinish(String response) {
                        Utility.handleWeatherResponse(WeatherActivity.this, response);
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

    }
}
