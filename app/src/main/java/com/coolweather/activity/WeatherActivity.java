package com.coolweather.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.coolweather.R;
import com.coolweather.service.AutoUpdateService;
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
public class WeatherActivity extends AppCompatActivity implements View.OnClickListener{
    private String cityName, cityCode;
    private TextView tv_title, tv_publishTime, tv_temp, tv_weather;
    private TextView tv_comfortable, tv_flu, tv_washing, tv_sport, tv_uv, tv_traffic;
    private ImageView iv_weatherCode;
    private Button bt_home, bt_refresh;
    private boolean isLoading; // 是否获取数据
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.weather_layout);
        getSupportActionBar().hide();
        cityName = getIntent().getStringExtra("city_name");
        cityCode = getIntent().getStringExtra("city_code");
        initView();
        tv_title.setText(cityName);
        showWeather();
        showLife();
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

        bt_home = (Button) findViewById(R.id.title_switch_city);
        bt_refresh = (Button) findViewById(R.id.title_refresh_weather);
        bt_home.setVisibility(View.VISIBLE);
        bt_refresh.setVisibility(View.VISIBLE);
        bt_home.setOnClickListener(this);
        bt_refresh.setOnClickListener(this);
    }

    private void queryFromServer(String name){
        isLoading = false;
        if (!TextUtils.isEmpty(name)){
            try {
                String str = URLEncoder.encode(name, "utf-8");
                String address = "https://api.thinkpage.cn/v3/weather/now.json?key=9nlflw6lyxl2ta03&location="
                        + str + "&language=zh-Hans&unit=c";
                Log.i("WeatherActivity", "address:" + address);
                tv_publishTime.setText("同步中...");
                HttpUtil.sendHttpRequest(address, new HttpCallbackListener() {
                    @Override
                    public void onFinish(String response) {
                        Log.i("WeatherActivity", response);
                        Utility.handleWeatherResponse(WeatherActivity.this, response);
                        isLoading = true;
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

    private void queryLifeFromServer(String name){
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
                                showLife();
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
        SharedPreferences prefs_1 = getSharedPreferences(cityCode + "_1", Context.MODE_PRIVATE);
        if (!TextUtils.isEmpty(prefs_1.getString("publish_time", ""))){
            tv_publishTime.setText("发布时间:" +(prefs_1.getString("publish_time", "")).substring(10, 19));
        }else{
            queryFromServer(cityName);
        }
        tv_temp.setText(prefs_1.getString("temperature", "0") + "℃");
        tv_weather.setText(prefs_1.getString("weatherText", "未知"));
        try {
            AssetManager assets = getResources().getAssets();
            InputStream is = assets.open(prefs_1.getString("weather_code", "99") + ".png");
            iv_weatherCode.setImageDrawable(Drawable.createFromStream(is, prefs_1.getString("weather_code", "")));
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (isLoading) {
            queryLifeFromServer(cityName);
        }
    }

    private void showLife() {
        SharedPreferences prefs_2 = getSharedPreferences(cityCode + "_2", Context.MODE_PRIVATE);
        tv_comfortable.setText(prefs_2.getString("life_dressing", "自感去"));
        tv_flu.setText(prefs_2.getString("life_flu", "体质有关"));
        tv_washing.setText(prefs_2.getString("car_washing", "有钱任性"));
        tv_sport.setText(prefs_2.getString("life_sport", "风雨无阻"));
        tv_uv.setText(prefs_2.getString("life_UV", "矫情"));
        tv_traffic.setText(prefs_2.getString("life_traffic", "拥堵费"));

        Intent intent = new Intent(this, AutoUpdateService.class);
        startService(intent);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.title_switch_city:
                Intent intent = new Intent(this, ChooseAreaActivity.class);
                intent.putExtra("from_weather_activity", true);
                startActivity(intent);
                break;
            case R.id.title_refresh_weather:
                tv_publishTime.setText("同步中...");
                queryFromServer(cityCode);
                break;
        }
    }
}
