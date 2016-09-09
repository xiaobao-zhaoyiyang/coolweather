package com.coolweather.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.coolweather.activity.MyApplication;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by yo on 2016/6/16.
 */
public class Utility {
    public static void handleWeatherResponse(Context context, String response){
        try {
            JSONObject object = new JSONObject(response);
            JSONArray array = object.getJSONArray("results");
            for (int i = 0; i < array.length(); i++) {
                JSONObject results = array.getJSONObject(i);
                String publishTime = results.optString("last_update");
                Log.i("Utility", "publish_time:" + publishTime);
                JSONObject now = results.getJSONObject("now");
                String temperature = now.getString("temperature");
                String weatherText = now.getString("text");
                String weatherCode = now.getString("code");
                String cityName = results.getJSONObject("location").getString("name");
                String cityCode = results.getJSONObject("location").getString("id");
                saveWeatherInfo(context, cityCode, cityName, temperature, weatherText, publishTime, weatherCode);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static void handleWeatherLifeResponse(Context context, String response){
        try {
            JSONObject object = new JSONObject(response);
            JSONArray array = object.getJSONArray("results");
            for (int i = 0; i < array.length(); i++) {
                JSONObject results = array.getJSONObject(i);
                JSONObject suggestion = results.getJSONObject("suggestion");
                String car_washing = suggestion.getJSONObject("car_washing").getString("brief");
                String dressing = suggestion.getJSONObject("dressing").getString("brief");
                String flu = suggestion.getJSONObject("flu").getString("brief");
                String sport = suggestion.getJSONObject("sport").getString("brief");
                String travel = suggestion.getJSONObject("travel").getString("brief");
                String uv = suggestion.getJSONObject("uv").getString("brief");
                String cityName = results.getJSONObject("location").getString("name");
                String cityCode = results.getJSONObject("location").getString("id");
                saveWeatherLifeInfo(context, cityCode, car_washing, dressing, flu, sport, travel, uv);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private static void saveWeatherInfo(Context context, String cityCode,
              String cityName, String temperature,
              String weatherText, String publishTime, String weatherCode) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy年M月d日", Locale.CHINA);
        SharedPreferences sp = context.getSharedPreferences(cityCode + "_1", Context.MODE_PRIVATE);
//        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean("city_selected", true);
        editor.putString("city_name", cityName);
        editor.putString("temperature", temperature);
        editor.putString("weatherText", weatherText);
        editor.putString("publish_time", publishTime);
        editor.putString("weather_code", weatherCode);
        editor.putString("current_data", sdf.format(new Date()));
        editor.commit();
        MyApplication.handler.sendEmptyMessage(0);
    }
    private static void saveWeatherLifeInfo(Context context, String cityCode, String carWashing, String lifeDressing,
                                            String lifeFlu, String lifeSport, String lifeTraffic, String lifeUV) {
        SharedPreferences sp = context.getSharedPreferences(cityCode + "_2", Context.MODE_PRIVATE);
//        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("car_washing", carWashing);
        editor.putString("life_dressing", lifeDressing);
        editor.putString("life_flu", lifeFlu);
        editor.putString("life_sport", lifeSport);
        editor.putString("life_traffic", lifeTraffic);
        editor.putString("life_UV", lifeUV);
        editor.commit();
    }
}
