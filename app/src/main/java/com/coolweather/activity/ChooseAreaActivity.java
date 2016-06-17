package com.coolweather.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.coolweather.R;
import com.coolweather.db.CoolWeatherDB;
import com.coolweather.model.City;
import com.coolweather.model.Province;
import com.coolweather.util.HttpCallbackListener;
import com.coolweather.util.HttpUtil;
import com.thinkpage.lib.api.TPCity;
import com.thinkpage.lib.api.TPCityInformation;
import com.thinkpage.lib.api.TPListeners;
import com.thinkpage.lib.api.TPWeatherManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by yo on 2016/6/16.
 */
public class ChooseAreaActivity extends AppCompatActivity {
    public static final int LEVEL_PROVINCE = 0;
    public static final int LEVEL_CITY = 1;
    private ProgressDialog progressDialog;
    private TextView titleText;
    private ListView listView;
    private List<Province> list_province;
    private List<City> list_city;
    private List<String> dataList = new ArrayList<>();
    private ArrayAdapter<String> adapter;
    private CoolWeatherDB db;
    private Province selectedProvince; // 当前选中的省份
    private City selectedCity; // 当前选中的城市
    private int currentLevel; // 当前选中的级别

    private boolean isFromWeatherActivity;// 是否从WeatherActivity跳过来
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.choose_area);
        getSupportActionBar().hide();

        isFromWeatherActivity = getIntent().getBooleanExtra("from_weather_activity", false);
        titleText = (TextView) findViewById(R.id.title_text);
        listView = (ListView) findViewById(R.id.list_view);
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, dataList);
        listView.setAdapter(adapter);
        db = CoolWeatherDB.getInstance(this);
        queryProvince();
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (currentLevel == LEVEL_PROVINCE){
                    selectedProvince = list_province.get(position);
                    queryCities();
                }else if (currentLevel == LEVEL_CITY){
                    selectedCity = list_city.get(position);
                    MyApplication.currentCity = selectedCity;
                    Intent intent = new Intent(ChooseAreaActivity.this,
                            WeatherActivity.class);
                    intent.putExtra("city_code", selectedCity.getCityCode());
                    intent.putExtra("city_name", selectedCity.getCityName());
                    startActivity(intent);
                    if (isFromWeatherActivity){
                        finish();
                    }
                }
            }
        });
    }

    /**
     * 查询全国所有的省
     */
    private void queryProvince(){
        list_province = db.loadProvince();
        Log.i("list_province", "list_province：" + list_province.size());
        if (list_province.size() > 0){
            dataList.clear();
            for (Province province : list_province) {
                dataList.add(province.getProvinceName());
            }
            Log.i("dataList", "dataList数据：" + dataList.size());
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            titleText.setText("中国");
            currentLevel = LEVEL_PROVINCE;
        }
    }

    /**
     * 查询点击省份下的所有城市
     */
    private void queryCities() {
        list_city = db.loadCities(selectedProvince.getProvinceCode());
        if (list_city.size() > 0){
            dataList.clear();
            for (City city : list_city) {
                dataList.add(city.getCityName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            titleText.setText(selectedProvince.getProvinceName());
            currentLevel = LEVEL_CITY;
        }else {
//            queryFromServer(selectedProvince.getProvinceCode());
            fromServer(selectedProvince.getProvinceName());
        }
    }

    private  void fromServer(String code){
        String str = code.substring(0, code.length() - 1);
        Log.i("Code", code + ", " + str);
        String address = null;
        if (!TextUtils.isEmpty(code)){
            try {
                String path = URLEncoder.encode(str, "utf-8");
                address = "https://api.thinkpage.cn/v3/location/search.json?key=9nlflw6lyxl2ta03&q=" + path;
                Log.i("path", path);
                showProgressDialog();
                HttpUtil.sendHttpRequest(address, new HttpCallbackListener() {
                    @Override
                    public void onFinish(String response) {
                        Log.i("Response", response);
                        try {
                            JSONObject object = new JSONObject(response);
                            JSONArray array = object.getJSONArray("results");
                            for (int i = 0; i < array.length(); i++) {
                                JSONObject result = array.getJSONObject(i);
                                City city = new City();
                                city.setCityName(result.getString("name"));
                                city.setCityCode(result.getString("id"));
                                city.setProvinceCode(selectedProvince.getProvinceCode());
                                db.saveCity(city);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                closeProgressDialog();
                                queryCities();
                            }
                        });
                    }

                    @Override
                    public void onError(Exception e) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                closeProgressDialog();
                                Toast.makeText(ChooseAreaActivity.this, "加载失败", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
    }

    private void queryFromServer(final String code) {
        showProgressDialog();
        TPWeatherManager weatherManager = TPWeatherManager.sharedWeatherManager();
        weatherManager.initWithKeyAndUserId("9nlflw6lyxl2ta03","U90698F7A1");
        weatherManager.getCityInformation(new TPCity(code)
                , TPWeatherManager.TPWeatherReportLanguage.kSimplifiedChinese
                , 200
                , 0
                , new TPListeners.TPCityInformationListener(){

                    @Override
                    public void onCityInformationAvailable(TPCityInformation[] tpCityInformations, String s) {
                        Log.i("TPCityInformation", tpCityInformations.length + "");
                        for (int i = 0; i < tpCityInformations.length; i++) {
                            Log.i("TPCityInformation", "tpCityInformations_" + i + ":" + tpCityInformations[i].name
                                    + ", " + tpCityInformations[i].cityid + ", " + tpCityInformations[i].countryCode);
                            City city = new City();
                            city.setProvinceCode(code);
                            city.setCityName(tpCityInformations[i].name);
                            city.setCityCode(tpCityInformations[i].cityid);
                            db.saveCity(city);
                        }
                        closeProgressDialog();
                        queryCities();
                    }
                });

    }

    private void closeProgressDialog() {
        if (progressDialog != null){
            progressDialog.dismiss();
        }
    }

    private void showProgressDialog() {
        if (progressDialog == null){
            progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("正在加载...");
            progressDialog.setCanceledOnTouchOutside(false);
        }
        progressDialog.show();
    }

    @Override
    public void onBackPressed() {
        if (currentLevel == LEVEL_CITY){
            queryProvince();
        }else if (currentLevel == LEVEL_PROVINCE){
            finish();
        }
    }
}
