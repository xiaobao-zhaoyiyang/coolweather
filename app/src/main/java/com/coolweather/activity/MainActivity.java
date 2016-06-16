package com.coolweather.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.coolweather.R;
import com.coolweather.db.CoolWeatherDB;
import com.coolweather.model.Province;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by yo on 2016/6/15.
 */
public class MainActivity extends AppCompatActivity {
    private CoolWeatherDB db;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();
        db = CoolWeatherDB.getInstance(this);
        List<Province>  list_province = db.loadProvince();
        if (list_province.size() == 0){
            saveData();
        }
        Timer timer = new Timer();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                Intent intent = new Intent(MainActivity.this,
                                        ChooseAreaActivity.class);
                startActivity(intent);
                MainActivity.this.finish();
            }
        };
        timer.schedule(task, 3000);
    }

    /**
     * 保存所有省份信息
     */
    private void saveData(){
        // 4个直辖市：北京市 天津市 上海市 重庆市
        Province province1 = new Province();
        province1.setProvinceName("北京市");
        province1.setProvinceCode("beijing");
        db.saveProvince(province1);
        Province province2 = new Province();
        province2.setProvinceName("天津市");
        province2.setProvinceCode("tianjin");
        db.saveProvince(province2);
        Province province3 = new Province();
        province3.setProvinceName("上海市");
        province3.setProvinceCode("shanghai");
        db.saveProvince(province3);
        Province province4 = new Province();
        province4.setProvinceName("重庆市");
        province4.setProvinceCode("chongqing");
        db.saveProvince(province4);

        //23个省:河北省 山西省 辽宁省 吉林省 黑龙江省 江苏省 浙江省 安徽省 福建省 江西省 山东省
        // 河南省 湖北省 湖南省 广东省 海南省 四川省 贵州省 云南省 陕西省 甘肃省 青海省 台湾省
        List<String> list_provinceName = new ArrayList<>();
        List<String> list_provinceCode = new ArrayList<>();
        list_provinceName.add("河北省");
        list_provinceCode.add("hebei");
        list_provinceName.add("山西省");
        list_provinceCode.add("shanxi");
        list_provinceName.add("辽宁省");
        list_provinceCode.add("liaoning");
        list_provinceName.add("吉林省");
        list_provinceCode.add("jilin");
        list_provinceName.add("黑龙江省");
        list_provinceCode.add("");
        list_provinceName.add("江苏省");
        list_provinceCode.add("jiangsu");
        list_provinceName.add("浙江省");
        list_provinceCode.add("zhejiang");
        list_provinceName.add("安徽省");
        list_provinceCode.add("anhui");
        list_provinceName.add("福建省");
        list_provinceCode.add("fujian");
        list_provinceName.add("江西省");
        list_provinceCode.add("jiangxi");
        list_provinceName.add("山东省");
        list_provinceCode.add("shandong");
        list_provinceName.add("河南省");
        list_provinceCode.add("henan");
        list_provinceName.add("湖北省");
        list_provinceCode.add("hubei");
        list_provinceName.add("湖南省");
        list_provinceCode.add("hunan");
        list_provinceName.add("广东省");
        list_provinceCode.add("guangdong");
        list_provinceName.add("海南省");
        list_provinceCode.add("hainan");
        list_provinceName.add("四川省");
        list_provinceCode.add("sichuan");
        list_provinceName.add("贵州省");
        list_provinceCode.add("guizhou");
        list_provinceName.add("云南省");
        list_provinceCode.add("yunnan");
        list_provinceName.add("陕西省");
        list_provinceCode.add("shanxi");
        list_provinceName.add("甘肃省");
        list_provinceCode.add("gansu");
        list_provinceName.add("青海省");
        list_provinceCode.add("qinghai");
        list_provinceName.add("台湾省");
        list_provinceCode.add("taiwan");
        for (int i = 0; i < list_provinceCode.size(); i++) {
            Province province_i = new Province();
            province_i.setProvinceName(list_provinceName.get(i));
            province_i.setProvinceCode(list_provinceCode.get(i));
            db.saveProvince(province_i);
        }
        // 5个自治区： 广西壮族自治区 内蒙古自治区 西藏自治区 宁夏回族自治区 新疆维吾尔自治区
        Province province5 = new Province();
        province5.setProvinceName("广西壮族自治区");
        province5.setProvinceCode("guangxi");
        db.saveProvince(province5);
        Province province6 = new Province();
        province6.setProvinceName("内蒙古自治区");
        province6.setProvinceCode("neimenggu");
        db.saveProvince(province6);
        Province province7 = new Province();
        province7.setProvinceName("西藏自治区");
        province7.setProvinceCode("xizang");
        db.saveProvince(province7);
        Province province8 = new Province();
        province8.setProvinceName("宁夏回族自治区");
        province8.setProvinceCode("ningxia");
        db.saveProvince(province8);
        Province province9 = new Province();
        province9.setProvinceName("新疆维吾尔自治区");
        province9.setProvinceCode("xinjiang");
        db.saveProvince(province9);

        // 2个特别行政区:香港特别行政区、澳门特别行政区
        Province province10 = new Province();
        province10.setProvinceName("香港特别行政区");
        province10.setProvinceCode("xianggang");
        db.saveProvince(province10);
        Province province11 = new Province();
        province11.setProvinceName("澳门特别行政区");
        province11.setProvinceCode("aomen");
        db.saveProvince(province11);
    }

}
