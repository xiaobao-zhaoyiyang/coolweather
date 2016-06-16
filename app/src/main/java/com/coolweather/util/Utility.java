package com.coolweather.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

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
                JSONObject now = results.getJSONObject("now");
                String temperature = now.getString("temperature");
                String weatherText = now.getString("text");
                String cityName = results.getJSONObject("location").getString("name");
                saveWeatherInfo(context, cityName, temperature, weatherText, publishTime);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private static void saveWeatherInfo(Context context,
              String cityName, String temperature,
              String weatherText, String publishTime) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy年M月d日", Locale.CHINA);
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
        editor.putBoolean("city_selected", true);
        editor.putString("city_name", cityName);
        editor.putString("temperature", temperature);
        editor.putString("weatherText", weatherText);
        editor.putString("publish_time", publishTime);
        editor.putString("current_data", sdf.format(new Date()));
        editor.commit();
    }
}
